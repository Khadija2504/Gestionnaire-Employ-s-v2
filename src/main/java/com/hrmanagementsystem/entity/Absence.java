package com.hrmanagementsystem.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "absences")
public class Absence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private Date absenceDate;
    private String reason;
    private boolean isAccepted;

    public Absence() {
    }

}
