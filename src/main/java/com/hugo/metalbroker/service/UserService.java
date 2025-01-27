package com.hugo.metalbroker.service;

import java.util.Map;

import com.hugo.metalbroker.model.user.UserDTO;
import jakarta.servlet.http.HttpServletResponse;

public interface UserService {
    public Map.Entry<UserDTO, String> login(UserDTO user, HttpServletResponse response);

    boolean addUsersToDB(UserDTO user);
}
