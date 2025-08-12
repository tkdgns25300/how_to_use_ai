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
import sanghun.project.howtouseai.dto.CardLikeCountDto;
import sanghun.project.howtouseai.domain.CardLike;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Collections;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CardService {

    private final CardRepository cardRepository;
    private final CategoryRepository categoryRepository;
    private final CardLikeRepository cardLikeRepository;

    /**
     * 모든 카드 정보를 조회하고 DTO로 변환하여 반환합니다. (N+1 문제 해결을 위해 Fetch Join 사용)
     *
     * @param pageable 페이징 정보
     * @return 카드 응답 DTO의 페이지
     */
    @Transactional(readOnly = true)
    public Page<CardResponse> getAllCards(Pageable pageable) {
        log.info("모든 카드 조회 요청 (Fetch Join): page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        Page<Card> cards = cardRepository.findAllWithCategory(pageable);
        log.info("카드 조회 완료 (Fetch Join): totalElements={}, totalPages={}", cards.getTotalElements(), cards.getTotalPages());
        return cards.map(this::convertToResponse);
    }

    /**
     * 홈 페이지에 표시할 카드 목록을 조회합니다. (좋아요 정보 포함)
     *
     * @param pageable 페이징 정보
     * @param userUuid 현재 사용자 UUID
     * @return 카드 응답 DTO 리스트
     */
    @Transactional(readOnly = true)
    public List<CardResponse> getCardsForHomePage(Pageable pageable, String userUuid) {
        log.info("홈 페이지 카드 조회 요청: page={}, size={}, userUuid={}", 
                pageable.getPageNumber(), pageable.getPageSize(), userUuid);
        
        Page<Card> cards = cardRepository.findAllWithCategory(pageable);
        log.info("홈 페이지 카드 조회 완료: totalElements={}, totalPages={}", 
                cards.getTotalElements(), cards.getTotalPages());

        List<Long> cardIds = cards.getContent().stream().map(Card::getId).collect(Collectors.toList());
        
        // N+1 문제를 해결하기 위해 좋아요 정보를 한 번에 조회
        Map<Long, List<CardLike>> likesByCardId = cardLikeRepository.findByCard_IdIn(cardIds).stream()
                .collect(Collectors.groupingBy(like -> like.getCard().getId()));

        return cards.getContent().stream()
                .map(card -> {
                    List<CardLike> likes = likesByCardId.getOrDefault(card.getId(), Collections.emptyList());
                    long likesCount = likes.size();
                    boolean isLiked = likes.stream().anyMatch(like -> like.getUuid().equals(userUuid));
                    List<String> likedUuids = likes.stream().map(CardLike::getUuid).collect(Collectors.toList());
                    return convertToResponse(card, likesCount, isLiked, likedUuids);
                })
                .collect(Collectors.toList());
    }

    /**
     * 특정 ID의 카드 정보를 조회하고 DTO로 변환하여 반환합니다.
     *
     * @param cardId 카드 ID
     * @param userUuid 현재 사용자 UUID
     * @return 카드 응답 DTO
     * @throws CardNotFoundException 해당 ID의 카드가 존재하지 않을 경우
     */
    @Transactional(readOnly = true)
    public CardResponse getCardById(Long cardId, String userUuid) {
        log.info("카드 상세 조회 요청: cardId={}, userUuid={}", cardId, userUuid);
        Card card = cardRepository.findByIdWithCategory(cardId)
                .orElseThrow(() -> new CardNotFoundException("Card not found with id: " + cardId));
        
        List<CardLike> likes = cardLikeRepository.findByCard_Id(cardId);
        long likesCount = likes.size();
        boolean isLiked = likes.stream().anyMatch(like -> like.getUuid().equals(userUuid));
        List<String> likedUuids = likes.stream().map(CardLike::getUuid).collect(Collectors.toList());
        
        log.info("카드 상세 조회 완료: id={}, title={}, likesCount={}, isLiked={}, userUuid={}, likedUuids={}", 
                card.getId(), card.getTitle(), likesCount, isLiked, userUuid, likedUuids);
        
        return convertToResponse(card, likesCount, isLiked, likedUuids);
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

    /**
     * 카드 엔티티를 전체 정보 DTO로 변환합니다. (좋아요 정보 포함)
     *
     * @param card 카드 엔티티
     * @return 카드 응답 DTO
     */
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

    /**
     * 카드 엔티티를 DTO로 변환합니다.
     *
     * @param card 카드 엔티티
     * @param likesCount 좋아요 수
     * @param isLiked 현재 사용자의 좋아요 여부
     * @param likedUuids 좋아요한 사용자 UUID 목록
     * @return 카드 응답 DTO
     */
    private CardResponse convertToResponse(Card card, long likesCount, boolean isLiked, List<String> likedUuids) {
        CategoryResponse categoryResponse = new CategoryResponse(
            card.getCategory().getId(),
            card.getCategory().getName(),
            card.getCategory().getIconUrl()
        );

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
                .likedByUser(isLiked)
                .likedUserUuids(likedUuids)
                .build();
    }

    private Map<Long, Long> getLikesCountMap(List<Long> cardIds) {
        if (cardIds == null || cardIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return cardLikeRepository.countLikesByCardIds(cardIds).stream()
                .collect(Collectors.toMap(CardLikeCountDto::getCardId, CardLikeCountDto::getLikeCount));
    }
} 