package com.hugo.metalbroker.service.implementation;

import com.hugo.metalbroker.model.user.UserDTO;
import com.hugo.metalbroker.repository.UserRepo;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements com.hugo.metalbroker.service.UserService {
    private final UserRepo userRepo;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

    public UserServiceImpl(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDTO login(UserDTO user) {
        boolean ifUserPresent = userRepo.findIfUserPresent(user);
        if (!ifUserPresent) {
            return UserDTO.getDefaultInstance();
        }
        UserDTO userDB = userRepo.getUserByUsername(user.getUsername());
        if (passwordEncoder.matches(user.getPassword(), userDB.getPassword())) {
            return user;
        } else {
            return null;
        }
    }

    @Override
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
