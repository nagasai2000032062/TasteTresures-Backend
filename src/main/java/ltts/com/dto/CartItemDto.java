package ltts.com.dto;

public class CartItemDto {

	private Long productId;
    private String variant;
    private Double price;
    private Integer quantity;
	public CartItemDto() {
		super();
	}
	public CartItemDto(Long productId, String variant, Double price, Integer quantity) {
		super();
		this.productId = productId;
		this.variant = variant;
		this.price = price;
		this.quantity = quantity;
	}
	public Long getProductId() {
		return productId;
	}
	public void setProductId(Long productId) {
		this.productId = productId;
	}
	public String getVariant() {
		return variant;
	}
	public void setVariant(String variant) {
		this.variant = variant;
	}
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	@Override
	public String toString() {
		return "CartItemDto [productId=" + productId + ", variant=" + variant + ", price=" + price + ", quantity="
				+ quantity + "]";
	}
    
}
