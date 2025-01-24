package com.hugo.metalbroker.service.implementation;

import com.hugo.metalbroker.model.user.UserDTO;
import com.hugo.metalbroker.repository.UserRepo;
import com.hugo.metalbroker.security.UserDetailsImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class BrokerUserDetailsServiceImpl implements UserDetailsService {
    private final UserRepo userRepo;

    public BrokerUserDetailsServiceImpl(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDTO user = userRepo.getUserByUsername(username);
        return new UserDetailsImpl(user);
    }
}
