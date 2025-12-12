package ltts.com.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.braintreegateway.BraintreeGateway;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

import jakarta.annotation.PostConstruct;
import ltts.com.dto.AddressDto;
import ltts.com.dto.OrderDto;
import ltts.com.dto.OrderItemDto;
import ltts.com.dto.OrderRequest;
import ltts.com.dto.ProductStatus;
import ltts.com.dto.UserDto;
import ltts.com.model.Order;
import ltts.com.model.OrderItem;
import ltts.com.model.OrderStatus;
import ltts.com.model.Product;
import ltts.com.model.Users;
import ltts.com.repository.OrdersRepo;
import ltts.com.repository.ProductRepo;
import ltts.com.repository.UserRepo;
import ltts.com.securityconfig.OrderPdfService;
import ltts.com.service.OrderService;
import ltts.com.service.ProductService;
import ltts.com.service.UserService;
import com.braintreegateway.*;

@RestController
@RequestMapping("/api/v1/orders")
public class OrdersController {

	@Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ProductService productService;

    @Autowired
	private ProductRepo productRepo;
    @Autowired
	private UserRepo userRepository;
    @Autowired
	private OrderService orderService;
	@Autowired
	private UserService userService;
    @Autowired
	private OrdersRepo orderRepository;
    
    @Autowired 
    private UserRepo userRepo;
    
    @Autowired
    private OrdersRepo ordersRepo;
    
    @Autowired
    private OrderPdfService orderPdfService;
    
    @Value("${razorpay.key_id}")
    private String keyId;
    private RazorpayClient razorpayClient;
    
    @Value("${razorpay.key_secret}")
    private String keySecret;
    
 // Braintree Keys
    @Value("${braintree.merchant-id}") private String merchantId;
    @Value("${braintree.public-key}") private String publicKey;
    @Value("${braintree.private-key}") private String privateKey;
    private BraintreeGateway gateway;
    
    @PostConstruct
    public void init() throws RazorpayException {
        gateway = new BraintreeGateway(
                Environment.SANDBOX,
                merchantId,
                publicKey,
                privateKey
        );
        System.out.println("✅ Braintree Gateway Initialized");

        razorpayClient = new RazorpayClient(keyId, keySecret);
        System.out.println("✅ Razorpay Client Initialized");
    }

    
 // -------------------------------------------------------------------------
    // 1️⃣ GET CLIENT TOKEN (React needs this)
    // -------------------------------------------------------------------------
    @GetMapping("/braintree/token")
    public ResponseEntity<?> getClientToken() {
        try {
            String token = gateway.clientToken().generate();
            return ResponseEntity.ok(Map.of("clientToken", token));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
    
 // -------------------------------------------------------------------------
    // 2️⃣ Braintree Checkout: Create Transaction + Save Order
    // -------------------------------------------------------------------------
    @PostMapping("/braintree/checkout")
    public ResponseEntity<?> checkout(@RequestBody Map<String, Object> body) {
        try {
            String nonce = (String) body.get("paymentMethodNonce");
            String email = (String) body.get("email");
            String address = (String) body.get("address");

            List<Map<String, Object>> cart = (List<Map<String, Object>>) body.get("cart");

            if (nonce == null || nonce.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Missing nonce"));
            }

            if (cart == null || cart.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Cart empty"));
            }

            // Calculate Total
            BigDecimal total = cart.stream()
                    .map(i -> new BigDecimal(i.get("price").toString())
                            .multiply(BigDecimal.valueOf(
                                    Integer.parseInt(i.get("quantity").toString())
                            )))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // Create Transaction
            TransactionRequest req = new TransactionRequest()
                    .amount(total)
                    .paymentMethodNonce(nonce)
                    .options()
                    .submitForSettlement(true)
                    .done();

            Result<Transaction> result = gateway.transaction().sale(req);

            if (!result.isSuccess()) {
                return ResponseEntity.badRequest().body(Map.of("error", result.getMessage()));
            }

            Transaction tx = result.getTarget();

            // Save Order
            Users buyer = userRepo.findByEmail(email);

            Order order = new Order();
            order.setBuyer(buyer);
            order.setOrderAddress(address);
            order.setStatus(OrderStatus.PROCESSING);

            Map<String, Object> paymentInfo = new HashMap<>();
            paymentInfo.put("transactionId", tx.getId());
            paymentInfo.put("amount", tx.getAmount().toString());
            paymentInfo.put("status", tx.getStatus().toString());
            order.setPayment(objectMapper.writeValueAsString(paymentInfo));

            // SAVE ORDER ITEMS
            for (Map<String, Object> i : cart) {
                Long pid = Long.valueOf(i.get("id").toString());
                Product p = productRepo.findById(pid)
                        .orElseThrow(() -> new RuntimeException("Product not found: " + pid));

                OrderItem item = new OrderItem();
                item.setOrder(order);
                item.setProduct(p);
                item.setPrice(Double.valueOf(i.get("price").toString()));
                item.setQuantity(Integer.parseInt(i.get("quantity").toString()));
                item.setVariant(i.get("weight") != null
                        ? i.get("weight").toString()
                        : (i.get("variant") != null ? i.get("variant").toString() : "DEFAULT"));

                order.getItems().add(item);
            }

            ordersRepo.save(order);

            return ResponseEntity.ok(
                    Map.of("success", true, "order", convertToDto(order))
            );

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/create_order")
    public ResponseEntity<?> createOrder(@RequestBody OrderRequest orderRequest) {
        try {
            OrderDto order = orderService.createOrder(orderRequest);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error creating order: " + e.getMessage());
        }
    }
//    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/orders/buyer/{buyerId}")
    public ResponseEntity<?> getOrdersByBuyerId(@PathVariable Long buyerId) {
    	try {
    		return ResponseEntity.ok(orderService.getOrdersByBuyerId(buyerId));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error creating order: " + e.getMessage());
        }
    }

//    @PreAuthorize(value="ROLE_ADMIN")
    @GetMapping("/all-orders")
    public ResponseEntity<?> getAllOrders() {
        try {
            List<OrderDto> orders = orderService.getAllOrders();
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error while getting orders: " + e.getMessage());
        }
    }
    
//    @PutMapping("/order-status/{orderId}")
//    public ResponseEntity<?> updateOrderStatus(@PathVariable Long orderId, @RequestBody ProductStatus productStatus) {
//        try {
//            // 1️⃣ Update order status normally
//            var updatedOrder = orderService.updateOrderStatus(orderId, productStatus.getStatus());
//
//            // 2️⃣ If the new status is CANCELLED, trigger Razorpay refund
//            if ("CANCELLED".equalsIgnoreCase(productStatus.getStatus())) {
//                try {
//                    // Fetch order from DB to get payment details
//                    Order order = orderRepository.findById(orderId)
//                            .orElseThrow(() -> new RuntimeException("Order not found"));
//
//                    // Parse payment JSON (we stored it in verifyRazorpayPayment)
//                    ObjectMapper mapper = new ObjectMapper();
//                    Map<String, Object> paymentInfo = mapper.readValue(order.getPayment(), Map.class);
//
//                    String paymentId = (String) paymentInfo.get("razorpayPaymentId");
//                    if (paymentId == null || paymentId.isEmpty()) {
//                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                                .body(Map.of("error", "No payment ID found for this order"));
//                    }
//
//                    // Calculate refund amount (in paise)
//                    int refundAmount = order.getItems().stream()
//                            .mapToInt(item -> (int) (item.getPrice() * item.getQuantity() * 100))
//                            .sum();
//
//                    // 3️⃣ Create refund request
//                    JSONObject refundRequest = new JSONObject();
//                    refundRequest.put("amount", refundAmount);
//
//                    com.razorpay.Refund refund = razorpayClient.payments.refund(paymentId, refundRequest);
//
//                    // 4️⃣ Store refund details in order object
//                    Map<String, Object> refundDetails = new HashMap<>();
//                    refundDetails.put("refundId", refund.get("id"));
//                    refundDetails.put("status", refund.get("status"));
//                    refundDetails.put("amount", refund.get("amount"));
//                    refundDetails.put("created_at", refund.get("created_at"));
//
//                    order.setPayment(mapper.writeValueAsString(refundDetails));
//                    orderRepository.save(order);
//
//                    System.out.println("✅ Refund initiated for Order ID: " + orderId);
//
//                } catch (Exception refundEx) {
//                    refundEx.printStackTrace();
//                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                            .body(Map.of("error", "Order cancelled but refund failed: " + refundEx.getMessage()));
//                }
//            }
//
//            return ResponseEntity.ok(updatedOrder);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(500).body("Error while updating order status: " + e.getMessage());
//        }
//    }
    @PutMapping("/order-status/{orderId}")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long orderId,
                                               @RequestBody ProductStatus statusReq) {
        try {
            // 1️⃣ Update status normally
        	Order order = ordersRepo.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Order not found"));
            OrderDto updated = orderService.updateOrderStatus(orderId, statusReq.getStatus());

            // 2️⃣ If CANCELLED → determine payment gateway → refund accordingly
            if ("CANCELLED".equalsIgnoreCase(statusReq.getStatus())) 
            {
                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> payment = mapper.readValue(order.getPayment(), Map.class);
                // ---------------------------------------------
                // 🔍 CHECK: RAZORPAY PAYMENT?
                // ---------------------------------------------
                if (payment.containsKey("razorpayPaymentId")) {

                	System.out.println("RP");
                	String paymentId = (String) payment.get("razorpayPaymentId");
                    if (paymentId == null || paymentId.isEmpty()) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(Map.of("error", "No payment ID found for this order"));
                    }

                    // Calculate refund amount (in paise)
                    int refundAmount = order.getItems().stream()
                            .mapToInt(item -> (int) (item.getPrice() * item.getQuantity() * 100))
                            .sum();


                    // 3️⃣ Create refund request
                    JSONObject refundRequest = new JSONObject();
                    refundRequest.put("amount", refundAmount);


                    System.out.println("1");
                    com.razorpay.Refund refund = razorpayClient.payments.refund(paymentId, refundRequest);

                    System.out.println("2");
                    // 4️⃣ Store refund details in order object
                    Map<String, Object> refundDetails = new HashMap<>();
                    refundDetails.put("refundId", refund.get("id"));
                    refundDetails.put("status", refund.get("status"));
                    refundDetails.put("amount", refund.get("amount"));
                    refundDetails.put("created_at", refund.get("created_at"));
                    order.setPayment(mapper.writeValueAsString(refundDetails));
                    orderRepository.save(order);

                    System.out.println("✅ Refund initiated for Order ID: " + orderId);

                    System.out.println("✅ Refund initiated for Order ID: " + orderId);


                    return ResponseEntity.ok(updated);
                }

                // ---------------------------------------------
                // 🔍 CHECK: BRAINTREE PAYMENT?
                // ---------------------------------------------
                else if (payment.containsKey("transactionId")) {
                	System.out.println("BT");
                    String txId = (String) payment.get("transactionId");
                    if (txId == null) {
                        return ResponseEntity.badRequest().body(Map.of("error", "No transaction found"));
                    }
                    Result<Transaction> refund = gateway.transaction().refund(txId);
                    if (!refund.isSuccess()) {
                        return ResponseEntity.status(500)
                                .body(Map.of("error", refund.getMessage()));
                    }
                    Transaction r = refund.getTarget();
                    Map<String, Object> refundInfo = new HashMap<>();
                    refundInfo.put("refundTransactionId", r.getId());
                    refundInfo.put("status", r.getStatus().toString());
                    refundInfo.put("amount", r.getAmount().toString());
                    order.setPayment(objectMapper.writeValueAsString(refundInfo));
                    ordersRepo.save(order);
                    return ResponseEntity.ok(updated);
                }         
                else {
                    return ResponseEntity.status(400)
                            .body(Map.of("error", "Unknown payment gateway. Unable to process refund."));
                }
            }
            else {
            ordersRepo.save(order);
            return ResponseEntity.ok(updated);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }


    

    // 1. Create Razorpay Order
    @PostMapping("/razorpay/order")
    public ResponseEntity<?> createRazorpayOrder(@RequestBody Map<String, Object> payload) {
        try {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> cart = (List<Map<String, Object>>) payload.get("cart");
            String email = (String) payload.get("email");
            String address = (String) payload.get("address");

            if (cart == null || cart.isEmpty()) return ResponseEntity.badRequest().body("Cart is empty");
            if (email == null || email.isBlank()) return ResponseEntity.badRequest().body("Email required");

            // Compute total in paise
            int totalAmount = cart.stream()
                    .mapToInt(item -> {
                        BigDecimal price = new BigDecimal(item.get("price").toString());
                        int qty = item.get("quantity") != null ? (int) item.get("quantity") : 1;
                        return price.multiply(BigDecimal.valueOf(qty)).multiply(BigDecimal.valueOf(100)).intValue();
                    })
                    .sum();

            JSONObject orderRequest = new JSONObject();
            
            System.out.println(totalAmount);
            orderRequest.put("amount", totalAmount); // in paise
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "order_rcpt_" + System.currentTimeMillis());
            orderRequest.put("payment_capture", 1);

            com.razorpay.Order order = razorpayClient.orders.create(orderRequest);

            // Return order info to frontend
            Map<String, Object> response = new HashMap<>();
            response.put("orderId", order.get("id"));
            response.put("amount", order.get("amount"));
            response.put("currency", order.get("currency"));
            response.put("key", keyId);
            response.put("email", email);
            response.put("address", address);
            response.put("cart", cart);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    // 2. Verify Razorpay Payment & Create Order
    @PostMapping("/razorpay/verify")
    public ResponseEntity<?> verifyRazorpayPayment(@RequestBody Map<String, Object> payload) {
    	System.out.println("00");
        try {
            String razorpayPaymentId = (String) payload.get("razorpay_payment_id");
            String razorpayOrderId = (String) payload.get("razorpay_order_id");
            String razorpaySignature = (String) payload.get("razorpay_signature");
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> cart = (List<Map<String, Object>>) payload.get("cart");
            String email = (String) payload.get("email");
            String address = (String) payload.get("address");
            System.out.println("1");
            // Verify signature
            String generatedSignature = hmacSHA256(razorpayOrderId + "|" + razorpayPaymentId, keySecret);
            if (!generatedSignature.equals(razorpaySignature)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Invalid payment signature"));
            }
            System.out.println("2");
            // Save order in DB (same as Braintree logic)
            Users buyer = userRepository.findByEmail(email);
            if (buyer == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Buyer not found"));

            Order order = new Order();
            order.setBuyer(buyer);
            order.setStatus(OrderStatus.PROCESSING);
            order.setOrderAddress(address);
            System.out.println("3");
            String paymentDetailsJson = objectMapper.writeValueAsString(Map.of(
                "razorpayPaymentId", razorpayPaymentId,
                "razorpayOrderId", razorpayOrderId,
                "status", "SUCCESS"
            ));
            order.setPayment(paymentDetailsJson);
            System.out.println("4");
            // Map cart → OrderItems
            for (Map<String, Object> item : cart) {
                Long productId = Long.valueOf(item.get("id").toString());
                Product product = productRepo.findById(productId).orElseThrow(() -> new RuntimeException("Product not found: " + productId));

                String variant = item.get("variant") != null ? item.get("variant").toString() : "DEFAULT";
                BigDecimal priceBD = new BigDecimal(item.get("price").toString());
                Double price = priceBD.doubleValue();
                Integer quantity = item.get("quantity") != null ? (int) item.get("quantity") : 1;
                String varient1= (String) item.get("weight");
                System.out.println(varient1);
                OrderItem oi = new OrderItem();
                oi.setOrder(order);
                oi.setProduct(product);
                oi.setVariant(varient1);
                oi.setPrice(price);
                oi.setQuantity(quantity);
                order.getItems().add(oi);
            }

            orderRepository.save(order);
            OrderDto orderDto = convertToDto(order);
            System.out.println("5");
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "razorpayPaymentId", razorpayPaymentId,
                    "order", orderDto
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    // HMAC SHA256 helper
    private String hmacSHA256(String data, String key) throws Exception {
        javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA256");
        javax.crypto.spec.SecretKeySpec secretKeySpec = new javax.crypto.spec.SecretKeySpec(key.getBytes(), "HmacSHA256");
        mac.init(secretKeySpec);
        byte[] hash = mac.doFinal(data.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
    
    @GetMapping("/{orderId}/pdf")
    public ResponseEntity<byte[]> downloadOrderPdf(@PathVariable Long orderId) {
        try {
            byte[] pdfBytes = orderPdfService.generatePdfForOrder(orderId);
            String filename = "order-" + orderId + ".pdf";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }

     
    @DeleteMapping("/delete/{orderId}")
    public ResponseEntity<?> deleteOrder(@PathVariable Long orderId) {
        try {
            orderService.deleteOrder(orderId);
            return ResponseEntity.ok("Order deleted successfully.");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete order.");
        }
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
