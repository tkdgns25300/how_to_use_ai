package sanghun.project.howtouseai.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sanghun.project.howtouseai.domain.Card;

import java.util.List;
import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    
    Page<Card> findByCategoryId(Long categoryId, Pageable pageable);
    
    @Query("SELECT c FROM Card c WHERE c.category.id = :categoryId ORDER BY c.createdAt DESC")
    List<Card> findByCategoryIdOrderByCreatedAtDesc(@Param("categoryId") Long categoryId);
    
    @Query("SELECT c FROM Card c WHERE c.tags LIKE %:tag%")
    List<Card> findByTagsContaining(@Param("tag") String tag);
    
    List<Card> findAllByOrderByCreatedAtDesc();
    
    boolean existsByTitleAndUuid(String title, String uuid);
    
    @Query("SELECT c FROM Card c JOIN FETCH c.category ORDER BY " +
           "(SELECT COUNT(cl) FROM CardLike cl WHERE cl.card.id = c.id) DESC, " +
           "c.createdAt DESC")
    Page<Card> findAllByOrderByLikesCountDescCreatedAtDesc(Pageable pageable);
    
    // 더 효율적인 정렬을 위한 대안 쿼리 (서브쿼리 없이)
    @Query("SELECT c FROM Card c JOIN FETCH c.category " +
           "LEFT JOIN CardLike cl ON c.id = cl.card.id " +
           "GROUP BY c.id, c.uuid, c.title, c.category.id, c.tags, c.situation, c.usageExamples, c.content, c.createdAt " +
           "ORDER BY COUNT(cl.id) DESC, c.createdAt DESC")
    Page<Card> findAllWithCategoryOrderedByLikesAndDate(Pageable pageable);

    @Query("SELECT c FROM Card c JOIN FETCH c.category")
    Page<Card> findAllWithCategory(Pageable pageable);
    
    @Query("SELECT c FROM Card c JOIN FETCH c.category WHERE c.id = :id")
    Optional<Card> findByIdWithCategory(@Param("id") Long id);
} 