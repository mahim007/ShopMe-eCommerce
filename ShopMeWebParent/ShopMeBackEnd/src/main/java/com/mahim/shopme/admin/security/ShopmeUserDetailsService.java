package com.mahim.shopme.admin.security;

import com.mahim.shopme.admin.user.UserRepository;
import com.mahim.shopme.common.entity.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ShopmeUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public ShopmeUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.getUserByEmail(email);
        if (user != null) {
            return new ShopmeUserDetails(user);
        }

        throw new UsernameNotFoundException("Could not find user with email: " + email);
    }
}
