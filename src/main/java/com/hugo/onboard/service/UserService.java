package com.hugo.onboard.service;

import java.util.Optional;

import com.hugo.onboard.model.user.User;
import com.hugo.onboard.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Boolean findUserIfPresent(User user) {
        Optional<User> searchDBForUser = userRepository.findById(user.getUsername());
        return searchDBForUser.isPresent();
    }

    public ResponseEntity<String> addUserToDatabase(User user) {
        try {
            if (Boolean.FALSE.equals(findUserIfPresent(user))) {
                userRepository.save(user);
                return new ResponseEntity<>("User registered", HttpStatus.OK);
            }
            return new ResponseEntity<>("User already registered", HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity<>("User registration failed", HttpStatus.EXPECTATION_FAILED);
        }
    }

    public ResponseEntity<String> authenticateUser(User user) {
        Optional<User> foundUser = userRepository.findById(user.getUsername());
        if (foundUser.isEmpty()) {
            return new ResponseEntity<>("No such user found", HttpStatus.NOT_FOUND);
        }
        User existingUser = foundUser.get();
        if (user.getPassword().equals(existingUser.getPassword())) {
            return new ResponseEntity<>("User is authorized to use the platform", HttpStatus.OK);
        }
        return new ResponseEntity<>("Invalid password", HttpStatus.UNAUTHORIZED);
    }

    public ResponseEntity<String> updateUserInfo(User user) {
        Optional<User> foundUser = userRepository.findById(user.getUsername());
        if (foundUser.isPresent()) {
            if (Boolean.FALSE.equals(foundUser.get().getBalance().equals(user.getBalance()))) {
                return new ResponseEntity<>("You are trying to manipulate your balance", HttpStatus.BAD_REQUEST);
            }
            userRepository.save(user);
            return new ResponseEntity<>("Your details updated", HttpStatus.OK);
        }
        return new ResponseEntity<>("You are not registered. Please register yourself first!", HttpStatus.NOT_FOUND);
    }
}
