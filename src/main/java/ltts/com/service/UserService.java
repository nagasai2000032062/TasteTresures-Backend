package ltts.com.service;


import java.util.List;

import ltts.com.dto.UserDto;
import ltts.com.model.Users;

public interface UserService 
{

	public Boolean findByEmail(String email);
	public Boolean findByEmailAndAnswer(String email,String answer);
	public UserDto register(UserDto userDto);
//	public Boolean forgotPasswordController(String email,String answer,String password);
	public UserDto updateProfile(UserDto userDto);
	public List<Users> getAllUsers();	
	public boolean resetPassword(String email, String newPassword);
	public boolean updateRoleById(Long id,String role);
}
