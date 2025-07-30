package sanghun.project.howtouseai.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sanghun.project.howtouseai.domain.CardLike;

import java.util.List;
import java.util.Optional;

@Repository
public interface CardLikeRepository extends JpaRepository<CardLike, Long> {
    
    List<CardLike> findByCardId(Long cardId);
    
    Optional<CardLike> findByCardIdAndUuid(Long cardId, String uuid);
    
    @Query("SELECT COUNT(cl) FROM CardLike cl WHERE cl.card.id = :cardId")
    long countByCardId(@Param("cardId") Long cardId);
    
    boolean existsByCardIdAndUuid(Long cardId, String uuid);
} 