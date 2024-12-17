package com.tekarch.UserBankService.Models;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name="users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    @Column(nullable = false,unique = true,length = 50)
    private String username;
    @Column(nullable = false,unique = true,length = 100)
    private String email;
    @Column(nullable = false)
    private String password_hash ;
    @Column(unique = true,length = 15)
    private String phone_number;
    @Column(nullable = false)
    private Boolean two_factor_enabled=false;
    @Column(nullable = false,length = 20)
    private String kycStatus;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    /*List to store KYC document paths or filenames
    @ElementCollection // This annotation is used for storing a collection of basic types like String in a separate table
    @CollectionTable(name = "kyc_documents", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "document")
    private List<String> kycDocuments;  // List to store KYC document paths or filenames*/

    // @Column(nullable = false)
    //private LocalDateTime created_at=LocalDateTime.now();
    // @Column(nullable = false)
    // private LocalDateTime updated_at=LocalDateTime.now();
//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
//    // Defines a one-to-many relationship with the `Account` entity, with cascading delete and orphan removal.
//    private List<Account> accounts;

}