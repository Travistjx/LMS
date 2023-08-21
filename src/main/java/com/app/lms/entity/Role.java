package com.app.lms.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "role")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long role_id;
    private String name;

    public Role() {

    }

    public Role(String name) {
        this.name = name;
    }

    public Long getId() {
        return role_id;
    }
    public void setId(Long id) {
        this.role_id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}

