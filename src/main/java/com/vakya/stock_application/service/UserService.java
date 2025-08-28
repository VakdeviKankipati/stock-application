package com.vakya.stock_application.service;

import com.vakya.stock_application.model.User;
import com.vakya.stock_application.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Mono<Object> registerUser(User user) {
        return userRepository.findByEmail(user.getEmail())
                .flatMap(existingUser -> Mono.error(new RuntimeException("Email already registered")))
                .switchIfEmpty(
                        Mono.defer(() -> {
                            user.setPassword(passwordEncoder.encode(user.getPassword()));
                            return userRepository.save(user)
                                    .cast(User.class);
                        })
                );
    }



    public Mono<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
