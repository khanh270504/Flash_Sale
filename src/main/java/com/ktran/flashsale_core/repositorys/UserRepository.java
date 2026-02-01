package com.ktran.flashsale_core.repositorys;

import com.ktran.flashsale_core.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
Optional<User> findByUserName(String userName);
}
