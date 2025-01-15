package com.hugo.onboard.service;

import com.hugo.onboard.model.user.User;
import com.hugo.onboard.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void addUserToDatabase(User user) {
        userRepository.save(user);
    }
}
