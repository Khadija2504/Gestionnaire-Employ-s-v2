package com.hrmanagementsystem.dao;

import com.hrmanagementsystem.entity.JobOffer;
import com.hrmanagementsystem.enums.JobOfferStatus;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import java.util.List;

public class JobOfferDAO {
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("hr_management_pu");

    public static JobOffer getById(int id) {
        EntityManager em = emf.createEntityManager();
        try{
            return em.find(JobOffer.class, id);
        } finally {
            em.close();
        }
    }

    public static void save(JobOffer jobOffer) {
        EntityManager em = emf.createEntityManager();
        try{
            em.getTransaction().begin();
            em.persist(jobOffer);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    public static void update(JobOffer jobOffer) {
        EntityManager em = emf.createEntityManager();
        try{
            em.getTransaction().begin();
            em.persist(jobOffer);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    public static void delete(int id) {
        EntityManager em = emf.createEntityManager();
        try{
            em.getTransaction().begin();
            JobOffer jobOffer = em.find(JobOffer.class, id);
            if(jobOffer != null) {
                em.remove(jobOffer);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    public static List<JobOffer> getAll() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<JobOffer> query = em.createQuery("SELECT jo FROM JobOffer jo", JobOffer.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    public static List<JobOffer> getByStatus(JobOfferStatus status) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<JobOffer> query = em.createQuery("FROM JobOffer WHERE status = :status", JobOffer.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}
