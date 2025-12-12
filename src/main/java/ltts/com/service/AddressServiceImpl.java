package ltts.com.service;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ltts.com.dto.AddressDto;
import ltts.com.dto.CategoryDto;
import ltts.com.model.Address;
import ltts.com.model.Users;
import ltts.com.repository.AddressRepository;
import ltts.com.repository.UserRepo;
import ltts.com.service.AddressService;

@Service
@Transactional
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressRepository addressRepo;

    @Autowired
    private UserRepo userRepo;
    @Autowired
	private ModelMapper modelMapper;

    @Override
    public AddressDto addAddress(Long userId, AddressDto address) {
        Users user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Address a=modelMapper.map(address,Address.class);
        
        a.setUser(user);

        // If first address, mark as default
        if (addressRepo.findByUserId(userId).isEmpty()) {
            a.setIsDefault(true);
        }
        a=addressRepo.save(a);
        address=modelMapper.map(a,AddressDto.class);
        return address;
    }

    @Override
    public List<AddressDto> getAddresses(Long userId) {
        List<Address>ad= addressRepo.findByUserId(userId);
        return ad.stream().map(
				add -> modelMapper.map(add, AddressDto.class)).collect(Collectors.toList());
    }

    @Override
    public AddressDto updateAddress(Long userId, Long addressId, AddressDto updated) {
        Address address = addressRepo.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        if (!address.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized to update this address");
        }

        address.setFullName(updated.getFullName());
        address.setPhoneNumber(updated.getPhoneNumber());
        address.setAddressLine(updated.getAddressLine());
        address.setCity(updated.getCity());
        address.setState(updated.getState());
        address.setCountry(updated.getCountry());
        address.setPostalCode(updated.getPostalCode());
        address.setIsDefault(updated.getIsDefault());

        address=addressRepo.save(address);
        updated=modelMapper.map(address,AddressDto.class);
        return updated;
    }

    @Override
    public boolean deleteAddress(Long userId, Long addressId) {
        Address address = addressRepo.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));
        if (!address.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized to delete this address");
        }
        addressRepo.delete(address);
        return true;
    }
    @Transactional
    public AddressDto setDefaultAddress(Long userId, Long addressId) {
        List<Address> addresses = addressRepo.findByUserId(userId);
        for (Address addr : addresses) {
            addr.setIsDefault(addr.getId().equals(addressId));
        }
        addressRepo.saveAll(addresses);
        Address a= addressRepo.findById(addressId).get();
        return modelMapper.map(a,AddressDto.class);
    }
    
     
}
