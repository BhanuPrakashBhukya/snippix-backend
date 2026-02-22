package com.meloffy.us.dao.impl;

import com.meloffy.us.dao.UserDAO;
import com.meloffy.us.model.User;
import com.meloffy.us.rowmapper.UserRowMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Component
public class UserDAOImpl implements UserDAO {

    private static final Logger log =
            LoggerFactory.getLogger(UserDAOImpl.class);

    @Autowired
    JdbcTemplate jdbcTemplate;

    private static String INSERT_INTO_USER = "INSERT INTO t_users (f_keycloak_user_id, f_username, f_full_name, f_email, f_phone, f_status, " +
            "f_created_at, f_updated_at) VALUES (?, ?, ?, ?, ?, ?, NOW(), NOW())";

    private static String UPDATE_LAST_ACTIVITY_BY_KEYCLOAK_USER_ID = "update t_users set f_last_active_at = CURRENT_TIMESTAMP where f_keycloak_user_id = ?";

    private static String SELECT_USER_BY_ID = "SELECT * FROM t_users where f_keycloak_user_id = ?";

    private static String SELECT_USERS = "SELECT * FROM t_users WHERE f_status = 'ACTIVE'";
    
    @Override
    public User findByEmailOrPhone(String emailOrPhone) {
        System.out.println("UserDAOImpl findByEmailOrPhone");
        return null;
    }

    @Override
    public User save(User user) {
        Long userId = 0L;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int rows = jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    INSERT_INTO_USER, Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, user.getKeycloakUserId());
            ps.setString(2, user.getUsername());
            ps.setString(3, user.getFullName());
            ps.setString(4, user.getEmail());
            ps.setString(5, user.getPhone());
            ps.setString(6, "ACTIVE");
            return ps;
        }, keyHolder);

        if (rows != 1) {
            throw new IllegalStateException("Failed to insert user, rows affected: " + rows);
        }

        userId = keyHolder.getKey().longValue();
        user.setId(userId);
        log.info("[JDBC] User inserted successfully with id={}", userId);

        return user;
    }

    @Override
    public boolean updateLastActivity(String keycloakUserId) {
        int rows = jdbcTemplate.update(UPDATE_LAST_ACTIVITY_BY_KEYCLOAK_USER_ID, keycloakUserId);
        if (rows == 0) {
            log.warn("No user found for keycloakUserId={}", keycloakUserId);
        }
        return rows > 0;
    }

    @Override
    public User getUserById(String id, User user) {
        Object[] args = new Object[]{id};
        User usr = new User();
        try {
            usr = jdbcTemplate.queryForObject(SELECT_USER_BY_ID, new UserRowMapper(), args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return usr;
    }

    @Override
    public List<User> getUsers(Authentication authentication) {
        Object[] args = new Object[]{  };
        List<User> user = new ArrayList<>();
        try {
            user = jdbcTemplate.query(SELECT_USERS, new UserRowMapper(), args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return user;
    }
}
