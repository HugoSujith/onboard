package com.hugo.metalbroker.service.implementation;

import java.util.logging.Logger;

import com.hugo.metalbroker.model.user.UserDTO;
import com.hugo.metalbroker.repository.UserRepo;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepo userRepo;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public UserDTO login(UserDTO user) {
        boolean ifUserPresent = userRepo.findIfUserPresent(user);
        if (!ifUserPresent) {
            return UserDTO.getDefaultInstance();
        }
        UserDTO userDB = userRepo.getUserByUsername(user.getUsername());
        Logger.getLogger(this.getClass().getName()).info("Password matchcing: " + String.valueOf(passwordEncoder.matches(user.getPassword(), userDB.getPassword())));
        if (passwordEncoder.matches(user.getPassword(), userDB.getPassword())) {
            return user;
        } else {
            return null;
        }
    }

    public boolean addUsersToDB(UserDTO user) {
        UserDTO userEncrypted = UserDTO.newBuilder()
                .setUsername(user.getUsername())
                .setPassword(passwordEncoder.encode(user.getPassword()))
                .setFirstname(user.getFirstname())
                .setLastname(user.getLastname())
                .setBalance(user.getBalance())
                .build();
        return userRepo.addUsersToDB(userEncrypted);
    }
}
