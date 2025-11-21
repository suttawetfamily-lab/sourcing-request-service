package com.pantavanij.sourcingreq.services.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping(value = "/api/sourcingreq/v1")
    public ResponseEntity<String> greeting() {
        return ResponseEntity.ok().body("Sourcing Request Rest Api");
    }
}
