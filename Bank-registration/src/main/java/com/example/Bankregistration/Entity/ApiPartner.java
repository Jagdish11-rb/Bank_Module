package com.example.Bankregistration.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(schema="api_partners")
public class ApiPartner {
    @Id
    private String id;
    private String username;
    private String adminName;
    private String adminId;
    private boolean isActive;

}
