package com.hugo.onboard.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.hugo.onboard.model.user.UserOuterClass.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepo {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final Logger log = Logger.getLogger(getClass().getName());

    public UserRepo(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    public boolean findIfUserPresent(User user) {
        String query = "SELECT COUNT(*) FROM USER WHERE USERNAME = :username";
        Map<String, Object> params = new HashMap<>();
        params.put("username", user.getUsername());

        try {
            Integer count = namedParameterJdbcTemplate.queryForObject(query, params, Integer.class);
            return count != null && count > 0;
        } catch (Exception e) {
            throw new RuntimeException("Error while checking user presence", e);
        }
    }

    public User getUserByUsername(User user) {
        if (findIfUserPresent(user)) {
            String query = "SELECT * FROM USER WHERE USERNAME = :username";
            Map<String, Object> params = new HashMap<>();
            params.put("username", user.getUsername());

            try {
                List<User> users = namedParameterJdbcTemplate.query(query, params, new RowMapper<User>() {
                    @Override
                    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return User.newBuilder()
                                .setUsername(rs.getString("username"))
                                .setPassword(rs.getString("password"))
                                .setFirstName(rs.getString("firstname"))
                                .setLastName(rs.getString("lastname"))
                                .setBalance(rs.getInt("balance"))
                                .build();

                    }
                });

                if (!users.isEmpty()) {
                    return users.getFirst();
                }
            } catch (Exception e) {
                log.info(e.getMessage());
                return null;
            }
        }
        return null;
    }


    public boolean addUsersToDB(User user) {
        if (!findIfUserPresent(user)) {
            String query = "INSERT INTO USER (username, password, firstname, lastname, balance) VALUES (:username, :password, :first_name, :last_name, :balance)";
            Map<String, Object> params = new HashMap<>();
            params.put("username", user.getUsername());
            params.put("password", user.getPassword());
            params.put("first_name", user.getFirstName());
            params.put("last_name", user.getLastName());
            params.put("balance", user.getBalance());

            System.out.println("Map contents: " + params);

            try {
                int rowsAffected = namedParameterJdbcTemplate.update(query, params);
                return rowsAffected > 0;
            } catch (Exception e) {
                log.info(e.getMessage());
                throw new RuntimeException("Error while adding user", e);
            }
        }
        return false;
    }

    public ResponseEntity<String> authenticateUser(User user) {
        String query = "SELECT COUNT(*) FROM USER WHERE USERNAME = :username AND PASSWORD = :password";
        Map<String, Object> params = new HashMap<>();
        params.put("username", user.getUsername());
        params.put("password", user.getPassword());

        try {
            Integer count = namedParameterJdbcTemplate.queryForObject(query, params, Integer.class);
            if (count != null && count > 0) {
                return new ResponseEntity<>("Authentication successful", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Invalid credentials", HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            log.info(e.getMessage());
            throw new RuntimeException("Error while authenticating user", e);
        }
    }
}