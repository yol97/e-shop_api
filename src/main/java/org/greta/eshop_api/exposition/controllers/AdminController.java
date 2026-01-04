package org.greta.eshop_api.exposition.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @GetMapping("/products")
    public ResponseEntity<String> adminProducts() {
        return ResponseEntity.ok("OK");
    }
}

