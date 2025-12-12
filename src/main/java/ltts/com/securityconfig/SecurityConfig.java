 package ltts.com.securityconfig;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import ltts.com.security.JwtAuthenticaionFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Autowired
	private JwtAuthenticaionFilter jwtAuthenticaionFilter;
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
        .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Enable CORS
            .csrf(csrf -> csrf.disable())  // Updated syntax for disabling CSRF
            .authorizeHttpRequests(auth -> auth
            		.requestMatchers("/api/v1/auth/welcome").hasAuthority("ROLE_ADMIN")
                	.requestMatchers("/api/v1/auth/test").hasAuthority("ROLE_ADMIN")
                	.requestMatchers("/api/v1/auth/admin-auth").hasAuthority("ROLE_ADMIN")
                	.requestMatchers("/api/v1/auth/user-auth").hasAuthority("ROLE_USER")
                	.requestMatchers("/api/v1/auth/profile").hasAnyAuthority("ROLE_ADMIN", "ROLE_USER")
                	.requestMatchers("/api/v1/auth/users").hasAuthority("ROLE_ADMIN")
                	.requestMatchers("/api/v1/auth/update-role/{userId}").hasAuthority("ROLE_ADMIN")
                    .requestMatchers("/api/v1/auth/**").permitAll()
                    
                    .requestMatchers("/api/v1/category/create-category").hasAuthority("ROLE_ADMIN")
                    .requestMatchers("/api/v1/category/update-category/{id}").hasAuthority("ROLE_ADMIN")
                    .requestMatchers("/api/v1/category/delete-category/{id}").hasAuthority("ROLE_ADMIN")
                    .requestMatchers("/api/v1/category/**").permitAll()
                    
                    .requestMatchers("/api/v1/product/create-product").hasAuthority("ROLE_ADMIN")
                    .requestMatchers("/api/v1/product/update-product/{pid}").hasAuthority("ROLE_ADMIN")
                    .requestMatchers("/api/v1/product/delete-product/{pid}").hasAuthority("ROLE_ADMIN")
                    .requestMatchers("/api/v1/product/**").permitAll()
                    
                	.requestMatchers("/api/v1/orders/orders/buyer/{buyerId}").hasAnyAuthority("ROLE_ADMIN", "ROLE_USER")
                	.requestMatchers("/api/v1/orders/all-orders").hasAuthority("ROLE_ADMIN")
                	.requestMatchers("/api/v1/orders/order-status/{orderId}").hasAnyAuthority("ROLE_ADMIN","ROLE_USER")
                	.requestMatchers("/api/v1/orders/razorpay/order").hasAnyAuthority("ROLE_ADMIN","ROLE_USER")
                	.requestMatchers("/api/v1/orders/razorpay/verify").hasAnyAuthority("ROLE_ADMIN","ROLE_USER")
                	.requestMatchers("/api/v1/orders/braintree/checkout").hasAnyAuthority("ROLE_ADMIN","ROLE_USER")
                	.requestMatchers("/api/v1/orders/braintree/token").hasAnyAuthority("ROLE_ADMIN","ROLE_USER")
                	.requestMatchers("/api/v1/orders/{orderId}/pdf").hasAnyAuthority("ROLE_ADMIN","ROLE_USER")
                	.requestMatchers("/api/v1/orders/delete/{orderId}").hasAnyAuthority("ROLE_ADMIN","ROLE_USER")
                	.requestMatchers("/api/v1/orders/**").permitAll()
                	
                	.requestMatchers("/api/v1/address/add/{userId}").hasAnyAuthority("ROLE_ADMIN", "ROLE_USER")
                	.requestMatchers("/api/v1/address/user/{userId}").hasAnyAuthority("ROLE_ADMIN", "ROLE_USER")
                	.requestMatchers("/api/v1/address/update/{userId}/{addressId}").hasAnyAuthority("ROLE_ADMIN", "ROLE_USER")
                	.requestMatchers("/api/v1/address/delete/{userId}/{addressId}").hasAnyAuthority("ROLE_ADMIN", "ROLE_USER")
                	.requestMatchers("/api/v1/address/set-default/{userId}/{addressId}").hasAnyAuthority("ROLE_ADMIN", "ROLE_USER")
                	 .requestMatchers("/api/v1/address/**").permitAll()
                    .requestMatchers("/api/v1/reviews/**").permitAll()
                    .anyRequest().authenticated()
            );
        http.addFilterBefore(jwtAuthenticaionFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
    
 // CORS Configuration
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("https://19a7ce3bed6f.ngrok-free.app","https://9609-2401-4900-65b4-a8fd-5595-80dc-b4f4-8f94.ngrok-free.app/","http://localhost:3000"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
