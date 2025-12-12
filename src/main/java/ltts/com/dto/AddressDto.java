package ltts.com.dto;

public class AddressDto {
    private Long id;
    private String fullName;
    private String phoneNumber;
    private String addressLine;
    private String city;
    private String state;
    private String country;
    private String postalCode;
    private Boolean isDefault;
	public AddressDto() {
		super();
	}
	public AddressDto(Long id, String fullName, String phoneNumber, String addressLine, String city, String state,
			String country, String postalCode, Boolean isDefault) {
		super();
		this.id = id;
		this.fullName = fullName;
		this.phoneNumber = phoneNumber;
		this.addressLine = addressLine;
		this.city = city;
		this.state = state;
		this.country = country;
		this.postalCode = postalCode;
		this.isDefault = isDefault;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getAddressLine() {
		return addressLine;
	}
	public void setAddressLine(String addressLine) {
		this.addressLine = addressLine;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getPostalCode() {
		return postalCode;
	}
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}
	public Boolean getIsDefault() {
		return isDefault;
	}
	public void setIsDefault(Boolean isDefault) {
		this.isDefault = isDefault;
	}
	@Override
	public String toString() {
		return "AddressDto [id=" + id + ", fullName=" + fullName + ", phoneNumber=" + phoneNumber + ", addressLine="
				+ addressLine + ", city=" + city + ", state=" + state + ", country=" + country + ", postalCode="
				+ postalCode + ", isDefault=" + isDefault + "]";
	}
	
}
