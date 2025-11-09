package com.touristchain.models;

import jakarta.persistence.*;

@Entity
@Table(name = "administrator")

public class Administrator {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Administrator_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "MainUser_id", referencedColumnName = "MainUser_id", nullable = false)
    private MainUser mainUser;

    private String details;

    public Administrator() {}

    //Getters and Setters
    public Long getId() { return id;}

    public void setId(Long id) { this.id = id; }

    public MainUser getMainUser() { return mainUser; }

    public void setMainUser(MainUser mainUser) { this.mainUser = mainUser; }

    public String getDetails() { return details; }

    public void setDetails(String details) { this.details = details; }

}