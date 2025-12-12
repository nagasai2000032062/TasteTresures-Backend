package ltts.com.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ltts.com.model.Category;
import ltts.com.model.Product;
import org.springframework.data.domain.Sort;
@Repository
public interface ProductRepo extends JpaRepository<Product, Long>
{

	List<Product> findAll();
//	@Query("SELECT p FROM Product p JOIN FETCH p.category ORDER BY p.createdAt DESC")
//    List<Products> findAllWithCategoryLimitedAndSorted();
	Product findBySlug(String slug);
	
	

	@Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Product> searchProductsByKeyword(String keyword);
	
	@Query("SELECT p FROM Product p WHERE p.category.id = :categoryId AND p.id != :productId")
    List<Product> findRelatedProducts(Long productId, Long categoryId);
	
	List<Product> findByCategory(Category category);
	
//	@Query("SELECT p FROM Product p " +
//	           "WHERE (:categoryName IS NULL OR p.category.name = :categoryName) " +
//	           "AND (:availability IS NULL OR p.availability = :availability) " +
//	           "AND (:minPrice IS NULL OR :maxPrice IS NULL OR " +
//	           "      EXISTS (SELECT 1 FROM p.variants v WHERE v.price BETWEEN :minPrice AND :maxPrice))")
//	    List<Product> filterProducts(
//	        @Param("categoryName") String categoryName,
//	        @Param("availability") Boolean availability,
//	        @Param("minPrice") Double minPrice,
//	        @Param("maxPrice") Double maxPrice
//	    );
	@Query("SELECT DISTINCT p FROM Product p " +
		       "LEFT JOIN p.variants v " +
		       "WHERE (:categoryName IS NULL OR p.category.name = :categoryName) " +
		       "AND (:availability IS NULL OR p.availability = :availability) " +
		       "AND (:minPrice IS NULL OR :maxPrice IS NULL OR v.price BETWEEN :minPrice AND :maxPrice)")
		List<Product> filterProducts(
		        @Param("categoryName") String categoryName,
		        @Param("availability") Boolean availability,
		        @Param("minPrice") Double minPrice,
		        @Param("maxPrice") Double maxPrice,
		        org.springframework.data.domain.Sort sort
		);

}
