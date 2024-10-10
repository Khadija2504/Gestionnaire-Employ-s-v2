package com.hrmanagementsystem.entity;

import com.hrmanagementsystem.enums.HolidayStatus;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "holidays")
public class Holiday {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private Date startDate;
    private Date endDate;
    private String reason;

    @Enumerated(EnumType.STRING)
    private HolidayStatus status;

    public Holiday() {

    }

}

