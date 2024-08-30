package com.restproject.backend.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/private/user")
public class UserControllers {

    @GetMapping("/v1/name")
    public String getName() {
        return "name";
    }
}
