package com.hugo.metalbroker.service;

import java.util.Map;

import com.hugo.metalbroker.model.user.UserDTO;

public interface UserService {
    public Map.Entry<UserDTO, String> login(UserDTO user);

    boolean addUsersToDB(UserDTO user);
}
