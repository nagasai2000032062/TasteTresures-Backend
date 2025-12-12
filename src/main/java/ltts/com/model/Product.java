package ltts.com.model;

import java.time.LocalDateTime;
import java.util.List;



import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;

@Entity
public class Product {

	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    @Column(nullable = false)
	    private String name; // Pickle/Snack name

	    @Column(nullable = false)
	    private String slug; // URL-friendly name

	    @Column(length = 2000, nullable = false)
	    private String description;

	    @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "category_id", nullable = false)
	    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	    private Category category;

	    @Column(name = "quantity", nullable = false)
	    private Integer quantity;
	    
	    @ElementCollection
	    @Lob
	    @Column(name = "image", columnDefinition = "LONGBLOB")
	    private List<byte[]> images;

	    @ElementCollection
	    @Column(name = "content_type")
	    private List<String> imageContentTypes;

	    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
	    private List<ProductPrice> variants;
	    @Column(name = "availability")
	    private Boolean availability;

	    @Column(name = "created_at", updatable = false)
	    private LocalDateTime createdAt;

	    @Column(name = "updated_at")
	    private LocalDateTime updatedAt;

	    @PrePersist
	    protected void onCreate() {
	        this.createdAt = LocalDateTime.now();
	        this.updatedAt = LocalDateTime.now();
	    }

	    @PreUpdate
	    protected void onUpdate() {
	        this.updatedAt = LocalDateTime.now();
	    }

		public Product() {
			super();
		}

		public Product(Long id, String name, String slug, String description, Category category, Integer quantity,
				List<byte[]> images, List<String> imageContentTypes, List<ProductPrice> variants, Boolean availability,
				LocalDateTime createdAt, LocalDateTime updatedAt) {
			super();
			this.id = id;
			this.name = name;
			this.slug = slug;
			this.description = description;
			this.category = category;
			this.quantity = quantity;
			this.images = images;
			this.imageContentTypes = imageContentTypes;
			this.variants = variants;
			this.availability = availability;
			this.createdAt = createdAt;
			this.updatedAt = updatedAt;
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

		public Category getCategory() {
			return category;
		}

		public void setCategory(Category category) {
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

		public List<String> getImageContentTypes() {
			return imageContentTypes;
		}

		public void setImageContentTypes(List<String> imageContentTypes) {
			this.imageContentTypes = imageContentTypes;
		}

		public List<ProductPrice> getVariants() {
			return variants;
		}

		public void setVariants(List<ProductPrice> variants) {
			this.variants = variants;
		}

		public Boolean getAvailability() {
			return availability;
		}

		public void setAvailability(Boolean availability) {
			this.availability = availability;
		}

		public LocalDateTime getCreatedAt() {
			return createdAt;
		}

		public void setCreatedAt(LocalDateTime createdAt) {
			this.createdAt = createdAt;
		}

		public LocalDateTime getUpdatedAt() {
			return updatedAt;
		}

		public void setUpdatedAt(LocalDateTime updatedAt) {
			this.updatedAt = updatedAt;
		}

		@Override
		public String toString() {
			return "Product [id=" + id + ", name=" + name + ", slug=" + slug + ", description=" + description
					+ ", category=" + category + ", quantity=" + quantity + ", images=" + images
					+ ", imageContentTypes=" + imageContentTypes + ", variants=" + variants + ", availability="
					+ availability + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + "]";
		}

		
}
