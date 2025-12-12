//package ltts.com.security;
//
//
//
//
//import java.util.Date;
//
//import org.springframework.security.core.Authentication;
//import org.springframework.stereotype.Component;
//
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//
//@Component
//public class JwtTokenProvider 
//{
//
//	public String generateToken(Authentication authentication) 
//	{
//		String email = authentication.getName();
//		Date currentDate =new Date();
//		Date expireDate =new Date(currentDate.getTime()+360000);
//		String token=Jwts.builder()
//				.setSubject(email)
//				.setIssuedAt(currentDate)
//				.setExpiration(expireDate)
//				.signWith(SignatureAlgorithm.HS512,"JWTSecretKey")
//				.compact();
//		return token;
//	}
//	public String getEmailFromToken(String token)
//	{
//		Claims claims=Jwts.parser().setSigningKey("JWTSecretKey")
//				.parseClaimsJws(token).getBody();
//		return claims.getSubject();
//		
//	}
//}



package ltts.com.security;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import ltts.com.exception.APIException;

@Component
public class JwtTokenProvider {
    
    private static final String SECRET_KEY = "JWTSecretKeyJWTSecretKeyJWTSecretKey"; // Must be at least 32 characters
    private static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    private static final long EXPIRATION_TIME = 172800000; // Token expiration in milliseconds

    // Generates a JWT token
    public String generateToken(Authentication authentication) {
        String email = authentication.getName();
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + EXPIRATION_TIME);

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(currentDate)
                .setExpiration(expireDate)
                .signWith(KEY) // Uses the SecretKey for signing
                .compact();
    }

    // Extracts email (subject) from the token
    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(KEY) // Use the updated parserBuilder
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }
    public boolean validateToken(String token) 
    {
    
    	try {
    		Jwts.parserBuilder()
            .setSigningKey(KEY) // Use the updated parserBuilder
            .build()
            .parseClaimsJws(token);
    		return true;
    	}catch(Exception e) {
    		throw new APIException("Token Isse: "+e.getMessage());
    	}
    }
}

