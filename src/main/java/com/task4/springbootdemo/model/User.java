package com.task4.springbootdemo.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;
    @Column(name = "email")
    private String email;
    @Column(name = "password")
    private String password;
    @Column(name = "registration_date")
    private String registrationDate;
    @Column(name = "last_login_date")
    private String lastLoginDate;
    @Column(name = "is_blocked")
    private boolean isBlocked;

    public String toString() {
        return (id + " " + name + " " + email);
    }
}
