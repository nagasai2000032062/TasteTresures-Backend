package ltts.com.security;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


import ltts.com.model.Users;
import ltts.com.repository.UserRepo;

@Service
public class CustomUserDetailsService implements UserDetailsService
{
	@Autowired
	private UserRepo userRepository;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException{
		
//		Users user=userRepository.findByEmail(email).orElseThrow(
//				()->new UserNotFound(String.format("User with email : %s is not found",email))
//				);
//		return new CustomUserDetails(user);
		Users user=userRepository.findByEmail(email);
		if (user == null) {
			throw new UsernameNotFoundException("user not found");
		}
		return new CustomUserDetails(user);
	}
	
}
