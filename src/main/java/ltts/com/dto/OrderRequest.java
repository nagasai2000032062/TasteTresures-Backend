package ltts.com.dto;

import java.util.List;

public class OrderRequest {

	private Long buyerId;

    private String paymentDetails;

    private List<CartItemDto> cartItems;

	public OrderRequest() {
		super();
	}

	public OrderRequest(Long buyerId, String paymentDetails, List<CartItemDto> cartItems) {
		super();
		this.buyerId = buyerId;
		this.paymentDetails = paymentDetails;
		this.cartItems = cartItems;
	}

	public Long getBuyerId() {
		return buyerId;
	}

	public void setBuyerId(Long buyerId) {
		this.buyerId = buyerId;
	}

	public String getPaymentDetails() {
		return paymentDetails;
	}

	public void setPaymentDetails(String paymentDetails) {
		this.paymentDetails = paymentDetails;
	}

	public List<CartItemDto> getCartItems() {
		return cartItems;
	}

	public void setCartItems(List<CartItemDto> cartItems) {
		this.cartItems = cartItems;
	}

	@Override
	public String toString() {
		return "OrderRequest [buyerId=" + buyerId + ", paymentDetails=" + paymentDetails + "]";
	}

    
}