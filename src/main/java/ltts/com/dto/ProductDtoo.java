package ltts.com.dto;

import java.util.List;

public class ProductDtoo {

	private Long id;
	private String name;
	private String slug;
	private String description;
	private String categoryName;
	private CategoryDto category;
	private Integer quantity;
	private List<byte[]> images;
	private List<ProductPriceDto> prices;
	private List<String> imageContentTypes;
	private Boolean availability;
	public ProductDtoo() {
		super();
	}
	public ProductDtoo(Long id, String name, String slug, String description, String categoryName, CategoryDto category,
			Integer quantity, List<byte[]> images, List<ProductPriceDto> prices, List<String> imageContentTypes,
			Boolean availability) {
		super();
		this.id = id;
		this.name = name;
		this.slug = slug;
		this.description = description;
		this.categoryName = categoryName;
		this.category = category;
		this.quantity = quantity;
		this.images = images;
		this.prices = prices;
		this.imageContentTypes = imageContentTypes;
		this.availability = availability;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSlug() {
		return slug;
	}
	public void setSlug(String slug) {
		this.slug = slug;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public CategoryDto getCategory() {
		return category;
	}
	public void setCategory(CategoryDto category) {
		this.category = category;
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
	public List<ProductPriceDto> getPrices() {
		return prices;
	}
	public void setPrices(List<ProductPriceDto> prices) {
		this.prices = prices;
	}
	public List<String> getImageContentTypes() {
		return imageContentTypes;
	}
	public void setImageContentTypes(List<String> imageContentTypes) {
		this.imageContentTypes = imageContentTypes;
	}
	public Boolean getAvailability() {
		return availability;
	}
	public void setAvailability(Boolean availability) {
		this.availability = availability;
	}
	@Override
	public String toString() {
		return "ProductDtoo [id=" + id + ", name=" + name + ", slug=" + slug + ", description=" + description
				+ ", categoryName=" + categoryName + ", category=" + category + ", quantity=" + quantity + ", images="
				+ images + ", prices=" + prices + ", imageContentTypes=" + imageContentTypes + ", availability="
				+ availability + "]";
	}
	
}
