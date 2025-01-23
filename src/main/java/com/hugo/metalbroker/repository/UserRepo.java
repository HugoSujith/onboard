package com.hugo.metalbroker.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.hugo.metalbroker.model.user.UserDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepo {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final Logger LOGGER = Logger.getLogger(getClass().getName());

    public UserRepo(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    public boolean findIfUserPresent(UserDTO user) {
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

    public UserDTO getUserByUsername(String username) {
        String query = "SELECT * FROM USER WHERE USERNAME = :username";
        Map<String, Object> params = new HashMap<>();
        params.put("username", username);

        try {
            List<UserDTO> users = namedParameterJdbcTemplate.query(query, params, (rs, rowNum) -> UserDTO.newBuilder()
                    .setUsername(rs.getString("username"))
                    .setPassword(rs.getString("password"))
                    .setFirstname(rs.getString("firstname"))
                    .setLastname(rs.getString("lastname"))
                    .setBalance(rs.getInt("balance"))
                    .build());

            if (!users.isEmpty()) {
                return users.getFirst();
            } else {
                LOGGER.info("No user found probably!");
            }
        } catch (Exception e) {
            LOGGER.info(e.getMessage());
            return null;
        }
        return null;
    }

    public boolean addUsersToDB(UserDTO user) {
        if (!findIfUserPresent(user)) {
            String query = "INSERT INTO USER (username, password, firstname, lastname, balance) VALUES (:username, :password, :first_name, :last_name, :balance)";
            Map<String, Object> params = new HashMap<>();
            params.put("username", user.getUsername());
            params.put("password", user.getPassword());
            params.put("first_name", user.getFirstname());
            params.put("last_name", user.getLastname());
            params.put("balance", user.getBalance());

            System.out.println("Map contents: " + params);

            try {
                int rowsAffected = namedParameterJdbcTemplate.update(query, params);
                return rowsAffected > 0;
            } catch (Exception e) {
                LOGGER.info(e.getMessage());
                throw new RuntimeException("Error while adding user", e);
            }
        }
        return false;
    }

    public ResponseEntity<String> authenticateUser(UserDTO user) {
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
            LOGGER.info(e.getMessage());
            throw new RuntimeException("Error while authenticating user", e);
        }
    }
}
