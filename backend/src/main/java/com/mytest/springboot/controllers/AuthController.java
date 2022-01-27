package com.mytest.springboot.controllers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import com.mytest.springboot.models.EnumRole;
import com.mytest.springboot.responses.JwtResponse;
import com.mytest.springboot.responses.MessageResponse;
import com.mytest.springboot.repository.RoleRepository;
import com.mytest.springboot.repository.UserRepository;
import com.mytest.springboot.requests.SignupRequest;
import com.mytest.springboot.services.UserDetailsImpl;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mytest.springboot.models.Role;
import com.mytest.springboot.models.User;
import com.mytest.springboot.requests.LoginRequest;
import com.mytest.springboot.config.JWTUtils;

@Tag(name = "Authentication", description = "AuthenticationRestController API v1")
@CrossOrigin(origins = "http://localhost:8081", maxAge = 3600)
@RestController
@RequestMapping("/api/v1")
public class AuthController {
  @Autowired
  AuthenticationManager authenticationManager;

  @Autowired
  UserRepository userRepository;

  @Autowired
  RoleRepository roleRepository;

  @Autowired
  PasswordEncoder encoder;

  @Autowired
  JWTUtils jwtUtils;

  @PostMapping("/auth/login")
  @ApiOperation(value = "authenticateUser", notes = "User login", tags = "Authentication")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Success"),
          @ApiResponse(responseCode = "401", description = "Authentication Data is missing or invalid"),
          @ApiResponse(responseCode = "403", description = "Forbidden operation"),
          @ApiResponse(responseCode = "404", description = "Controller not found")}
  )
  public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

    SecurityContextHolder.getContext().setAuthentication(authentication);
    String jwt = jwtUtils.generateJwtToken(authentication);
    
    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
    List<String> roles = userDetails.getAuthorities().stream()
        .map(item -> item.getAuthority())
        .collect(Collectors.toList());

    return ResponseEntity.ok(new JwtResponse(jwt,
                         userDetails.getId(), 
                         userDetails.getUsername(), 
                         roles));
  }

  @PostMapping("/auth/signup")
  @ApiOperation(value = "registerUser", notes = "Register new user", tags = "Authentication")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Success"),
          @ApiResponse(responseCode = "401", description = "Authentication Data is missing or invalid"),
          @ApiResponse(responseCode = "403", description = "Forbidden operation"),
          @ApiResponse(responseCode = "404", description = "Controller not found")}
  )
  public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
    if (userRepository.existsByUsername(signUpRequest.getUsername())) {
      return ResponseEntity
          .badRequest()
          .body(new MessageResponse("Error: Username is already taken!"));
    }

    // Create new user's account
    User user = new User(signUpRequest.getUsername(), 
               encoder.encode(signUpRequest.getPassword()));

    Set<String> strRoles = signUpRequest.getRole();
    Set<Role> roles = new HashSet<>();

    if (strRoles == null) {
      Role userRole = roleRepository.findByName(EnumRole.ROLE_USER)
          .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
      roles.add(userRole);
    } else {
      strRoles.forEach(role -> {
        switch (role) {
        case "admin":
          Role adminRole = roleRepository.findByName(EnumRole.ROLE_ADMIN)
              .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
          roles.add(adminRole);

          break;
          case "edit":
            Role editRole = roleRepository.findByName(EnumRole.ROLE_ALLOW_EDIT)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(editRole);

            break;
        default:
          Role userRole = roleRepository.findByName(EnumRole.ROLE_USER)
              .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
          roles.add(userRole);
        }
      });
    }

    user.setRoles(roles);
    userRepository.save(user);

    return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
  }
}
