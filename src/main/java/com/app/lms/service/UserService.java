package com.app.lms.service;

import com.app.lms.entity.User;
import com.app.lms.web.UserDto;

import java.util.List;

public interface UserService {
    void saveUser(UserDto registration);
    User findByEmail(String email);

    List<UserDto> findAllUsers();
}
