package com.hugo.metalbroker.service;

import com.hugo.metalbroker.model.user.UserDTO;

public interface UserService {
    UserDTO login(UserDTO user);

    boolean addUsersToDB(UserDTO user);
}
