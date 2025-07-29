package com.inaf.authe_service.controller;

import com.inaf.authe_service.dto.LoginRequest;
import com.inaf.authe_service.dto.LoginResponse;
import com.inaf.authe_service.dto.RegisterRequest;
import com.inaf.authe_service.entity.User;
import com.inaf.authe_service.service.JwtService;
import com.inaf.authe_service.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@Tag(name = "Authentification", description = "Gestion de l'inscription, connexion, profil et déconnexion")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @Operation(summary = "Inscription d'un utilisateur",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Inscription réussie"),
                    @ApiResponse(responseCode = "400", description = "Email déjà utilisé", content = @Content)
            }
    )

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            if (userService.findByEmail(request.getEmail()) != null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Email already exists"));
            }

            User user = new User();
            user.setUsername(request.getUsername());
            user.setEmail(request.getEmail());
            user.setPassword(request.getPassword());

            User savedUser = userService.saveUser(user);

            return ResponseEntity.ok(Map.of(
                    "message", "User registered successfully",
                    "username", savedUser.getUsername(),
                    "email", savedUser.getEmail()
            ));

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Connexion d’un utilisateur",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Connexion réussie",
                            content = @Content(schema = @Schema(implementation = LoginResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Identifiants invalides", content = @Content)
            }
    )
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByEmail(request.getEmail());

        if (user == null) {
            return ResponseEntity.status(404).body(Map.of("error", "User not found after authentication"));
        }

        String jwt = jwtService.generateToken(userDetails);
        LoginResponse response = new LoginResponse(jwt, user.getUsername(), user.getEmail());

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Récupérer le profil de l'utilisateur connecté",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Profil récupéré"),
                    @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Utilisateur introuvable", content = @Content)
            }
    )
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()
                    || authentication.getPrincipal().equals("anonymousUser")) {
                return ResponseEntity.status(401).body(Map.of("error", "User not authenticated"));
            }

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String email = userDetails.getUsername();

            User user = userService.findByEmail(email);

            if (user == null) {
                return ResponseEntity.status(404).body(Map.of("error", "User not found"));
            }

            Map<String, Object> profile = new HashMap<>();
            profile.put("id", user.getId());
            profile.put("username", user.getUsername());
            profile.put("email", user.getEmail());

            return ResponseEntity.ok(profile);

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Error retrieving profile"));
        }
    }

    @Operation(summary = "Déconnexion de l'utilisateur",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Déconnexion réussie")
            }
    )
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(Map.of(
                "message", "Logged out successfully",
                "note", "Please remove the JWT token from client storage"
        ));
    }
}
