package org.practice.finalprojectsf.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", unique = true, nullable = false)
    private String userId;
    
    @Column(name = "balance", nullable = false)
    private Double balance = 0.0;
    
    public User(String userId) {
        this.userId = userId;
        this.balance = 0.0;
    }
    
    public User(String userId, Double balance) {
        this.userId = userId;
        this.balance = balance;
    }
}

