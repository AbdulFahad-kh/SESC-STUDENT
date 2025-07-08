package com.acme.studentportal.service;

import com.acme.studentportal.domain.model.Student;
import com.acme.studentportal.domain.model.UserAccount;
import com.acme.studentportal.web.request.RegisterUserRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

public interface UserService extends UserDetailsService {
    UserAccount registerNewUser(RegisterUserRequest request);
    Optional<UserAccount> getCurrentUser();
    Optional<Student> getCurrentStudent();
    boolean isCurrentUserStudent();
    
    // From UserDetailsService
    @Override
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
}
