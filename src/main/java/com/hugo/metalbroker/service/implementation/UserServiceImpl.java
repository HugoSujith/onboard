package com.hugo.metalbroker.service.implementation;

import java.util.AbstractMap;
import java.util.Map;

import com.hugo.metalbroker.exceptions.InsufficientBalance;
import com.hugo.metalbroker.exceptions.UserNotFoundException;
import com.hugo.metalbroker.model.user.BalanceDTO;
import com.hugo.metalbroker.model.user.UserDTO;
import com.hugo.metalbroker.repository.AssetRepo;
import com.hugo.metalbroker.repository.TransactionRepo;
import com.hugo.metalbroker.repository.UserRepo;
import com.hugo.metalbroker.repository.WalletRepo;
import com.hugo.metalbroker.utils.AssetUtils;
import com.hugo.metalbroker.utils.JWTUtils;
import com.hugo.metalbroker.utils.ProtoUtils;
import com.hugo.metalbroker.utils.UIDGenerator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
    private final WalletRepo walletRepo;

    public UserServiceImpl(UserRepo userRepo, BCryptPasswordEncoder passwordEncoder, AuthenticationManager authManager, JWTUtils jwtUtils, WalletRepo walletRepo) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.authManager = authManager;
        this.jwtUtils = jwtUtils;
        this.walletRepo = walletRepo;
    }

    @Override
    public Map.Entry<UserDTO, String> login(UserDTO user, HttpServletResponse response) {
        Authentication auth = authManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
        if (auth.isAuthenticated()) {
            String userJWTToken = jwtUtils.generateToken(user.getUsername(), response);
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
        boolean registerUser = userRepo.addUsersToDB(userEncrypted);
        boolean createUserWallet = walletRepo.createWallet(user.getUsername());
        return registerUser && createUserWallet;
    }

    @Override
    public BalanceDTO getBalance(HttpServletRequest request) {
        String username = jwtUtils.getUsername(request.getCookies());
        double balance = userRepo.getBalance(username);
        if (balance == -1) {
            throw new InsufficientBalance(Double.toString(balance));
        } else {
            return BalanceDTO.newBuilder()
                    .setUsername(username)
                    .setBalance(balance)
                    .build();
        }
    }


}
