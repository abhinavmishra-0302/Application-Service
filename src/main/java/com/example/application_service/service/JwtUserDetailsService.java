package com.example.application_service.service;

import com.example.application_service.dto.UserDetailsResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.User;

import java.util.ArrayList;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    private final RestTemplate restTemplate;
    private final String AUTH_SERVICE_URL = "http://localhost:8080/api/v1/auth/get-user";

    public JwtUserDetailsService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Call auth-service to get the user by username
        UserDetailsResponse userDetailsResponse = restTemplate.getForObject(
                AUTH_SERVICE_URL + "?username=" + username,
                UserDetailsResponse.class
        );

        if (userDetailsResponse == null || userDetailsResponse.getUsername() == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        // Return Spring Security's User object with username, password, and authorities
        return new User(userDetailsResponse.getUsername(), userDetailsResponse.getPassword(), new ArrayList<>());
    }
}

