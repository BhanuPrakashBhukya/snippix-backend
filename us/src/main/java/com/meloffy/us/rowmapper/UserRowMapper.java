package com.meloffy.us.rowmapper;

import com.meloffy.us.model.User;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class UserRowMapper implements RowMapper<User> {

    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {

        User user = new User();

        // Primary key
        user.setId(rs.getLong("f_id"));

        // Identity linkage
        user.setKeycloakUserId(rs.getString("f_keycloak_user_id"));
        user.setUsername(rs.getString("f_username"));
        user.setFullName(rs.getString("f_full_name"));
        user.setEmail(rs.getString("f_email"));
        user.setPhone(rs.getString("f_phone"));

        // User state
        user.setStatus(rs.getString("f_status"));

        // Timestamps (handle NULL safely)
        Timestamp lastActiveTs = rs.getTimestamp("f_last_active_at");
        if (lastActiveTs != null) {
            user.setLastActiveAt(lastActiveTs.toLocalDateTime());
        }

        user.setCreatedAt(rs.getTimestamp("f_created_at").toLocalDateTime());
        user.setUpdatedAt(rs.getTimestamp("f_updated_at").toLocalDateTime());

        return user;
    }
}
