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
import sanghun.project.howtouseai.dto.LikeResponse;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CardLikeService {

    private final CardLikeRepository cardLikeRepository;
    private final CardRepository cardRepository;

    /**
     * 특정 카드에 대한 사용자의 '좋아요' 상태를 토글합니다.
     * 이미 '좋아요'를 눌렀다면 취소하고, 누르지 않았다면 추가합니다.
     *
     * @param cardId 카드 ID
     * @param uuid   사용자 UUID
     * @return 새로운 좋아요 상태와 총 좋아요 수를 담은 응답 DTO
     * @throws CardNotFoundException 해당 ID의 카드가 존재하지 않을 경우
     */
    public LikeResponse toggleLike(Long cardId, String uuid) {
        log.info("좋아요 토글 요청: cardId={}, uuid={}", cardId, uuid);

        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> {
                    log.warn("존재하지 않는 카드에 좋아요 토글 시도: cardId={}", cardId);
                    return new CardNotFoundException("Card not found with id: " + cardId);
                });

        Optional<CardLike> existingLike = cardLikeRepository.findByCardIdAndUuid(cardId, uuid);

        boolean isLiked;
        if (existingLike.isPresent()) {
            // 이미 좋아요 상태 -> 좋아요 취소
            cardLikeRepository.delete(existingLike.get());
            isLiked = false;
            log.info("좋아요 취소 완료: cardId={}, uuid={}", cardId, uuid);
        } else {
            // 좋아요 아닌 상태 -> 좋아요 추가
            CardLike newLike = CardLike.builder().card(card).uuid(uuid).build();
            cardLikeRepository.save(newLike);
            isLiked = true;
            log.info("좋아요 추가 완료: cardId={}, uuid={}", cardId, uuid);
        }

        long likesCount = cardLikeRepository.countByCardId(cardId);
        log.info("좋아요 토글 후, 카드 ID {}의 총 좋아요 수: {}", cardId, likesCount);

        return LikeResponse.builder()
                .liked(isLiked)
                .likesCount(likesCount)
                .build();
    }
} 