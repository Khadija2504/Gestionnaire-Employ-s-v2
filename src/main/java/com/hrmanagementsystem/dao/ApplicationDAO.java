package com.hrmanagementsystem.dao;

import com.hrmanagementsystem.entity.Application;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import java.util.List;

public class ApplicationDAO {
    private static EntityManagerFactory emf = Persistence.createEntityManagerFactory("hr_management_pu");

    public static Application getbyId(int id) {
        EntityManager em = emf.createEntityManager();
        try{
            return em.find(Application.class,id);
        } finally {
            em.close();
        }
    }

    public static void save(Application application) {
        EntityManager em = emf.createEntityManager();
        try{
            em.getTransaction().begin();
            em.persist(application);
            em.getTransaction().commit();
        }catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    public static List<Application> getAllByJobOfferId(int jobOfferId) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Application> query = em.createQuery(
                    "SELECT a FROM Application a WHERE a.jobOffer.id = :jobOfferId",
                    Application.class
            );
            query.setParameter("jobOfferId", jobOfferId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}
