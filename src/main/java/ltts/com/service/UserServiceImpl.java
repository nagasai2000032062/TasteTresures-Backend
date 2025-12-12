package ltts.com.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;



import jakarta.transaction.Transactional;
import ltts.com.dto.AddressDto;
import ltts.com.dto.UserDto;
import ltts.com.model.Users;
import ltts.com.repository.UserRepo;

@Service
public class UserServiceImpl implements UserService
{
	@Autowired
	private UserRepo userRepo;
	@Autowired
	private ModelMapper modelMapper;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Override
	public UserDto register(UserDto userDto) {
		if (userDto.getRole() == null || userDto.getRole().isEmpty()) {
	        userDto.setRole("ROLE_USER");
	    }
		System.out.println("5");
		userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
		Users user=new Users();
		user.setName(userDto.getName());
		user.setEmail(userDto.getEmail());
		user.setAnswer(userDto.getAnswer());
		user.setPassword(userDto.getPassword());
		user.setPhone(userDto.getPhone());
		user.setRole(userDto.getRole());
		System.out.println("5");
		user=userRepo.save(user);
		
		System.out.println("Save1..");
		if(user!=null)
		{
			
			userDto=convertToDto(user);
			System.out.println("Save2..");
			return userDto;
		}
		else
			return null;
	}

//	@Transactional
//	@Override
//	public Boolean forgotPasswordController(String email, String answer, String password) {
//		Users user=userRepo.findByEmailAndAnswer(email, answer);
//		if(user!=null)
//		{
//			user.setPassword(passwordEncoder.encode(password));
//			userRepo.save(user);
//			return true;
//		}
//		else
//		{
//			return false;
//		}
//	}

	@Transactional
	@Override
	public boolean resetPassword(String email, String newPassword) {
	    Users user = userRepo.findByEmail(email);
	    if (user != null) {
	        user.setPassword(passwordEncoder.encode(newPassword));
	        userRepo.save(user);
	        return true;
	    }
	    return false;
	}

	@Override
	public Boolean findByEmail(String email) {
		Users user= userRepo.findByEmail(email);
		if(user==null)
			return false;
		else
			return true;
	}

	@Override
	public Boolean findByEmailAndAnswer(String email, String answer) {
		
		Users user=userRepo.findByEmailAndAnswer(email, answer);
		if(user!=null)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	@Override
	public UserDto updateProfile(UserDto userDto) {
		Users user= userRepo.findByEmail(userDto.getEmail());
		user.setPassword(passwordEncoder.encode(userDto.getPassword()));
		user.setName(userDto.getName());
		user.setPhone(userDto.getPhone());
		user=userRepo.save(user);
		userDto=convertToDto(user);
		userDto.setPassword(null);
		return userDto;
	}	
	
	public List<Users> getAllUsers()
	{
		List<Users>ob=userRepo.findAll();
		return ob;
	}
	public boolean updateRoleById(Long id,String role)
	{
		Users user=userRepo.findById(id).get();
		if(user!=null)
		{
			user.setRole(role);
			userRepo.save(user);
			return true;
		}
		else
			return false;
	}
	
	public UserDto convertToDto(Users user)
	{
		List<AddressDto>AllAddress=user.getAddresses().stream()
                .map(add -> new AddressDto(add.getId(), add.getFullName(),add.getPhoneNumber(),add.getAddressLine(),add.getCity(),
                		add.getState(),add.getCountry(),add.getPostalCode(),add.getIsDefault()))
                .collect(Collectors.toList());
		UserDto userDto = new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                null, // avoid exposing password
                user.getPhone(),
                AllAddress,
                user.getAnswer(),
                user.getRole()
        );
		return userDto;
	}
	
}
