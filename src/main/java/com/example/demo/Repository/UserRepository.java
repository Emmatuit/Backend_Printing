package com.example.demo.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.model.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

	Optional<UserEntity> findByEmail(String email);

	Optional<UserEntity> findByResetPasswordToken(String token);

	Optional<UserEntity> findByUsername(String username);

	@Query("SELECT u FROM UserEntity u WHERE u.isEmailVerified = false AND u.createdAt < :cutoff")
	List<UserEntity> findUnverifiedUsersBefore(@Param("cutoff") LocalDateTime cutoff);

	List<UserEntity> findBySuspendedUntilBefore(LocalDateTime time);

}
