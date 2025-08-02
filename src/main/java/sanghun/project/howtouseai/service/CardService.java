package sanghun.project.howtouseai.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import sanghun.project.howtouseai.repository.CardRepository;
import sanghun.project.howtouseai.repository.CategoryRepository;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CardService {

    private final CardRepository cardRepository;
    private final CategoryRepository categoryRepository;

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
                .build();
    }
} 