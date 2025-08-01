package sanghun.project.howtouseai.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sanghun.project.howtouseai.domain.Category;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    List<Category> findAllByOrderByNameAsc();
    
    boolean existsByName(String name);
} 