package ltts.com.dto;

import java.util.Arrays;
import java.util.List;

public class OrderItemDto {

	private Long productId;
    private String productName;
    private String variant;
    private Double price;
    private Integer quantity;
//    private Byte[] image;
    private List<byte[]> images;
	public OrderItemDto() {
		super();
	}
	public OrderItemDto(Long productId, String productName, String variant, Double price, Integer quantity,
			List<byte[]> images) {
		super();
		this.productId = productId;
		this.productName = productName;
		this.variant = variant;
		this.price = price;
		this.quantity = quantity;
		this.images = images;
	}
	public Long getProductId() {
		return productId;
	}
	public void setProductId(Long productId) {
		this.productId = productId;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
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
	public List<byte[]> getImages() {
		return images;
	}
	public void setImages(List<byte[]> images) {
		this.images = images;
	}
	@Override
	public String toString() {
		return "OrderItemDto [productId=" + productId + ", productName=" + productName + ", variant=" + variant
				+ ", price=" + price + ", quantity=" + quantity + ", images=" + images + "]";
	}
	
}
