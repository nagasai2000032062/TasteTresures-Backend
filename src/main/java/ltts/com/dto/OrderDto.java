package ltts.com.dto;

import java.time.LocalDateTime;
import java.util.List;

public class OrderDto {

	private Long id;
    private List<OrderItemDto> items;
    private UserDto buyer;
    private String payment;
    private String orderAddress;
    private String status;
    private LocalDateTime createdAt;
	public OrderDto() {
		super();
	}
	public OrderDto(Long id, List<OrderItemDto> items, UserDto buyer, String payment, String orderAddress,
			String status, LocalDateTime createdAt) {
		super();
		this.id = id;
		this.items = items;
		this.buyer = buyer;
		this.payment = payment;
		this.orderAddress = orderAddress;
		this.status = status;
		this.createdAt = createdAt;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public List<OrderItemDto> getItems() {
		return items;
	}
	public void setItems(List<OrderItemDto> items) {
		this.items = items;
	}
	public UserDto getBuyer() {
		return buyer;
	}
	public void setBuyer(UserDto buyer) {
		this.buyer = buyer;
	}
	public String getPayment() {
		return payment;
	}
	public void setPayment(String payment) {
		this.payment = payment;
	}
	public String getOrderAddress() {
		return orderAddress;
	}
	public void setOrderAddress(String orderAddress) {
		this.orderAddress = orderAddress;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	@Override
	public String toString() {
		return "OrderDto [id=" + id + ", items=" + items + ", buyer=" + buyer + ", payment=" + payment
				+ ", orderAddress=" + orderAddress + ", status=" + status + ", createdAt=" + createdAt + "]";
	}
	
}
