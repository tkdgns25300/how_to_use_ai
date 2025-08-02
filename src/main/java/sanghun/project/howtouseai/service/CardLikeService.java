package sanghun.project.howtouseai.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sanghun.project.howtouseai.domain.Card;
import sanghun.project.howtouseai.domain.CardLike;
import sanghun.project.howtouseai.exception.CardNotFoundException;
import sanghun.project.howtouseai.exception.LikeAlreadyExistsException;
import sanghun.project.howtouseai.repository.CardLikeRepository;
import sanghun.project.howtouseai.repository.CardRepository;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CardLikeService {

    private final CardLikeRepository cardLikeRepository;
    private final CardRepository cardRepository;

    @Transactional
    public void addLike(Long cardId, String uuid) {
        log.info("좋아요 추가 요청: cardId={}, uuid={}", cardId, uuid);

        // 카드 존재 여부 확인
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> {
                    log.warn("존재하지 않는 카드에 좋아요 시도: cardId={}", cardId);
                    return new CardNotFoundException(
                        String.format("카드를 찾을 수 없습니다: ID %d", cardId)
                    );
                });

        // 이미 좋아요했는지 확인
        if (cardLikeRepository.existsByCardIdAndUuid(cardId, uuid)) {
            log.warn("이미 좋아요한 카드에 중복 좋아요 시도: cardId={}, uuid={}", cardId, uuid);
            throw new LikeAlreadyExistsException(
                String.format("이미 좋아요한 카드입니다: ID %d", cardId)
            );
        }

        // 좋아요 엔티티 생성
        CardLike cardLike = CardLike.builder()
                .card(card)
                .uuid(uuid)
                .build();

        // 저장
        cardLikeRepository.save(cardLike);
        log.info("좋아요 추가 완료: cardId={}, uuid={}", cardId, uuid);
    }
} 