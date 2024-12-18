package com.tekarch.UserBankService.DTO;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Getter
@Setter
public class AccountDTO {
    private Long accountId;            // SERIAL PRIMARY KEY
    private Long userId;               // FOREIGN KEY (users.user_id)
    private String accountNumber;      // UNIQUE, NOT NULL
    private String accountType;        // NOT NULL
    private BigDecimal balance;        // DECIMAL(15, 2) DEFAULT 0.0
    private String currency;           // DEFAULT 'USD'
    private Timestamp createdAt;       // DEFAULT CURRENT_TIMESTAMP
}
