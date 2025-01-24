package com.hugo.metalbroker.service.implementation;

import java.util.AbstractMap;
import java.util.Map;

import com.hugo.metalbroker.exceptions.UserNotFoundException;
import com.hugo.metalbroker.model.user.UserDTO;
import com.hugo.metalbroker.repository.UserRepo;
import com.hugo.metalbroker.utils.JWTUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements com.hugo.metalbroker.service.UserService {
    private final UserRepo userRepo;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final JWTUtils jwtUtils;

    public UserServiceImpl(UserRepo userRepo, BCryptPasswordEncoder passwordEncoder, AuthenticationManager authManager, JWTUtils jwtUtils) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.authManager = authManager;
        this.jwtUtils = jwtUtils;
    }

    @Override
    public Map.Entry<UserDTO, String> login(UserDTO user) {
        Authentication auth = authManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
        if (auth.isAuthenticated()) {
            String userJWTToken = jwtUtils.generateToken(user.getUsername());
            return new AbstractMap.SimpleEntry<>(user, userJWTToken);
        }
        throw new UserNotFoundException(user.getUsername());
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
