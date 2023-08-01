package com.example.Bankregistration.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(schema="admin_table")
public class Admin {

    @Id
    private String userId;
    private String name;
    private String password;
}
