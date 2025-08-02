package sanghun.project.howtouseai.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sanghun.project.howtouseai.domain.Card;

import java.util.List;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    
    Page<Card> findByCategoryId(Long categoryId, Pageable pageable);
    
    @Query("SELECT c FROM Card c WHERE c.category.id = :categoryId ORDER BY c.createdAt DESC")
    List<Card> findByCategoryIdOrderByCreatedAtDesc(@Param("categoryId") Long categoryId);
    
    @Query("SELECT c FROM Card c WHERE c.tags LIKE %:tag%")
    List<Card> findByTagsContaining(@Param("tag") String tag);
    
    List<Card> findAllByOrderByCreatedAtDesc();
    
    boolean existsByTitleAndUuid(String title, String uuid);
} 