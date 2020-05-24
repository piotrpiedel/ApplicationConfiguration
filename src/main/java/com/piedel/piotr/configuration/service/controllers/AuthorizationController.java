package com.piedel.piotr.configuration.service.controllers;

import com.piedel.piotr.configuration.service.services.AuthorizationService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthorizationController {

    private final AuthorizationService authorizationService;

    public AuthorizationController(AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }
}
