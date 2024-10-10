package com.hrmanagementsystem.repository.implementations;

import com.hrmanagementsystem.entity.User;
import com.hrmanagementsystem.repository.interfaces.EmployeeRepository;

import javax.persistence.*;
import javax.transaction.Transactional;
import javax.persistence.EntityManager;

public class EmployeeRepositoryImpl implements EmployeeRepository {
    private EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("hrManagementPU");

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    @Override
    public void addUser(User user) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        entityManager.persist(user);
        entityManager.getTransaction().commit();
        entityManager.close();
    }

//    @Override
//    public List<User> getAllUsers() {
//        return entityManager.createQuery("FROM User", User.class).getResultList();
//    }
}
