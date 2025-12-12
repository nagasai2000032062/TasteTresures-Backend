package ltts.com.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.transaction.Transactional;
import ltts.com.model.OrderItem;

public interface orderItemRepository extends JpaRepository<OrderItem, Long>
{

	@Modifying
	@Transactional
	@Query("DELETE FROM OrderItem oi WHERE oi.product.id = :productId")
	void deleteByProductId(@Param("productId") Long productId);
}
