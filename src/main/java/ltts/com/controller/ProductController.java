package ltts.com.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import ltts.com.dto.ProductDto;
import ltts.com.dto.ProductDtoo;
import ltts.com.dto.UserDto;
import ltts.com.model.Product;
import ltts.com.model.Users;
import ltts.com.repository.ProductRepo;
import ltts.com.repository.UserRepo;
import ltts.com.dto.ApiResponse;
import ltts.com.service.ProductService;

@RestController
@RequestMapping("/api/v1/product")
public class ProductController {

	@Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ProductService productService;

    @Autowired
	private ProductRepo productRepo;
    @Autowired
	private UserRepo userRepository;
    @PostMapping("/create-product")
    public ResponseEntity<?> createProduct(@ModelAttribute ProductDto productDto) {
        try {
        	System.out.println("1");
            if (productDto.getName() == null || productDto.getName().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Name is required");
            }
            if (productDto.getDescription() == null || productDto.getDescription().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Description is required");
            }
            if (productDto.getPrices() == null || productDto.getPrices().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Price is required");
            }
            if (productDto.getCategory() == null || productDto.getCategory().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Category is required");
            }
            if (productDto.getQuantity() == null || productDto.getQuantity() < 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Quantity is required");
            }
            if (productDto.getImages() == null || productDto.getImages().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("At least one image is required");
            }
            System.out.println("2");
            ProductDtoo product = productService.createProduct(productDto);
            return ResponseEntity.ok(new ApiResponse(true, "Product created successfully", product));

        } catch (Exception ex) {
            ex.printStackTrace(); // or use logger
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error in creating product", ex.getMessage()));
        }
    }
    
    @PutMapping("/update-product/{pid}")
    public ResponseEntity<?> updateProduct(@PathVariable Long pid, @ModelAttribute ProductDto productDto) {
        try {
            // Input validation
            if (productDto.getName() == null || productDto.getName().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Name is required");
            }
            if (productDto.getDescription() == null || productDto.getDescription().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Description is required");
            }
            if (productDto.getPrices() == null || productDto.getPrices().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Price is required");
            }
            if (productDto.getCategory() == null || productDto.getCategory().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Category is required");
            }
            if (productDto.getQuantity() == null || productDto.getQuantity() < 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Quantity is required");
            }
            if (productDto.getImages() == null || productDto.getImages().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("At least one image is required");
            }

            // Call service to update
            ProductDtoo updatedProduct = productService.updateProduct(pid, productDto);
            return ResponseEntity.status(201)
                    .body(new ApiResponse(true, "Product Updated Successfully", updatedProduct));

        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error in updating product", ex.getMessage()));
        }
    }

    @GetMapping("/get-product")
    public ResponseEntity<Map<String, Object>> getAllProducts() {
        Map<String, Object> response = new HashMap<>();

        try {
            List<ProductDtoo> products = productService.getAllProducts();

            response.put("success", true);
            response.put("countTotal", products.size());
            response.put("message", "All Products");
            response.put("products", products);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error in getting products");
            response.put("error", e.getMessage());

            return ResponseEntity.status(500).body(response);
        }
    }
    @GetMapping("/product-photo/{pid}/{index}")
    public ResponseEntity<?> getProductPhoto(
            @PathVariable Long pid,
            @PathVariable int index) {

        try {
            Map<String, byte[]> imageMap = productService.getProductPhotoByIndex(pid, index);

            if (imageMap.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Photo not found");
            }

            byte[] imageData = null;
            String contentType = "";

            for (Map.Entry<String, byte[]> entry : imageMap.entrySet()) {
                contentType = entry.getKey();
                imageData = entry.getValue();
            }

            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", contentType);

            return ResponseEntity.ok().headers(headers).body(imageData);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error while getting photo: " + e.getMessage());
        }
    }
    @GetMapping("/productImage-count/{pid}")
    public ResponseEntity getImagesCountById(@PathVariable Long pid)
    {
    	try {
    		int k=productService.getNoOfImgesById(pid);
    		Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "images count");
            response.put("count", k);

            return ResponseEntity.ok().body(new ApiResponse(true, "Fetched", response));
		}catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error while getting single product");
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    @DeleteMapping("/delete-product/{pid}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long pid) {
    	System.out.println("DeleteRequest..");
        try {
            boolean isDeleted = productService.deleteProduct(pid);
            if (isDeleted) {
                return ResponseEntity.ok().body(new ApiResponse(true, "Product Deleted successfully", null));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
            }
        } catch (Exception e) {
        	System.out.println("Exception..");
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error while deleting product: " + e.getMessage());
        }
    }
    @GetMapping("/product-count")
    public ResponseEntity<?> getProductCount() {
        try {
            long total = productService.count(); // Retrieves the total document count
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("total", total);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error in product count");
            errorResponse.put("error", e.getMessage());
            errorResponse.put("success", false);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
    
    @GetMapping("/product-category/{slug}")
    public ResponseEntity<?> productCategory(@PathVariable String slug) {
        try {
            List<ProductDtoo> p = productService.getProductsByCategory(slug);
            return ResponseEntity.status(201).body(new ApiResponse(true,"Product List", p));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse(false, "Error while getting products", e.getMessage()));
        }
    }
    @GetMapping("/related-products/{pid}/{cid}")
    public ResponseEntity<?> getRelatedProducts(@PathVariable Long pid, @PathVariable Long cid) {
        try {
            List<ProductDtoo> p = productService.getRelatedProducts(pid, cid);
            return ResponseEntity.status(201).body(new ApiResponse(true,"Product List", p));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse(false, "Error In Search Product API", e.getMessage()));
        }
    }
    @GetMapping("/product-list/{page}")
    public ResponseEntity<?> getProductList(@PathVariable("page") int page) {
        try {
            List<ProductDtoo>p=productService.getProductList(page);
            return ResponseEntity.status(201).body(new ApiResponse(true,"Product List", p));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse(false, "Error in per page control", e.getMessage()));
        }
    }
    @GetMapping("/filter")
    public ResponseEntity<?> filterProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Boolean availability,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection
    ) {
        try {
            List<ProductDtoo> products = productService.filterProducts(
                    category, availability, minPrice, maxPrice, sortBy, sortDirection);

            return ResponseEntity.status(200).body(new ApiResponse(true, "Product List", products));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, "Error in filter", e.getMessage()));
        }
    }


    @GetMapping("/search/{keyword}")
    public ResponseEntity<?> searchProduct(@PathVariable String keyword) {
    
        try {
            List<ProductDtoo> p = productService.searchProducts(keyword);
            
            return ResponseEntity.status(201).body(new ApiResponse(true,"Product List", p));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse(false, "Error In Search Product API", e.getMessage()));
        }
    }
    
    @GetMapping("/get-product/{slug}")
	public ResponseEntity getSingleProduct(@PathVariable("slug") String slug) {
		try {
			System.out.println("1");
			ProductDtoo product=productService.getSingleProduct(slug);
			if (product == null) {
                Map<String, Object> notFoundResponse = new HashMap<>();
                notFoundResponse.put("success", false);
                notFoundResponse.put("message", "Product not found");
                return ResponseEntity.status(404).body(notFoundResponse);
            }
			System.out.println("2");
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Single Product Fetched");
            response.put("product", product);

            return ResponseEntity.ok().body(new ApiResponse(true, "Fetched", response));
		}catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error while getting single product");
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }	
    }
}
