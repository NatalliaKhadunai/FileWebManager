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

    private String GET_USERS = "from User";
    private String GET_USER_BY_USERNAME = "from User U where U.username=:username";

    @Override
    public User save(User user) {
        entityManager.persist(user);
        return user;
    }

    @Override
    public User update(User user) {
        entityManager.merge(user);
        return user;
    }

    @Override
    public void delete(User user) {
        entityManager.remove(entityManager.contains(user) ? user : entityManager.merge(user));
    }

    @Override
    public List<User> getUsers() {
        List<User> userList = entityManager.createQuery(GET_USERS).getResultList();
        return userList;
    }

    @Override
    public User getUser(String username) {
        User user = (User)entityManager.createQuery(GET_USER_BY_USERNAME)
                .setParameter("username", username)
                .getSingleResult();
        return user;
    }
}
