package ltts.com.dto;

import java.time.LocalDateTime;

public class ReviewDto {

	private Long id;
	private ProductDtoo product;
	private UserDto user;
	private int rating; // 1–5
    private String comment;
    private LocalDateTime createdAt;
	public ReviewDto() {
		super();
	}
	public ReviewDto(Long id, ProductDtoo product, UserDto user, int rating, String comment, LocalDateTime createdAt) {
		super();
		this.id = id;
		this.product = product;
		this.user = user;
		this.rating = rating;
		this.comment = comment;
		this.createdAt = createdAt;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public ProductDtoo getProduct() {
		return product;
	}
	public void setProduct(ProductDtoo product) {
		this.product = product;
	}
	public UserDto getUser() {
		return user;
	}
	public void setUser(UserDto user) {
		this.user = user;
	}
	public int getRating() {
		return rating;
	}
	public void setRating(int rating) {
		this.rating = rating;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	@Override
	public String toString() {
		return "ReviewDto [id=" + id + ", product=" + product + ", user=" + user + ", rating=" + rating + ", comment="
				+ comment + ", createdAt=" + createdAt + "]";
	}
	
}
