package sanghun.project.howtouseai.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sanghun.project.howtouseai.domain.Card;
import sanghun.project.howtouseai.domain.Category;
import sanghun.project.howtouseai.dto.CardCreateRequest;
import sanghun.project.howtouseai.dto.CardResponse;
import sanghun.project.howtouseai.dto.CategoryResponse;
import sanghun.project.howtouseai.exception.CardAlreadyExistsException;
import sanghun.project.howtouseai.exception.CategoryNotFoundException;
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