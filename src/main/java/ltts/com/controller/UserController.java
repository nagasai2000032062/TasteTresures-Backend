package ltts.com.controller;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ltts.com.dto.Login;
import ltts.com.dto.LoginResponse;
import ltts.com.dto.RoleUpdate;
import ltts.com.dto.UserDto;
import ltts.com.dto.UserResponse;
import ltts.com.model.Users;
import ltts.com.repository.UserRepo;
import ltts.com.dto.AddressDto;
import ltts.com.dto.ApiResponse;
import ltts.com.security.JwtTokenProvider;
import ltts.com.securityconfig.EmailService;
import ltts.com.securityconfig.OtpService;
import ltts.com.service.OrderService;
import ltts.com.service.UserService;

//@CrossOrigin(origins = "http://localhost:3000/")
@RestController
@RequestMapping("/api/v1/auth")
public class UserController 
{
	 @Autowired private OtpService otpService;
	    @Autowired private EmailService emailService;
	
	
	@Autowired
	private UserRepo userRepo;

	@Autowired
	private OrderService orderService;
	@Autowired
	private UserService userService;
	
	@Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    @GetMapping("/welcome")
	public String welcome()
	{
		return "Welcome to first spring web application";
	}
    
    @PostMapping("/otp/email/send")
    public ResponseEntity<?> sendEmailOtp(@RequestParam String email) {
        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Email is required");
        }
        String code = otpService.createEmailOtp(email, 300); // 5 minutes TTL
        // Send email
        emailService.sendEmail(
                email,
                "Your Email OTP",
                "Your verification OTP is: " + code + "\nThis code is valid for 5 minutes."
        );
        return ResponseEntity.ok(new ApiResponse(true, "Email OTP sent", null));
    }

    @PostMapping("/otp/email/verify")
    public ResponseEntity<?> verifyEmailOtp(@RequestParam String email, @RequestParam String otp) {
        boolean ok = otpService.verifyEmailOtp(email, otp);
        if (!ok) return ResponseEntity.badRequest().body("Invalid or expired OTP");
        return ResponseEntity.ok(new ApiResponse(true, "Email verified", null));
    }


    // === REGISTER (blocks until BOTH verified) ===
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserDto userDto) {
        try {
        	System.out.println("1");
            if (userDto.getName() == null || userDto.getName().isEmpty())
                return ResponseEntity.badRequest().body("Name is Required");
            if (userDto.getEmail() == null || userDto.getEmail().isEmpty())
                return ResponseEntity.badRequest().body("Email is Required");
            if (userDto.getPassword() == null || userDto.getPassword().isEmpty())
                return ResponseEntity.badRequest().body("Password is Required");
            if (userDto.getPhone() == null || userDto.getPhone().isEmpty())
                return ResponseEntity.badRequest().body("Phone no is Required");

            if (userDto.getAnswer() == null || userDto.getAnswer().isEmpty())
                return ResponseEntity.badRequest().body("Answer is Required");
            System.out.println("2");
            // Enforce pre-verified email & phone (from OTP service)
            boolean emailVerified = otpService.isEmailVerified(userDto.getEmail());
         
            if (!emailVerified) {
                return ResponseEntity.badRequest()
                        .body("Please verify BOTH email and phone via OTP before registering.");
            }
            System.out.println("3");
            boolean exists = userService.findByEmail(userDto.getEmail());
            if (exists) {
                return ResponseEntity.status(200).body("Already Registered, please login");
            }

            UserDto saved = userService.register(userDto);
            System.out.println("4");
            // Optional: clear OTPs after successful registration
            otpService.clear(userDto.getEmail());

            return ResponseEntity.ok(new ApiResponse(true, "created Successfully", saved));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ApiResponse(false, "Error in creation", null));
        }
    }
    @PostMapping("/login")
    public ResponseEntity loginUser(@RequestBody Login loginDto) {
    	try {
    		if(loginDto.getEmail()==null || loginDto.getPassword()==null)
        		return ResponseEntity.status(404).body("Invalid email or password");
        	Boolean b=userService.findByEmail(loginDto.getEmail());
        	if(b==false)
        		return ResponseEntity.status(404).body("Email is not registerd");
        	else {
            Authentication authentication = 
                    authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
                    );

            System.out.println(authentication.getName());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token= jwtTokenProvider.generateToken(authentication);
            Users user=userRepo.findByEmail(loginDto.getEmail());
            //return ResponseEntity.ok(new JWTAuthResponse(user.getId(),user.getName(),user.getEmail(),user.getPhone(),user.getAddress(),user.getAnswer(),user.getRole(),token));
            return ResponseEntity.status(200).body(new LoginResponse(
                    "login successfully",
                    true,
                    new UserResponse(
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getPhone(),
                        null,
                        user.getRole()
                    ),
                    token
                ));
        	}
    	}catch(Exception e) {
    		return ResponseEntity.status(500).body(new ApiResponse(false, "Error in login", null));
    	}
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPasswordController(@RequestParam String email,
                                                      @RequestParam String otp,
                                                      @RequestParam String newPassword) {
        try {
            // Validate input
            if (email == null || email.trim().isEmpty())
                return ResponseEntity.badRequest().body("Email is required");
            if (otp == null || otp.trim().isEmpty())
                return ResponseEntity.badRequest().body("OTP is required");
            if (newPassword == null || newPassword.length() < 6)
                return ResponseEntity.badRequest().body("Password must be at least 6 characters");

            // Verify OTP
            boolean otpValid = otpService.verifyEmailOtp(email, otp);
            if (!otpValid) {
                return ResponseEntity.status(400).body("Invalid or expired OTP");
            }

            // Reset password
            boolean success = userService.resetPassword(email, newPassword);
            if (success) {
                return ResponseEntity.ok(new ApiResponse(true, "Password reset successfully", null));
            } else {
                return ResponseEntity.status(404).body("User not found");
            }

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Something went wrong: " + e.getMessage());
        }
    }

    
    @PutMapping("/update-role/{userId}")
    public ResponseEntity<?> updateRole(@PathVariable Long userId, @RequestBody RoleUpdate roleUpdate) {
        try {
            boolean updatedRole = userService.updateRoleById(userId, roleUpdate.getRole());
            return ResponseEntity.ok(updatedRole);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error while updating Role: " + e.getMessage());
        }
    }
    
//    @PreAuthorize(value="ROLE_ADMIN")
    @GetMapping("/test")
    public ResponseEntity testController()
    {
    	return ResponseEntity.ok("true");
    }
//    @PreAuthorize(value="ROLE_USER")
    @GetMapping("/user-auth")
    public ResponseEntity getUser()
    {
    	return ResponseEntity.ok("true");
    }
    
//    @PreAuthorize(value="ROLE_ADMIN")
    @GetMapping("/admin-auth")
    public ResponseEntity getAdmin()
    {
    	return ResponseEntity.ok("true");
    }
//    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PutMapping("/profile")
	public ResponseEntity updateProfile(@RequestBody UserDto userDto) {    	
    	try {
    		if(userDto.getPassword()==null && userDto.getPassword().length()<6)
        		return ResponseEntity.badRequest().body("Password is required and must be at least 6 characters long");
    		UserDto updatedUser = userService.updateProfile(userDto);
    		return ResponseEntity.ok(new ApiResponse(true, "Profile updated successfully", updatedUser));
    	}catch(Exception e) {
    		return ResponseEntity.status(500).body(new ApiResponse(false, "Error WHile Update profile", null));
    	}

    }    
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
    	try {
    		return ResponseEntity.ok(userService.getAllUsers());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error creating order: " + e.getMessage());
        }
    }
}
