package com.hrmanagementsystem.dao.implementations;

import com.hrmanagementsystem.dao.interfaces.UserInterface;
import com.hrmanagementsystem.entity.User;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

public class UserDAO implements UserInterface {
    private static EntityManager entityManager;

    public UserDAO(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public User findByEmail(String email) {
        try {
            return entityManager.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}