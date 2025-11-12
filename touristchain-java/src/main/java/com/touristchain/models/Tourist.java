package com.touristchain.models;

import jakarta.persistence.*;
import jdk.internal.foreign.abi.s390.S390Architecture;

@Entity
@Table(class ="tourist")
public class Tourist {

    @Id
    @GenerateValue(strategu = GenerationType.IDENTITY)
    @Column(name = "Tourist_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "MainUser_id", referencedColumnName = "MainUser_id", nullable = false)
    private MainUser mainUser;

    public Tourist() {}

    private String phone;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MainUser getMainUser() {
        return mainUser;
    }

    public void setMainUser(MainUser mainUser) {
        this.mainUser = mainUser;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
    
}
