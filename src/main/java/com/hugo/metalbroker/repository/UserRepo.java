package com.hugo.metalbroker.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hugo.metalbroker.exceptions.RegistrationFailureException;
import com.hugo.metalbroker.exceptions.UserNotFoundException;
import com.hugo.metalbroker.model.user.UserDTO;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepo {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public UserRepo(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    public boolean findIfUserPresent(UserDTO user) {
        String query = SQLQueryConstants.FIND_COUNT_OF_USERS_BY_USERNAME;
        Map<String, Object> params = new HashMap<>();
        params.put("username", user.getUsername());
        try {
            Integer count = namedParameterJdbcTemplate.queryForObject(query, params, Integer.class);
            return count != null && count > 0;
        } catch (Exception e) {
            throw new UserNotFoundException(user.getUsername());
        }
    }

    public UserDTO getUserByUsername(String username) {
        String query = SQLQueryConstants.GET_ALL_USERS_BY_USERNAME;
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
                throw new UserNotFoundException(username);
            }
        } catch (Exception e) {
            throw new UserNotFoundException(username);
        }
    }

    public boolean addUsersToDB(UserDTO user) {
        if (!findIfUserPresent(user)) {
            String query = SQLQueryConstants.INSERT_INTO_USER;
            Map<String, Object> params = new HashMap<>();
            params.put("username", user.getUsername());
            params.put("password", user.getPassword());
            params.put("first_name", user.getFirstname());
            params.put("last_name", user.getLastname());
            params.put("balance", user.getBalance());

            try {
                int rowsAffected = namedParameterJdbcTemplate.update(query, params);
                return rowsAffected > 0;
            } catch (Exception e) {
                throw new RegistrationFailureException(user.getUsername());
            }
        }
        return false;
    }
}
