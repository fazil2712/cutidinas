package com.ppip.cutidinas.repository;

import com.ppip.cutidinas.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByBadgeidOrName(String badgeid, String name);
}
