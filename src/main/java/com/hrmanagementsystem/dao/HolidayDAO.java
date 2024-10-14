package com.hrmanagementsystem.dao;

import com.hrmanagementsystem.entity.Holiday;
import com.hrmanagementsystem.entity.User;
import com.hrmanagementsystem.enums.HolidayStatus;

import javax.persistence.*;
import java.util.List;

public class HolidayDAO {
    private static EntityManagerFactory emf = Persistence.createEntityManagerFactory("hr_management_pu");

    public static Holiday getById(int id) {
        EntityManager em = emf.createEntityManager();
        try{
            return em.find(Holiday.class, id);
        }finally {
            em.close();
        }
    }

    public static List<Holiday> getAcceptedHolidaysForEmployee(User employee) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Holiday> query = em.createQuery(
                    "SELECT h FROM Holiday h WHERE h.employee = :employee AND h.status = :status",
                    Holiday.class);
            query.setParameter("employee", employee);
            query.setParameter("status", HolidayStatus.Accepted);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public static void save(Holiday holiday) {
        EntityManager em = emf.createEntityManager();
        try{
            em.getTransaction().begin();
            em.persist(holiday);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
        }finally {
            em.close();
        }
    }

    public static List<Holiday> getAllHolidays() {
        EntityManager em = emf.createEntityManager();
        try{
            TypedQuery<Holiday> query = em.createQuery("FROM Holiday", Holiday.class);
            return query.getResultList();
        }finally {
            em.close();
        }
    }

    public static void update(Holiday holiday) {
        EntityManager em = emf.createEntityManager();
        try{
            em.getTransaction().begin();
            em.merge(holiday);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
}
