package ltts.com.model;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import jakarta.persistence.Table;

@Entity
@Table(name = "order_items")
public class OrderItem {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ✅ Link to Order
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    // ✅ Link to Product
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // ✅ Track which variant was purchased
    @Column(nullable = false)
    private String variant; // e.g., "500g", "1kg" (you can also link to ProductPrice if preferred)

    // ✅ Price at time of purchase
    @Column(nullable = false)
    private Double price;

    // ✅ Quantity ordered
    @Column(nullable = false)
    private Integer quantity;

	public OrderItem() {
		super();
	}

	public OrderItem(Order order, Product product, String variant, Double price, Integer quantity) {
		super();
//		this.id = id;
		this.order = order;
		this.product = product;
		this.variant = variant;
		this.price = price;
		this.quantity = quantity;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public String getVariant() {
		return variant;
	}

	public void setVariant(String variant) {
		this.variant = variant;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	@Override
	public String toString() {
		return "OrderItem [id=" + id + ", order=" + order + ", product=" + product + ", variant=" + variant + ", price="
				+ price + ", quantity=" + quantity + "]";
	}
    
    
}
