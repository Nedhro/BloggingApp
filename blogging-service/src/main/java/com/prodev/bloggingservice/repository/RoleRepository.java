package com.prodev.bloggingservice.repository;

import com.prodev.bloggingservice.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByRoleName(String username);

    @SuppressWarnings("unchecked")
    Role save(Role role);

    int countByRoleName(String adminRoleName);
}
