package ltts.com.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import ltts.com.model.Address;

public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByUserId(Long userId);
}
