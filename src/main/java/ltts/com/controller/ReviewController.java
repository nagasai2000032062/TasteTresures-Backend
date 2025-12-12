package ltts.com.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ltts.com.dto.ApiResponse;
import ltts.com.dto.ProductDtoo;
import ltts.com.dto.ReviewDto;
import ltts.com.dto.ReviewRequest;
import ltts.com.service.ReviewService;

@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping("/create-review")
    public ResponseEntity<?> createReview(@RequestBody ReviewRequest reviewRequest) {
        try {
        	System.out.println("0");
            if (reviewRequest.getProductId() == null || reviewRequest.getProductId() < 0) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "ProductId is required", null));
            }
            if (reviewRequest.getUserId() == null || reviewRequest.getUserId() < 0) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "UserId is required", null));
            }
            if (reviewRequest.getRating() < 1 || reviewRequest.getRating() > 5) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "Rating must be between 1 and 5", null));
            }
            if (reviewRequest.getComment() == null || reviewRequest.getComment().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "Comment is required", null));
            }

            System.out.println("1");
            ReviewDto review = reviewService.createReview(reviewRequest);
            System.out.println("2");
            return ResponseEntity.ok(new ApiResponse(true, "Review created successfully", review));

        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error in creating review", ex.getMessage()));
        }
    }

    @GetMapping("/get-reviews/{id}")
    public ResponseEntity<?> getReviewsByProduct(@PathVariable Long id) {
        try {
            List<ReviewDto> reviews = reviewService.getReviewsByProduct(id);
            return ResponseEntity.ok(new ApiResponse(true, "Review List", reviews));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, "Error while getting reviews", e.getMessage()));
        }
    }
}
