package sanghun.project.howtouseai.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sanghun.project.howtouseai.domain.Card;
import sanghun.project.howtouseai.domain.CardLike;
import sanghun.project.howtouseai.exception.CardNotFoundException;
import sanghun.project.howtouseai.exception.LikeAlreadyExistsException;
import sanghun.project.howtouseai.exception.LikeNotFoundException;
import sanghun.project.howtouseai.repository.CardLikeRepository;
import sanghun.project.howtouseai.repository.CardRepository;

import java.util.Optional;

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

    @Transactional
    public void removeLike(Long cardId, String uuid) {
        log.info("좋아요 취소 요청: cardId={}, uuid={}", cardId, uuid);

        // 카드 존재 여부 확인
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> {
                    log.warn("존재하지 않는 카드에 좋아요 취소 시도: cardId={}", cardId);
                    return new CardNotFoundException(
                        String.format("카드를 찾을 수 없습니다: ID %d", cardId)
                    );
                });

        // 좋아요 존재 여부 확인
        Optional<CardLike> existingLike = cardLikeRepository.findByCardIdAndUuid(cardId, uuid);
        if (existingLike.isEmpty()) {
            log.warn("존재하지 않는 좋아요 취소 시도: cardId={}, uuid={}", cardId, uuid);
            throw new LikeNotFoundException(
                String.format("좋아요를 찾을 수 없습니다: 카드 ID %d, UUID %s", cardId, uuid)
            );
        }

        // 좋아요 삭제
        cardLikeRepository.delete(existingLike.get());
        log.info("좋아요 취소 완료: cardId={}, uuid={}", cardId, uuid);
    }
} 