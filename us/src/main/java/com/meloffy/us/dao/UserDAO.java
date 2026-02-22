package com.meloffy.us.dao;

import com.meloffy.us.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface UserDAO {

    User findByEmailOrPhone(String emailOrPhone);

    User save(User user);

    boolean updateLastActivity(String keycloakUserid);

    User getUserById(String id, User user);

    List<User> getUsers(Authentication authentication);
}
