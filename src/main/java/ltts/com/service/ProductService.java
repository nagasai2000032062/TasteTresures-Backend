package ltts.com.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import ltts.com.dto.ProductDto;
import ltts.com.dto.ProductDtoo;
import ltts.com.model.Product;

public interface ProductService {
	public ProductDtoo createProduct(ProductDto productDto)throws IOException;
	public ProductDtoo updateProduct(Long id,ProductDto productDto)throws IOException;
	public List<ProductDtoo> getAllProducts();
	public ProductDtoo getSingleProduct(String slug);
	public Map<String, byte[]> getProductPhotoByIndex(Long id, int index);
	public int getNoOfImgesById(Long id);
	public boolean deleteProduct(Long id);
	public long count();
	public List<ProductDtoo> getProductsByCategory(String slug);
	public List<ProductDtoo> getRelatedProducts(Long productId, Long categoryId);
	public List<ProductDtoo> getProductList(int page);
//	public List<ProductDtoo> filterProducts(String category, Boolean availability, Double minPrice, Double maxPrice);
	public List<ProductDtoo> filterProducts(
            String category,
            Boolean availability,
            Double minPrice,
            Double maxPrice,
            String sortBy,
            String sortDirection
    );
	public List<ProductDtoo> searchProducts(String keyword);
}
