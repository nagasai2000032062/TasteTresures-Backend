package ltts.com.dto;

import java.util.List;

public class UserResponse {

	private Long id;
    private String name;
    private String email;
    private String phone;
    private List<AddressDto> addresses;
    private String role;
	public UserResponse() {
		super();
	}
	public UserResponse(Long id, String name, String email, String phone, List<AddressDto> addresses, String role) {
		super();
		this.id = id;
		this.name = name;
		this.email = email;
		this.phone = phone;
		this.addresses = addresses;
		this.role = role;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public List<AddressDto> getAddresses() {
		return addresses;
	}
	public void setAddresses(List<AddressDto> addresses) {
		this.addresses = addresses;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	@Override
	public String toString() {
		return "UserResponse [id=" + id + ", name=" + name + ", email=" + email + ", phone=" + phone + ", addresses="
				+ addresses + ", role=" + role + "]";
	}
}
