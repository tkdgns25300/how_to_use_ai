package sanghun.project.howtouseai.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sanghun.project.howtouseai.domain.CardLike;
import sanghun.project.howtouseai.dto.CardLikeCountDto;

import java.util.List;
import java.util.Optional;

@Repository
public interface CardLikeRepository extends JpaRepository<CardLike, Long> {
    
    Optional<CardLike> findByCardIdAndUuid(Long cardId, String uuid);
    
    boolean existsByCardIdAndUuid(Long cardId, String uuid);
    
    @Query("SELECT cl.uuid FROM CardLike cl WHERE cl.card.id = :cardId")
    List<String> findUuidsByCardId(@Param("cardId") Long cardId);
    
    @Query("SELECT COUNT(cl) FROM CardLike cl WHERE cl.card.id = :cardId")
    Long countByCardId(@Param("cardId") Long cardId);
    
    // 카드 ID로 모든 좋아요 데이터 삭제
    void deleteByCardId(Long cardId);

    @Query("SELECT new sanghun.project.howtouseai.dto.CardLikeCountDto(cl.card.id, COUNT(cl.id)) " +
           "FROM CardLike cl WHERE cl.card.id IN :cardIds " +
           "GROUP BY cl.card.id")
    List<CardLikeCountDto> countLikesByCardIds(@Param("cardIds") List<Long> cardIds);

    List<CardLike> findByCard_Id(Long cardId);

    List<CardLike> findByCard_IdIn(List<Long> cardIds);
} 