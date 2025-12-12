package ltts.com.dto;

public class RoleUpdate {

	private String role;

	public RoleUpdate() {
		super();
	}

	public RoleUpdate(String role) {
		super();
		this.role = role;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	@Override
	public String toString() {
		return "RoleUpdate [role=" + role + "]";
	}
	
}
