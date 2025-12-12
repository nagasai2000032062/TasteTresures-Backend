package ltts.com.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ltts.com.model.Users;

@Repository
public interface UserRepo extends JpaRepository<Users, Long>
{

	Users findByEmail(String email);
	Users findByEmailAndAnswer(String email,String answer);
}
