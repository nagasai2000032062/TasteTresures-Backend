package ltts.com.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ltts.com.dto.AddressDto;
import ltts.com.dto.CategoryDto;
import ltts.com.dto.OrderDto;
import ltts.com.dto.OrderItemDto;
import ltts.com.dto.OrderRequest;
import ltts.com.dto.ProductDto;
import ltts.com.dto.ProductDtoo;
import ltts.com.dto.ProductPriceDto;
import ltts.com.dto.UserDto;
import ltts.com.model.Order;
import ltts.com.model.OrderItem;
import ltts.com.model.OrderStatus;
import ltts.com.model.Product;
import ltts.com.model.Users;
import ltts.com.repository.OrdersRepo;
import ltts.com.repository.ProductRepo;
import ltts.com.repository.UserRepo;

//@Service
//public class OrderServiceImpl implements OrderService
//{
//
//	@Autowired
//	private UserRepo userRepo;
//	@Autowired
//	private ModelMapper modelMapper;
//	@Autowired
//	private OrdersRepo ordersRepo;
//	@Autowired
//	private ProductRepo productRepo;
//	@Override
//    public OrderDto createOrder(OrderRequest orderRequest) {
//        // ✅ Validate buyer
//        Users buyer = userRepo.findById(orderRequest.getBuyerId())
//                .orElseThrow(() -> new RuntimeException("Buyer not found with ID: " + orderRequest.getBuyerId()));
//
//        // ✅ Validate products
//        List<Product> products = productRepo.findAllById(orderRequest.getProductIds());
//        if (products.isEmpty()) {
//            throw new RuntimeException("No valid products found for given IDs: " + orderRequest.getProductIds());
//        }
//        if (products.size() != orderRequest.getProductIds().size()) {
//            throw new RuntimeException("Some product IDs do not exist. Requested: "
//                    + orderRequest.getProductIds() + ", Found: "
//                    + products.stream().map(Product::getId).toList());
//        }
//
//        // ✅ Create and save order
//        Order order = new Order();
//        order.setProducts(products);
//        order.setPayment(orderRequest.getPaymentDetails());
//        order.setBuyer(buyer);
//        order.setStatus(OrderStatus.NOT_PROCESSED);
//
//        ordersRepo.save(order);
//
//        return convertToDto(order);
//    }
//	
//	@Override
//	public List<OrderDto> getOrdersByBuyerId(Long buyerId) {
//        List<Order> orders = ordersRepo.findByBuyerId(buyerId);
//        return orders.stream()
//                .map(this::convertToDto)
//                .collect(Collectors.toList());
//    }
//
//	@Override
//	public List<OrderDto> getAllOrders() {
//        List<Order> orders = ordersRepo.findAllByOrderByCreatedAtDesc();
//        return orders.stream()
//                .map(this::convertToDto)
//                .collect(Collectors.toList());
//    }
//	@Override
//	 public OrderDto updateOrderStatus(Long orderId, String status) throws Exception {
//	        Order order = ordersRepo.findById(orderId)
//	                .orElseThrow(() -> new Exception("Order not found with ID: " + orderId));
//	        if(status.equals("NOT_PROCESSED"))
//	        order.setStatus(OrderStatus.NOT_PROCESSED);
//	        if(status.equals("PROCESSING")) {
//	        	order.setStatus(OrderStatus.PROCESSING);}
//	        if(status.equals("SHIPPED"))
//	        	order.setStatus(OrderStatus.SHIPPED);
//	        if(status.equals("DELIVERED"))
//	        	order.setStatus(OrderStatus.DELIVERED);
//	        if(status.equals("CANCELLED"))
//	        	order.setStatus(OrderStatus.CANCELLED);
//	         ordersRepo.save(order);
//	         return convertToDto(order);
//	    }
//	
//	private OrderDto convertToDto(Order order) {
//        UserDto buyerDto = new UserDto(
//                order.getBuyer().getId(),
//                order.getBuyer().getName(),
//                order.getBuyer().getEmail(),
//                null, // Avoid exposing sensitive data like passwords
//                order.getBuyer().getPhone(),
//                order.getBuyer().getAddress(),
//                order.getBuyer().getAnswer(),
//                order.getBuyer().getRole()
//        );
//        
//        
//        List<ProductDtoo> productDtos = order.getProducts().stream()
//                .map(product -> {
//                    // Create CategoryDto instance
//                    CategoryDto categoryDto = new CategoryDto(
//                            product.getCategory().getId(),
//                            product.getCategory().getName(),
//                            product.getCategory().getSlug()
//                    );
//                    List<ProductPriceDto> productPriceDtos = product.getVariants().stream()
//                            .map(price -> new ProductPriceDto(price.getId(), price.getWeight(), price.getPrice()))
//                            .collect(Collectors.toList());
//                    // Return ProductDto with the categoryDto and other fields
//                    return new ProductDtoo(
//                            product.getId(),
//                            product.getName(),
//                            product.getSlug(),
//                            product.getDescription(),
//                            categoryDto.getName(), // If you want to include category name directly
//                            categoryDto, // Or include full category details
//                            product.getQuantity(),
//                            product.getImages(),
//                            productPriceDtos,
//                            product.getImageContentTypes(),
//                            product.getAvailability()
//                    );
//                })
//                .collect(Collectors.toList());
//
//        
//        return new OrderDto(
//                order.getId(),
//                productDtos,
//                buyerDto,
//                order.getPayment(),
//                order.getStatus().name(),
//                order.getCreatedAt()
//        );
//    }
//}
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private OrdersRepo ordersRepo;

    @Autowired
    private ProductRepo productRepo;

    @Override
    public OrderDto createOrder(OrderRequest orderRequest) {
        Users buyer = userRepo.findById(orderRequest.getBuyerId())
                .orElseThrow(() -> new RuntimeException("Buyer not found"));

        Order order = new Order();
        order.setBuyer(buyer);
        order.setPayment(orderRequest.getPaymentDetails());
        order.setStatus(OrderStatus.NOT_PROCESSED);

        List<OrderItem> orderItems = orderRequest.getCartItems().stream().map(cartItem -> {
            Product product = productRepo.findById(cartItem.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found with ID " + cartItem.getProductId()));

            return new OrderItem(
                    order,
                    product,
                    cartItem.getVariant(),
                    cartItem.getPrice(),
                    cartItem.getQuantity()
            );
        }).toList();

        order.setItems(orderItems);

        ordersRepo.save(order);

        return convertToDto(order);
    }


    @Override
    public List<OrderDto> getOrdersByBuyerId(Long buyerId) {
        List<Order> orders = ordersRepo.findByBuyerId(buyerId);
        return orders.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderDto> getAllOrders() {
        List<Order> orders = ordersRepo.findAllByOrderByCreatedAtDesc();
        return orders.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public OrderDto updateOrderStatus(Long orderId, String status) throws Exception {
        Order order = ordersRepo.findById(orderId)
                .orElseThrow(() -> new Exception("Order not found with ID: " + orderId));

        switch (status.toUpperCase()) {
            case "NOT_PROCESSED" -> order.setStatus(OrderStatus.NOT_PROCESSED);
            case "PROCESSING" -> order.setStatus(OrderStatus.PROCESSING);
            case "SHIPPED" -> order.setStatus(OrderStatus.SHIPPED);
            case "DELIVERED" -> order.setStatus(OrderStatus.DELIVERED);
            case "CANCELLED" -> order.setStatus(OrderStatus.CANCELLED);
            default -> throw new IllegalArgumentException("Invalid order status: " + status);
        }

        ordersRepo.save(order);
        return convertToDto(order);
    }

    // ✅ Convert Entity → DTO
//    private OrderDto convertToDto(Order order) {
//        // Convert buyer
//        UserDto buyerDto = new UserDto(
//                order.getBuyer().getId(),
//                order.getBuyer().getName(),
//                order.getBuyer().getEmail(),
//                null, // avoid exposing password
//                order.getBuyer().getPhone(),
//                order.getBuyer().getAddress(),
//                order.getBuyer().getAnswer(),
//                order.getBuyer().getRole()
//        );
//
//        // Convert order items -> products
//        List<ProductDtoo> productDtos = order.getItems().stream()
//                .map(item -> {
//                    Product product = item.getProduct();
//
//                    CategoryDto categoryDto = new CategoryDto(
//                            product.getCategory().getId(),
//                            product.getCategory().getName(),
//                            product.getCategory().getSlug()
//                    );
//
//                    List<ProductPriceDto> productPriceDtos = product.getVariants().stream()
//                            .map(price -> new ProductPriceDto(price.getId(), price.getWeight(), price.getPrice()))
//                            .collect(Collectors.toList());
//
//                    return new ProductDtoo(
//                            product.getId(),
//                            product.getName(),
//                            product.getSlug(),
//                            product.getDescription(),
//                            categoryDto.getName(),
//                            categoryDto,
//                            product.getQuantity(),
//                            product.getImages(),
//                            productPriceDtos,
//                            product.getImageContentTypes(),
//                            product.getAvailability()
//                    );
//                })
//                .collect(Collectors.toList());
//
//        // Build final order DTO
//        return new OrderDto(
//                order.getId(),
//                productDtos,
//                buyerDto,
//                order.getPayment(),
//                order.getStatus().name(),
//                order.getCreatedAt()
//        );
//    }


    public void deleteOrder(Long orderId) {

        Order order = ordersRepo.findById(orderId)
                .orElseThrow(() -> new IllegalStateException("Order not found."));

        // Validate order status
        if (order.getStatus() != OrderStatus.CANCELLED &&
            order.getStatus() != OrderStatus.DELIVERED) {

            throw new IllegalStateException(
                "Order can be deleted only if status is CANCELLED or DELIVERED."
            );
        }

        ordersRepo.delete(order);
    }


    private OrderDto convertToDto(Order order) {
    	List<AddressDto>AllAddress=order.getBuyer().getAddresses().stream()
                .map(add -> new AddressDto(add.getId(), add.getFullName(),add.getPhoneNumber(),add.getAddressLine(),add.getCity(),
                		add.getState(),add.getCountry(),add.getPostalCode(),add.getIsDefault()))
                .collect(Collectors.toList());
        UserDto buyerDto = new UserDto(
                order.getBuyer().getId(),
                order.getBuyer().getName(),
                order.getBuyer().getEmail(),
                null, // avoid exposing password
                order.getBuyer().getPhone(),
                AllAddress,
                order.getBuyer().getAnswer(),
                order.getBuyer().getRole()
        );

        List<OrderItemDto> itemDtos = order.getItems().stream()
                .map(item -> {
                    Byte[] firstImage = null;
                    if (item.getProduct().getImages() != null && !item.getProduct().getImages().isEmpty()) {
                        // if Product.getImages() returns List<byte[]> → convert to Byte[]
                        byte[] img = item.getProduct().getImages().get(0);
                        firstImage = new Byte[img.length];
                        for (int i = 0; i < img.length; i++) {
                            firstImage[i] = img[i];
                        }
                    }

                    return new OrderItemDto(
                            item.getProduct().getId(),
                            item.getProduct().getName(),
                            item.getVariant(),
                            item.getPrice(),
                            item.getQuantity(),
                            item.getProduct().getImages()
                    );
                })
                .collect(Collectors.toList());

        return new OrderDto(
                order.getId(),
                itemDtos,
                buyerDto,
                order.getPayment(),
                order.getOrderAddress(),
                order.getStatus().name(),
                order.getCreatedAt()
        );
    }
}
