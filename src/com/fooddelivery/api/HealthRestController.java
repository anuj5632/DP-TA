package com.fooddelivery.api;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
public class HealthRestController {

    @GetMapping("/api/health")
    public String health() {
        System.out.println("========== API HEALTH (GET /api/health) ==========");
        return "OK";
    }
}
