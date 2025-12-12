package ltts.com.service;

import java.util.List;

import ltts.com.dto.ReviewDto;
import ltts.com.dto.ReviewRequest;

public interface ReviewService 
{

	public ReviewDto createReview(ReviewRequest reviewRequest);
	public List<ReviewDto> getReviewsByProduct(Long productId);
}
