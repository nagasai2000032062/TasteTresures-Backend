package ltts.com.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.Transactional;
import ltts.com.dto.CategoryDto;
import ltts.com.dto.ProductDto;
import ltts.com.dto.ProductDtoo;
import ltts.com.dto.ProductPriceDto;
import ltts.com.model.Category;
import ltts.com.model.Product;
import ltts.com.model.ProductPrice;
import ltts.com.repository.CategoryRepo;
import ltts.com.repository.ProductRepo;
import ltts.com.repository.orderItemRepository;
import ltts.com.securityconfig.ImageUtils;

@Service
public class ProductServiceImplements implements ProductService {

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private CategoryRepo categoryRepo;
    
    @Autowired
    private orderItemRepository orderItemRepo; 

    @Autowired
    private ModelMapper modelMapper;

    // Helper to generate slug
    private String slugify(String input) {
        return input.toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("-$", "");
    }

    @Override
    public ProductDtoo createProduct(ProductDto productDto) throws IOException {
        // Validate input
        if (productDto.getName() == null || productDto.getName().isEmpty()) {
            throw new IllegalArgumentException("Product name is required");
        }
        if (productDto.getCategory() == null || productDto.getCategory().isEmpty()) {
            throw new IllegalArgumentException("Category is required");
        }
        if (productDto.getImages() == null || productDto.getImages().isEmpty()) {
            throw new IllegalArgumentException("At least one image is required");
        }
        if (productDto.getPrices() == null || productDto.getPrices().isEmpty()) {
            throw new IllegalArgumentException("At least one variant is required");
        }

        // Map basic fields excluding images and variants
        Product product = new Product();
        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setQuantity(productDto.getQuantity());

        // Handle images manually
        List<byte[]> imageList = new ArrayList<>();
        List<String> contentTypes = new ArrayList<>();
        for (MultipartFile file : productDto.getImages()) {
            if (file.getSize() > MAX_FILE_SIZE) {
                throw new IOException("File size exceeds limit: " + file.getOriginalFilename());
            }
            imageList.add(ImageUtils.compressImage(file.getBytes()));
            contentTypes.add(file.getContentType());
        }
        product.setImages(imageList);
        product.setImageContentTypes(contentTypes);

        // Set category
        Category category = categoryRepo.findByName(productDto.getCategory());
        if (category == null) {
            throw new IllegalArgumentException("Category not found");
        }
        product.setCategory(category);

        // Set slug
        product.setSlug(slugify(product.getName()));

        // Handle variants manually
        List<ProductPrice> variantList = new ArrayList<>();
        for (ProductPriceDto variantDto : productDto.getPrices()) {
            ProductPrice variant = new ProductPrice();
            variant.setWeight(variantDto.getWeight());
            variant.setPrice(variantDto.getPrice());
            variant.setProduct(product);
            variantList.add(variant);
        }
        product.setVariants(variantList);
        if(productDto.getQuantity()>0)
        	product.setAvailability(true);
        else
        	product.setAvailability(false);
//        product.setShipping(productDto.getShipping());
        // Save product
        product= productRepo.save(product);
        return convertToDto(product);
    }
    public ProductDtoo updateProduct(Long id, ProductDto productDto) throws IOException {
        // Validate input
        if (productDto.getName() == null || productDto.getName().isEmpty()) {
            throw new IllegalArgumentException("Product name is required");
        }
        if (productDto.getCategory() == null || productDto.getCategory().isEmpty()) {
            throw new IllegalArgumentException("Category is required");
        }
        if (productDto.getImages() == null || productDto.getImages().isEmpty()) {
            throw new IllegalArgumentException("At least one image is required");
        }
        if (productDto.getPrices() == null || productDto.getPrices().isEmpty()) {
            throw new IllegalArgumentException("At least one variant is required");
        }

        // Fetch existing product
        Product product = productRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        // Update basic fields
        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setQuantity(productDto.getQuantity());

        // Update category
        Category category = categoryRepo.findByName(productDto.getCategory());
        if (category == null) {
            throw new IllegalArgumentException("Category not found");
        }
        product.setCategory(category);

        // Update slug
        product.setSlug(slugify(product.getName()));

        // Update images
        List<byte[]> imageList = new ArrayList<>();
        List<String> contentTypes = new ArrayList<>();
        for (MultipartFile file : productDto.getImages()) {
            if (file.getSize() > MAX_FILE_SIZE) {
                throw new IOException("File size exceeds limit: " + file.getOriginalFilename());
            }
            imageList.add(ImageUtils.compressImage(file.getBytes()));
            contentTypes.add(file.getContentType());
        }
        product.setImages(imageList);
        product.setImageContentTypes(contentTypes);

        // Handle variants safely (orphan removal)
        product.getVariants().clear();  // clear old variants
        for (ProductPriceDto variantDto : productDto.getPrices()) {
            ProductPrice variant = new ProductPrice();
            variant.setWeight(variantDto.getWeight());
            variant.setPrice(variantDto.getPrice());
            variant.setProduct(product);
            product.getVariants().add(variant);  // add to existing list
        }
//        product.setShipping(productDto.getShipping());
        if(productDto.getQuantity()>0)
        	product.setAvailability(true);
        else
        	product.setAvailability(false);
        // Save and return updated product
        product= productRepo.save(product);
        return convertToDto(product);
    }

    @Override
    public List<ProductDtoo> getAllProducts() {
        List<Product> products = productRepo.findAll(
                PageRequest.of(0, 12, Sort.by(Sort.Direction.DESC, "createdAt"))
        ).getContent();

        return products.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    @Override
	public ProductDtoo getSingleProduct(String slug) {
		Product product = productRepo.findBySlug(slug);
		return convertToDto(product);
	}
    @Override
    public Map<String, byte[]> getProductPhotoByIndex(Long id, int index) {
        Optional<Product> productOpt = productRepo.findById(id);
        Map<String, byte[]> imagesMap = new HashMap<>();

        if (productOpt.isEmpty()) {
            return imagesMap; // empty if product not found
        }

        Product p = productOpt.get();

        if (p.getImages() == null || p.getImages().isEmpty() || p.getImageContentTypes() == null) {
            return imagesMap;
        }

        if (index < 0 || index >= p.getImages().size()) {
            return imagesMap; // invalid index
        }

        byte[] imageData = ImageUtils.decompressImage(p.getImages().get(index));
        String contentType = p.getImageContentTypes().get(index);
        imagesMap.put(contentType, imageData);

        return imagesMap;
    }
    public int getNoOfImgesById(Long id)
    {
    	Product product = productRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
    	return product.getImages().size();
    }

//    @Transactional
//	@Override
//	public boolean deleteProduct(Long id) {
//	    Optional<Product> product = productRepo.findById(id);
//	    if (product.isPresent()) {
//	        productRepo.deleteById(id);
//	        return true;
//	    } else {
//	        return false;
//	    }
//	}
    
    @Transactional
    @Override
    public boolean deleteProduct(Long id) {
        if (!productRepo.existsById(id)) return false;

        // delete related order items first
        orderItemRepo.deleteByProductId(id);

        // now delete product
        productRepo.deleteById(id);
        return true;
    }

    @Override
	public long count() {
		return productRepo.count();
	}
    @Override
	public List<ProductDtoo> getProductsByCategory(String slug) {
		Category category = categoryRepo.findBySlug(slug);
		List<Product> products = productRepo.findByCategory(category);
		if(products==null)
			System.out.println("not found......");
		return products.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
	}
    @Override
	public List<ProductDtoo> getRelatedProducts(Long productId, Long categoryId) {
		List<Product>products=productRepo.findRelatedProducts(productId, categoryId);
		return products.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
	}
    
    @Override
	public List<ProductDtoo> getProductList(int page)
	{
		int perPage = 6; // Number of products per page
        int offset = (page - 1) * perPage;
        List<Product> products = productRepo.findAll(PageRequest.of(page - 1, perPage, Sort.by("createdAt").descending())).getContent();
        return products.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
	}
//    public List<ProductDtoo> filterProducts(String category, Boolean availability, Double minPrice, Double maxPrice) {
//    	List<Product> products = productRepo.filterProducts(category, availability, minPrice, maxPrice);
//    	return products.stream()
//                .map(this::convertToDto)
//                .collect(Collectors.toList());
//    }
    public List<ProductDtoo> filterProducts(
            String category,
            Boolean availability,
            Double minPrice,
            Double maxPrice,
            String sortBy,
            String sortDirection
    ) {
        Sort sort;

        // Handle sort by price specifically
        if ("price".equalsIgnoreCase(sortBy)) {
            sort = Sort.by("variants.price"); // sorting by variant price
        } else {
            sort = Sort.by(sortBy != null ? sortBy : "id"); // default sort by id
        }

        if ("desc".equalsIgnoreCase(sortDirection)) {
            sort = sort.descending();
        } else {
            sort = sort.ascending();
        }

        List<Product> products = productRepo.filterProducts(category, availability, minPrice, maxPrice, sort);

        return products.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }


   
    @Override
	public List<ProductDtoo> searchProducts(String keyword) {
		List<Product>products=productRepo.searchProductsByKeyword(keyword);
		return products.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
	}
    
    private ProductDtoo convertToDto(Product p) {
        CategoryDto c = new CategoryDto(
                p.getCategory().getId(),
                p.getCategory().getName(),
                p.getCategory().getSlug()
        );

        List<ProductPriceDto> productPriceDtos = p.getVariants().stream()
                .map(price -> new ProductPriceDto(price.getId(), price.getWeight(), price.getPrice()))
                .collect(Collectors.toList());

        return new ProductDtoo(
                p.getId(),
                p.getName(),
                p.getSlug(),
                p.getDescription(),
                c.getName(),
                c,
                p.getQuantity(),
                p.getImages(),
                productPriceDtos,
                p.getImageContentTypes(),
                p.getAvailability()
        );
    }

}
