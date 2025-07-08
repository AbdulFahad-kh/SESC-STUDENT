package com.acme.studentportal.service.impl;

import com.acme.studentportal.domain.model.Student;
import com.acme.studentportal.domain.model.UserAccount;
import com.acme.studentportal.domain.model.UserRole;
import com.acme.studentportal.domain.repository.StudentRepository;
import com.acme.studentportal.domain.repository.UserAccountRepository;
import com.acme.studentportal.exception.UsernameAlreadyExistsException;
import com.acme.studentportal.service.UserService;
import com.acme.studentportal.web.request.RegisterUserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserAccountRepository userAccountRepository;
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserAccount registerNewUser(RegisterUserRequest request) {
        if (userAccountRepository.existsByUsername(request.getUsername())) {
            throw new UsernameAlreadyExistsException("Username is already taken");
        }

        UserAccount user = new UserAccount();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.addRole(UserRole.ROLE_STUDENT);

        UserAccount savedUser = userAccountRepository.save(user);

        // Do NOT create student profile here. It will be created on first enrolment.

        return savedUser;
    }

    @Override
    public Optional<UserAccount> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }
        String username = authentication.getName();
        return userAccountRepository.findByUsername(username);
    }

    @Override
    public Optional<Student> getCurrentStudent() {
        return getCurrentUser()
                .flatMap(user -> studentRepository.findWithEnrollmentsByUserAccountId(user.getId()));
    }

    @Override
    public boolean isCurrentUserStudent() {
        return getCurrentStudent().isPresent();
    }
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userAccountRepository.findByUsername(username)
                .map(user -> User.builder()
                        .username(user.getUsername())
                        .password(user.getPassword())
                        .authorities(user.getRoles().stream()
                                .map(role -> role.name())
                                .toArray(String[]::new))
                        .accountExpired(false)
                        .accountLocked(false)
                        .credentialsExpired(false)
                        .disabled(!user.isActive())
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    public UserAccountRepository getUserAccountRepository() {
        return userAccountRepository;
    }
}
