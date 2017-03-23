package com.epam.training.dao;

import com.epam.training.entity.User;

import java.util.List;

public interface UserDAO {
    User save(User user);
    void delete(User user);
    List<User> getUsers();
}
