package sanghun.project.howtouseai.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sanghun.project.howtouseai.domain.Like;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    
    List<Like> findByCardId(Long cardId);
    
    Optional<Like> findByCardIdAndDeviceKey(Long cardId, String deviceKey);
    
    @Query("SELECT COUNT(l) FROM Like l WHERE l.card.id = :cardId")
    long countByCardId(@Param("cardId") Long cardId);
    
    boolean existsByCardIdAndDeviceKey(Long cardId, String deviceKey);
} 