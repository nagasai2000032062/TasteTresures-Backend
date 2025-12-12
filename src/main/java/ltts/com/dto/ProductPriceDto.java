package ltts.com.dto;


public class ProductPriceDto {

	private Long id;
	private String weight;
	private Double price;
	public ProductPriceDto() {
		super();
	}
	public ProductPriceDto(Long id, String weight, Double price) {
		super();
		this.id = id;
		this.weight = weight;
		this.price = price;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getWeight() {
		return weight;
	}
	public void setWeight(String weight) {
		this.weight = weight;
	}
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	@Override
	public String toString() {
		return "ProductPriceDto [id=" + id + ", weight=" + weight + ", price=" + price + "]";
	}
}
