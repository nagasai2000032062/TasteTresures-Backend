package ltts.com.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ltts.com.dto.AddressDto;
import ltts.com.dto.CategoryDto;
import ltts.com.dto.ProductDtoo;
import ltts.com.dto.ProductPriceDto;
import ltts.com.dto.ReviewDto;
import ltts.com.dto.ReviewRequest;
import ltts.com.dto.UserDto;
import ltts.com.model.Product;
import ltts.com.model.Review;
import ltts.com.model.Users;
import ltts.com.repository.ProductRepo;
import ltts.com.repository.ReviewRepository;
import ltts.com.repository.UserRepo;

@Service
public class ReviewServiceImpl implements ReviewService
{

	@Autowired
	private ReviewRepository reviewRepo;
	@Autowired
	private ProductRepo productRepo;
	@Autowired
	private UserRepo userRepo;
	@Autowired
	private ModelMapper modelMapper;
	@Override
	public ReviewDto createReview(ReviewRequest reviewRequest) {
		Review review= new Review();
		Product product=productRepo.findById(reviewRequest.getProductId()).orElseThrow(() -> new RuntimeException("Product not found"));
		Users user=userRepo.findById(reviewRequest.getUserId()).orElseThrow(() -> new RuntimeException("Product not found"));
		review.setProduct(product);
		review.setUser(user);
		review.setRating(reviewRequest.getRating());
		review.setComment(reviewRequest.getComment());
		review=reviewRepo.save(review);
		return convertToDto(review);
	}

	@Override
	public List<ReviewDto> getReviewsByProduct(Long productId) {
		Product product = productRepo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
		List<Review> reviews=reviewRepo.findByProduct(product);
		return reviews.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
	}

	private ReviewDto convertToDto(Review r) {
		List<AddressDto>AllAddress=r.getUser().getAddresses().stream()
                .map(add -> new AddressDto(add.getId(), add.getFullName(),add.getPhoneNumber(),add.getAddressLine(),add.getCity(),
                		add.getState(),add.getCountry(),add.getPostalCode(),add.getIsDefault()))
                .collect(Collectors.toList());
		UserDto userDto = new UserDto(
              r.getUser().getId(),
              r.getUser().getName(),
              r.getUser().getEmail(),
              null, // avoid exposing password
              r.getUser().getPhone(),
              AllAddress,
              r.getUser().getAnswer(),
              r.getUser().getRole()
      );
	  Product product = r.getProduct();
		
        CategoryDto categoryDto = new CategoryDto(
                product.getCategory().getId(),
                product.getCategory().getName(),
                product.getCategory().getSlug()
        );

        List<ProductPriceDto> productPriceDtos = product.getVariants().stream()
                .map(price -> new ProductPriceDto(price.getId(), price.getWeight(), price.getPrice()))
                .collect(Collectors.toList());

        ProductDtoo productDtos=  new ProductDtoo(
                product.getId(),
                product.getName(),
                product.getSlug(),
                product.getDescription(),
                categoryDto.getName(),
                categoryDto,
                product.getQuantity(),
                product.getImages(),
                productPriceDtos,
                product.getImageContentTypes(),
                product.getAvailability()
        );
        return new ReviewDto(
              r.getId(),
              productDtos,
              userDto,
              r.getRating(),
              r.getComment(),
              r.getCreatedAt()
      );
	}
}
