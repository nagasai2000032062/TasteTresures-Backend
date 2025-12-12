package ltts.com.service;

import java.util.List;

import ltts.com.dto.OrderDto;
import ltts.com.dto.OrderRequest;
import ltts.com.model.Order;

public interface OrderService 
{

	public OrderDto createOrder(OrderRequest orderRequest);
	public List<OrderDto> getOrdersByBuyerId(Long buyerId);
	public List<OrderDto> getAllOrders();
	public OrderDto updateOrderStatus(Long orderId, String status) throws Exception;
	public void deleteOrder(Long orderId);
}
