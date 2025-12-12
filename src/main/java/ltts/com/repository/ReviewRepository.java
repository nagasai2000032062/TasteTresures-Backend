package ltts.com.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ltts.com.model.Product;
import ltts.com.model.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProduct(Product product);
}