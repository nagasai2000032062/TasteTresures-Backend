package ltts.com.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ltts.com.dto.AddressDto;
import ltts.com.dto.ApiResponse;
import ltts.com.dto.CategoryDto;
import ltts.com.model.Address;
import ltts.com.service.AddressService;

@RestController
@RequestMapping("/api/v1/address")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @PostMapping("/add/{userId}")
    public ResponseEntity addAddress(@PathVariable Long userId, @RequestBody AddressDto a) {
    	try {
    		if(a.getFullName() == null || a.getFullName().isEmpty())
        		return ResponseEntity.badRequest().body("Name is Required");
        	if(a.getPhoneNumber() == null || a.getPhoneNumber().isEmpty())
        		return ResponseEntity.badRequest().body("Phone Number is Required");
        	if(a.getCity() == null || a.getCity().isEmpty())
        		return ResponseEntity.badRequest().body("City is Required");
        	if(a.getCountry() == null || a.getCountry().isEmpty())
        		return ResponseEntity.badRequest().body("Country is Required");
        	if(a.getState() == null || a.getState().isEmpty())
        		return ResponseEntity.badRequest().body("State is Required");
        	if(a.getAddressLine() == null || a.getAddressLine().isEmpty())
        		return ResponseEntity.badRequest().body("Address is Required");
        	if(a.getPostalCode() == null || a.getPostalCode().isEmpty())
        		return ResponseEntity.badRequest().body("Postal Code is Required");
        	a=addressService.addAddress(userId, a);
        	return ResponseEntity.ok(new ApiResponse(true, "address added Successfully", a));
    	}catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ApiResponse(false, "Error in creation", null));
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity getAddresses(@PathVariable Long userId) {
    	try {
			List<AddressDto>ca=addressService.getAddresses(userId);
			return ResponseEntity.ok(new ApiResponse(true, "Addresses", ca));
		}catch(Exception e) {
    		return ResponseEntity.status(500).body(new ApiResponse(false, "Error while getting all addresses", null));
    	}
    }

    @PutMapping("/update/{userId}/{addressId}")
    public ResponseEntity updateAddress(@PathVariable Long userId, @PathVariable Long addressId,
                                                 @RequestBody AddressDto a) {
    	try {
    		if(a.getFullName() == null || a.getFullName().isEmpty())
        		return ResponseEntity.badRequest().body("Name is Required");
        	if(a.getPhoneNumber() == null || a.getPhoneNumber().isEmpty())
        		return ResponseEntity.badRequest().body("Phone Number is Required");
        	if(a.getCity() == null || a.getCity().isEmpty())
        		return ResponseEntity.badRequest().body("City is Required");
        	if(a.getCountry() == null || a.getCountry().isEmpty())
        		return ResponseEntity.badRequest().body("Country is Required");
        	if(a.getState() == null || a.getState().isEmpty())
        		return ResponseEntity.badRequest().body("State is Required");
        	if(a.getAddressLine() == null || a.getAddressLine().isEmpty())
        		return ResponseEntity.badRequest().body("Address is Required");
        	if(a.getPostalCode() == null || a.getPostalCode().isEmpty())
        		return ResponseEntity.badRequest().body("Postal Code is Required");
        	a=addressService.updateAddress(userId, addressId,a);
        	return ResponseEntity.ok(new ApiResponse(true, "address added Successfully", a));
    	}catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ApiResponse(false, "Error in creation", null));
        }
    }

    @DeleteMapping("/delete/{userId}/{addressId}")
    public ResponseEntity<?> deleteAddress(@PathVariable Long userId, @PathVariable Long addressId) {
        boolean deleted = addressService.deleteAddress(userId, addressId);
        return deleted ? ResponseEntity.ok("Address deleted successfully") : ResponseEntity.badRequest().build();
    }
    @PutMapping("/set-default/{userId}/{addressId}")
    public ResponseEntity setDefault(@PathVariable Long userId, @PathVariable Long addressId) {
    	try {
			AddressDto ca=addressService.setDefaultAddress(userId, addressId);
			return ResponseEntity.ok(new ApiResponse(true, "Successful", ca));
		}catch(Exception e) {
    		return ResponseEntity.status(500).body(new ApiResponse(false, "Error", null));
    	}
    }

}
