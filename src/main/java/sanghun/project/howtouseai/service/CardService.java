package sanghun.project.howtouseai.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sanghun.project.howtouseai.domain.Card;
import sanghun.project.howtouseai.domain.Category;
import sanghun.project.howtouseai.dto.CardCreateRequest;
import sanghun.project.howtouseai.dto.CardResponse;
import sanghun.project.howtouseai.dto.CardUpdateRequest;
import sanghun.project.howtouseai.dto.CategoryResponse;
import sanghun.project.howtouseai.exception.CardAlreadyExistsException;
import sanghun.project.howtouseai.exception.CardNotFoundException;
import sanghun.project.howtouseai.exception.CategoryNotFoundException;
import sanghun.project.howtouseai.exception.UnauthorizedAccessException;
import sanghun.project.howtouseai.repository.CardLikeRepository;
import sanghun.project.howtouseai.repository.CardRepository;
import sanghun.project.howtouseai.repository.CategoryRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CardService {

    private final CardRepository cardRepository;
    private final CategoryRepository categoryRepository;
    private final CardLikeRepository cardLikeRepository;

    public Page<CardResponse> getAllCards(Pageable pageable) {
        log.info("모든 카드 조회 요청: page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        
        Page<Card> cards = cardRepository.findAllByOrderByLikesCountDescCreatedAtDesc(pageable);
        log.info("카드 조회 완료: totalElements={}, totalPages={}", cards.getTotalElements(), cards.getTotalPages());
        
        return cards.map(this::convertToResponse);
    }

    public CardResponse getCardById(Long cardId) {
        log.info("카드 상세 조회 요청: cardId={}", cardId);
        
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> {
                    log.warn("존재하지 않는 카드 조회 시도: cardId={}", cardId);
                    return new CardNotFoundException(
                        String.format("카드를 찾을 수 없습니다: ID %d", cardId)
                    );
                });
        
        log.info("카드 상세 조회 완료: id={}, title={}", card.getId(), card.getTitle());
        
        return convertToResponse(card);
    }

    @Transactional
    public CardResponse createCard(CardCreateRequest request) {
        log.info("카드 생성 요청: title={}, categoryId={}, uuid={}", 
                request.getTitle(), request.getCategoryId(), request.getUuid());

        // 카테고리 존재 여부 확인
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> {
                    log.warn("존재하지 않는 카테고리로 카드 생성 시도: categoryId={}", request.getCategoryId());
                    return new CategoryNotFoundException(
                        String.format("카테고리를 찾을 수 없습니다: ID %d", request.getCategoryId())
                    );
                });

        // 중복 카드 제목 체크 (같은 사용자 내에서)
        if (cardRepository.existsByTitleAndUuid(request.getTitle(), request.getUuid())) {
            log.warn("중복된 카드 제목으로 생성 시도: title={}, uuid={}", request.getTitle(), request.getUuid());
            throw new CardAlreadyExistsException(
                String.format("이미 존재하는 카드 제목입니다: %s", request.getTitle())
            );
        }

        // 카드 엔티티 생성
        Card card = Card.builder()
                .uuid(request.getUuid())
                .title(request.getTitle())
                .category(category)
                .tags(request.getTags())
                .situation(request.getSituation())
                .usageExamples(request.getUsageExamples())
                .content(request.getContent())
                .build();

        // 저장
        Card savedCard = cardRepository.save(card);
        log.info("카드 생성 완료: id={}, title={}", savedCard.getId(), savedCard.getTitle());

        // 응답 DTO로 변환
        return convertToResponse(savedCard);
    }

    @Transactional
    public CardResponse updateCard(Long cardId, CardUpdateRequest request) {
        log.info("카드 수정 요청: cardId={}, title={}, uuid={}", 
                cardId, request.getTitle(), request.getUuid());

        // 카드 존재 여부 확인
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> {
                    log.warn("존재하지 않는 카드 수정 시도: cardId={}", cardId);
                    return new CardNotFoundException(
                        String.format("카드를 찾을 수 없습니다: ID %d", cardId)
                    );
                });

        // 권한 확인 (UUID가 일치하는지)
        if (!card.getUuid().equals(request.getUuid())) {
            log.warn("권한 없는 사용자의 카드 수정 시도: cardId={}, requestUuid={}, cardUuid={}", 
                    cardId, request.getUuid(), card.getUuid());
            throw new UnauthorizedAccessException(
                String.format("카드 수정 권한이 없습니다: ID %d", cardId)
            );
        }

        String newTitle = request.getTitle();
        Long newCategoryId = request.getCategoryId();
        Category newCategory = card.getCategory();

        // 제목 업데이트 처리
        if (newTitle != null && !newTitle.trim().isEmpty()) {
            // 중복 카드 제목 체크 (자신 제외, 같은 사용자 내에서)
            if (!card.getTitle().equals(newTitle) && 
                cardRepository.existsByTitleAndUuid(newTitle, request.getUuid())) {
                log.warn("중복된 카드 제목으로 수정 시도: title={}, uuid={}", newTitle, request.getUuid());
                throw new CardAlreadyExistsException(
                    String.format("이미 존재하는 카드 제목입니다: %s", newTitle)
                );
            }
        } else {
            newTitle = card.getTitle(); // 기존 제목 유지
        }

        // 카테고리 업데이트 처리
        if (newCategoryId != null && !newCategoryId.equals(card.getCategory().getId())) {
            newCategory = categoryRepository.findById(newCategoryId)
                    .orElseThrow(() -> {
                        log.warn("존재하지 않는 카테고리로 카드 수정 시도: categoryId={}", newCategoryId);
                        return new CategoryNotFoundException(
                            String.format("카테고리를 찾을 수 없습니다: ID %d", newCategoryId)
                        );
                    });
        }

        // 카드 정보 업데이트
        card.updateInfo(newTitle, newCategory, request.getTags(), 
                       request.getSituation(), request.getUsageExamples(), request.getContent());
        
        // 저장
        Card updatedCard = cardRepository.save(card);
        log.info("카드 수정 완료: id={}, title={}", updatedCard.getId(), updatedCard.getTitle());

        // 응답 DTO로 변환
        return convertToResponse(updatedCard);
    }

    @Transactional
    public void deleteCard(Long cardId, String uuid) {
        log.info("카드 삭제 요청: cardId={}, uuid={}", cardId, uuid);

        // 카드 존재 여부 확인
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> {
                    log.warn("존재하지 않는 카드 삭제 시도: cardId={}", cardId);
                    return new CardNotFoundException(
                        String.format("카드를 찾을 수 없습니다: ID %d", cardId)
                    );
                });

        // 권한 확인 (UUID가 일치하는지)
        if (!card.getUuid().equals(uuid)) {
            log.warn("권한 없는 사용자의 카드 삭제 시도: cardId={}, requestUuid={}, cardUuid={}", 
                    cardId, uuid, card.getUuid());
            throw new UnauthorizedAccessException(
                String.format("카드 삭제 권한이 없습니다: ID %d", cardId)
            );
        }

        // 관련된 좋아요 데이터 먼저 삭제
        log.info("카드 관련 좋아요 데이터 삭제 시작: cardId={}", cardId);
        cardLikeRepository.deleteByCardId(cardId);
        log.info("카드 관련 좋아요 데이터 삭제 완료: cardId={}", cardId);

        // 카드 삭제
        cardRepository.delete(card);
        log.info("카드 삭제 완료: id={}, title={}", cardId, card.getTitle());
    }

    private CardResponse convertToResponse(Card card) {
        CategoryResponse categoryResponse = CategoryResponse.builder()
                .id(card.getCategory().getId())
                .name(card.getCategory().getName())
                .iconUrl(card.getCategory().getIconUrl())
                .createdAt(card.getCategory().getCreatedAt())
                .build();

        // 좋아요 정보 조회
        Long likesCount = cardLikeRepository.countByCardId(card.getId());
        List<String> likedUserUuids = cardLikeRepository.findUuidsByCardId(card.getId());

        return CardResponse.builder()
                .id(card.getId())
                .uuid(card.getUuid())
                .title(card.getTitle())
                .category(categoryResponse)
                .tags(card.getTags())
                .situation(card.getSituation())
                .usageExamples(card.getUsageExamples())
                .content(card.getContent())
                .createdAt(card.getCreatedAt())
                .likesCount(likesCount)
                .likedUserUuids(likedUserUuids)
                .build();
    }
} 