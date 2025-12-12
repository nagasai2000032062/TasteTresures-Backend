package ltts.com.securityconfig;



import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpService {

 private static class OtpData {
     String otp;
     long expiresAt; // epoch millis
     boolean verified;

     OtpData(String otp, long expiresAt) {
         this.otp = otp;
         this.expiresAt = expiresAt;
         this.verified = false;
     }
 }

 private final Map<String, OtpData> emailOtps = new ConcurrentHashMap<>();
 private final Random random = new Random();

 private String generate6Digit() {
     return String.format("%06d", random.nextInt(1_000_000));
 }

 public String createEmailOtp(String email, long ttlSeconds) {
     String code = generate6Digit();
     emailOtps.put(email.toLowerCase().trim(),
             new OtpData(code, Instant.now().toEpochMilli() + ttlSeconds * 1000));
     return code;
 }

 public boolean verifyEmailOtp(String email, String code) {
     OtpData data = emailOtps.get(email.toLowerCase().trim());
     if (data == null) return false;
     long now = Instant.now().toEpochMilli();
     if (now > data.expiresAt) return false;
     if (!data.otp.equals(code)) return false;
     data.verified = true;
     return true;
 }

 public boolean isEmailVerified(String email) {
     OtpData data = emailOtps.get(email.toLowerCase().trim());
     return data != null && data.verified && Instant.now().toEpochMilli() <= data.expiresAt;
 }

 

 public void clear(String email) {
     if (email != null) emailOtps.remove(email.toLowerCase().trim());
 }
}
