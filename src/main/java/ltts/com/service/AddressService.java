package ltts.com.service;

import java.util.List;

import ltts.com.dto.AddressDto;
import ltts.com.model.Address;

public interface AddressService {
    AddressDto addAddress(Long userId, AddressDto address);
    List<AddressDto> getAddresses(Long userId);
    AddressDto updateAddress(Long userId, Long addressId, AddressDto updated);
    boolean deleteAddress(Long userId, Long addressId);
    public AddressDto setDefaultAddress(Long userId, Long addressId);
}
