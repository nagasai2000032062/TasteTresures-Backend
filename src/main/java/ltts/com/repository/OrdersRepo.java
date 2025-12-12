package ltts.com.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ltts.com.model.Order;
import ltts.com.model.Users;

@Repository
public interface OrdersRepo extends JpaRepository<Order, Long>
{

	List<Order> findByBuyerId(Long buyerId);
	List<Order> findAllByOrderByCreatedAtDesc();
}
