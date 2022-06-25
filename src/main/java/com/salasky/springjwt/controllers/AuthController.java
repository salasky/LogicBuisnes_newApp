package com.salasky.springjwt.controllers;

import com.salasky.springjwt.models.*;
import com.salasky.springjwt.models.payload.request.LoginRequest;
import com.salasky.springjwt.models.payload.request.SignupRequest;
import com.salasky.springjwt.models.payload.response.JwtResponse;
import com.salasky.springjwt.models.payload.response.MessageResponse;
import com.salasky.springjwt.repository.EmployeeRepositories;
import com.salasky.springjwt.repository.RoleRepository;
import com.salasky.springjwt.repository.SubdivisionRepositories;
import com.salasky.springjwt.repository.UserRepository;
import com.salasky.springjwt.security.jwt.JwtUtils;
import com.salasky.springjwt.security.services.UserDetailsImpl;
import com.salasky.springjwt.validator.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    Logger logger = LoggerFactory.getLogger(AuthController.class);

    private AuthenticationManager authenticationManager;
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder encoder;
    private JwtUtils jwtUtils;
    private EmployeeRepositories employeeRepositories;
    private SubdivisionRepositories subdivisionRepositories;
    private Validator validator;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder encoder, JwtUtils jwtUtils, EmployeeRepositories employeeRepositories, SubdivisionRepositories subdivisionRepositories, Validator validator) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
        this.jwtUtils = jwtUtils;
        this.employeeRepositories = employeeRepositories;
        this.subdivisionRepositories = subdivisionRepositories;
        this.validator = validator;
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());
        logger.info("Аутентификация пользователя");
        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        System.out.println(signUpRequest.getUsername());
        System.out.println(signUpRequest.getFirstName());
        System.out.println(signUpRequest.getJobTitle());

        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            logger.error("Username уже используется");
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Username уже используется!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            logger.error("Email уже используется");
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Email уже используется"));
        }

        Optional<Subdivision> subdivision=subdivisionRepositories.findByName(signUpRequest.getSubdivisionName());
        if(subdivision.isPresent()){
        // Create new user's account
        User user = new User(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);

                        break;
                    case "mod":
                        Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(modRole);

                        break;
                    default:
                        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);

        // Create new employee

            Employee employee=new Employee(signUpRequest.getUsername(),signUpRequest.getFirstName()
                    ,signUpRequest.getSecondName(),signUpRequest.getLastName()
                    ,signUpRequest.getJobTitle(),subdivision.get());
            employeeRepositories.save(employee);
            logger.info("Добавлен новый работник");
            return  ResponseEntity.status(HttpStatus.OK).body(employee);
        }

        logger.error("Добавление пользователя.Subdivision c названием "+signUpRequest.getSubdivisionName()+ " не существует");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Subdivision c названием "+signUpRequest.getSubdivisionName()+ " не существует\n" +
                "Пожалуйста обратитесь к модератору для добавления подразделения в базу");
    }
}
