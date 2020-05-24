package com.piedel.piotr.configuration.service.services;

import com.piedel.piotr.configuration.service.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService {


    private final UserRepository userRepository;

    public AuthorizationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
