package com.epam.training.dao.impl;

import com.epam.training.dao.UserDAO;
import com.epam.training.entity.User;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public class UserDAOImpl implements UserDAO {
    @PersistenceContext
    private EntityManager entityManager;

    private String GET_USERS = "from USERS";

    @Override
    public User save(User user) {
        entityManager.persist(user);
        return user;
    }

    @Override
    public void delete(User user) {
        entityManager.remove(user);
    }

    @Override
    public List<User> getUsers() {
        List<User> userList = entityManager.createQuery(GET_USERS).getResultList();
        return userList;
    }
}
