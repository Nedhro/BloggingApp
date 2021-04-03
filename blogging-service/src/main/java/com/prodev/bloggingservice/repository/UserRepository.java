package com.prodev.bloggingservice.repository;

import com.prodev.bloggingservice.model.User;
import com.prodev.bloggingservice.model.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);

    List<User> findAllByEnabledTrue();

    List<User> findByIdAndEnabledTrue(Long id);
}
