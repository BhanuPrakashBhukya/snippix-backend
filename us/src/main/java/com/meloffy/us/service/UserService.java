package com.meloffy.us.service;

import com.meloffy.us.dto.UserDTO;
import com.meloffy.us.model.User;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface UserService {

    String createUser(String keyCloakUserId, String username, String fullName, String email, String phone, String password);

    User findByEmailOrPhone(String emailOrPhone);

    boolean updateLastActivity(String keycloakUserid);

    UserDTO getUserByid(Authentication authentication);

    List<UserDTO> getUsers(Authentication authentication);
}
