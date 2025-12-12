package ltts.com.dto;

public class ProductStatus {

	String status;

	public ProductStatus() {
		super();
	}

	public ProductStatus(String status) {
		super();
		this.status = status;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "ProductStatus [status=" + status + "]";
	}

	
}
