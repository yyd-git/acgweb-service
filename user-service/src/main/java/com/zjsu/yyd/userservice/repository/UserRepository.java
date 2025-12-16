package com.zjsu.yyd.userservice.repository;

import com.zjsu.yyd.userservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUserNameAndIsDeletedFalse(String userName);
}
