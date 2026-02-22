package com.meloffy.us.service.impl;

import com.meloffy.us.dao.UserDAO;
import com.meloffy.us.dto.UserDTO;
import com.meloffy.us.model.User;
import com.meloffy.us.service.UserService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger log =
            LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserDAO userDAO;

    @Override
    @Transactional
    public String createUser(String keycloakUserId,
                             String username,
                             String fullName,
                             String email,
                             String phone,
                             String password) {

        log.info("[SERVICE] createUser started");
        log.info("[SERVICE] keycloakUserId={}", keycloakUserId);

        User user = new User();
        user.setKeycloakUserId(keycloakUserId);
        user.setFullName(fullName);
        user.setUsername(username);
        user.setEmail(email);
        user.setPhone(phone);

        log.info("[SERVICE] Saving user entity: {}", user);

        log.info("ID before save = {}", user.getId());
        User usr = userDAO.save(user);
        log.info("ID after flush = {}", user.getId());

        log.info("[SERVICE] userDAO.save() executed");

        return user.getKeycloakUserId();
    }

    @Override
    public User findByEmailOrPhone(String emailOrPhone) {
        log.info("[SERVICE] findByEmailOrPhone={}", emailOrPhone);
        return userDAO.findByEmailOrPhone(emailOrPhone);
    }

    @Override
    public boolean updateLastActivity(String keycloakUserid) {
        return userDAO.updateLastActivity(keycloakUserid);
    }

    @Override
    public UserDTO getUserByid(Authentication authentication) {
        String userId = authentication.getName();
        User usr = userDAO.getUserById(userId, getAuthenticatedUser(authentication));
        UserDTO userDTO = mapToDTO(usr);
        return userDTO;
    }

    @Override
    public List<UserDTO> getUsers(Authentication authentication) {

        String principalId = authentication.getName();

        List<User> users = userDAO.getUsers(authentication);

        List<UserDTO> usersDTO = users.stream()
                .map(this::mapToDTO)
                .toList();

        return usersDTO;
    }

    private User getAuthenticatedUser(Authentication authentication) {
        User usr = new User();
        Jwt jwt =  (Jwt) authentication.getPrincipal();

        usr.setKeycloakUserId(jwt.getSubject());

        return usr;
    }

    private UserDTO mapToDTO(User usr) {
        UserDTO userDTO = new UserDTO();

        userDTO.setUsername(usr.getUsername());
        userDTO.setFullName(usr.getFullName());
        userDTO.setEmail(usr.getEmail());
        userDTO.setPhone(usr.getPhone());
        userDTO.setKeycloakUserId(usr.getKeycloakUserId());
        userDTO.setId(usr.getId());
        userDTO.setLastActiveAt(usr.getLastActiveAt());
        userDTO.setStatus(usr.getStatus());

        return userDTO;
    }
}