package ltts.com.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;
import ltts.com.model.Category;

@Repository
public interface CategoryRepo extends JpaRepository<Category, Long>
{
    List<Category>findAll();
    Category findBySlug(String slug);
    Category findByName(String name);
//    void deleteById(Long id);
}
