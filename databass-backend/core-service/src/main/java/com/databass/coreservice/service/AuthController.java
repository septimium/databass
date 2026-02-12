package com.databass.coreservice.service;

import com.databass.coreservice.dto.AuthRequest;
import com.databass.coreservice.model.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public String addNewUser(@RequestBody AuthRequest authRequest) {
        User user = new User();
        user.setUsername(authRequest.getUsername());
        user.setPassword(authRequest.getPassword());
        user.setEmail(authRequest.getEmail());

        return service.saveUser(user);
    }

    @PostMapping("/login")
    public String getToken(@RequestBody AuthRequest authRequest) {
        return service.generateToken(authRequest.getUsername(), authRequest.getPassword());
    }
}