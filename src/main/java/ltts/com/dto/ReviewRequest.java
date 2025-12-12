package ltts.com.dto;


public class ReviewRequest {

	private Long id;
	private Long productId;
	private Long userId;
	private int rating; // 1–5
    private String comment;
	public ReviewRequest() {
		super();
	}
	public ReviewRequest(Long id, Long productId, Long userId, int rating, String comment) {
		super();
		this.id = id;
		this.productId = productId;
		this.userId = userId;
		this.rating = rating;
		this.comment = comment;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getProductId() {
		return productId;
	}
	public void setProductId(Long productId) {
		this.productId = productId;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
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
	@Override
	public String toString() {
		return "ReviewRequest [id=" + id + ", productId=" + productId + ", userId=" + userId + ", rating=" + rating
				+ ", comment=" + comment + "]";
	}
    
}
