package com.learnudemy.service;

import com.learnudemy.entity.Users;
import com.learnudemy.exception.EmailExistException;
import com.learnudemy.exception.UsernameExistException;

import java.util.List;

public interface UserService {

    Users register(String firstName, String lastName, String username, String email, String password) throws EmailExistException, UsernameExistException;

    List<Users> getAllUser();

    Users findByUsername(String username);

    Users findByEmail(String email);

}
