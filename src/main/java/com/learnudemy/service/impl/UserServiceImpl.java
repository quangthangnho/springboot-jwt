package com.learnudemy.service.impl;

import com.learnudemy.entity.Users;
import com.learnudemy.enums.Role;
import com.learnudemy.exception.EmailExistException;
import com.learnudemy.exception.UsernameExistException;
import com.learnudemy.model.UserPrincipal;
import com.learnudemy.repository.UsersRepository;
import com.learnudemy.service.LoginAttemptService;
import com.learnudemy.service.UserService;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

@Service
@Transactional
@Qualifier("UserDetailsService")
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UsersRepository repository;

    private final BCryptPasswordEncoder passwordEncoder;

    private final LoginAttemptService loginAttemptService;

    @Autowired
    public UserServiceImpl(UsersRepository repository, BCryptPasswordEncoder passwordEncoder, LoginAttemptService loginAttemptService) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.loginAttemptService = loginAttemptService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user = repository.findByUsername(username);
        if (Objects.isNull(user)) {
            throw new UsernameNotFoundException("User not found by username: " + username);
        } else {
            validateLoginAttempt(user);
            user.setLastLoginDateDisplay(user.getLastLoginDate());
            user.setLastLoginDate(new Date());
            repository.save(user);
            return new UserPrincipal(user);
        }
    }

    private void validateLoginAttempt(Users user){
        if(user.isNotLocked()) {
            user.setNotLocked(!loginAttemptService.hasExceededMaxAttempts(user.getUsername()));
        } else {
            loginAttemptService.evicUserFromCache(user.getUsername());
        }
    }

    @Override
    public Users register(String firstName, String lastName, String username, String email, String password) throws EmailExistException, UsernameExistException {
        validateNewUsernameAndEmail(StringUtils.EMPTY, username, email);
        Users user = new Users();
        user.setUserId(generateUserId());
        String encodePassword = encodePassword(password);
        user.setFirstname(firstName);
        user.setLastname(lastName);
        user.setUsername(username);
        user.setEmail(email);
        user.setJoinDate(new Date());
        user.setPassword(encodePassword);
        user.setActive(true);
        user.setNotLocked(true);
        user.setRole(Role.ROLE_USER.name());
        user.setAuthorities(Role.ROLE_USER.getAuthorities());
        user.setProfileImageUrl(getTemporatyProfileImageUrl());
        repository.save(user);
        return null;
    }

    private String getTemporatyProfileImageUrl() {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path("/user/image/profile/temp").toUriString();
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    private String generateUserId() {
        return RandomStringUtils.randomNumeric(10);
    }

    private void validateNewUsernameAndEmail(String currentUsername, String newUsername, String newEmail) throws UsernameExistException, EmailExistException {
        if (StringUtils.isNotBlank(currentUsername)) {
            Users currentUser = findByUsername(currentUsername);
            if (currentUser == null) {
                throw new UsernameNotFoundException("No user found by username " + currentUsername);
            }
            Users userByUsername = findByUsername(newUsername);
            if (userByUsername != null && !currentUser.getId().equals(userByUsername.getId())) {
                throw new UsernameExistException("Username already exists");
            }
            Users userByEmail = findByEmail(newEmail);
            if (userByEmail != null && !currentUser.getId().equals(userByEmail.getId())) {
                throw new EmailExistException("Email already exists");
            }
        } else {
            Users userByUsername = findByUsername(newUsername);
            if (userByUsername != null) {
                throw new UsernameExistException("username already exists");
            }
            Users userByEmail = findByEmail(newEmail);
            if (userByEmail != null) {
                throw new EmailExistException("email already exists");
            }
        }
    }

    @Override
    public List<Users> getAllUser() {
        return repository.findAll();
    }

    @Override
    public Users findByUsername(String username) {
        return repository.findByUsername(username);
    }

    @Override
    public Users findByEmail(String email) {
        return repository.findByEmail(email);
    }
}
