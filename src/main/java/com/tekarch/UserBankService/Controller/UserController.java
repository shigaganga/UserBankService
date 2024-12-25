
package com.tekarch.UserBankService.Controller;
import com.tekarch.UserBankService.DTO.AccountDTO;
import com.tekarch.UserBankService.Models.User;
import com.tekarch.UserBankService.Services.UserServiceImpl;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
public class UserController {

   @Autowired
    private final UserServiceImpl userServiceImpl;
    private static final Logger logger = LogManager.getLogger(UserController.class);

    @PostMapping("/users")
    public ResponseEntity<User> addUser(@RequestBody User user) {
        logger.info("Adding a new user");
        User createdUser = userServiceImpl.addUser(user);
        logger.info("New user created with ID: {}", createdUser.getUserId());
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> userList = userServiceImpl.getAllUsers();
        return new ResponseEntity<>(userList, HttpStatus.OK);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User userObj = userServiceImpl.getUserById(id);
        if (userObj == null) {
            logger.warn("User not found with ID: {}", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(userObj, HttpStatus.OK);
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUserById(@PathVariable Long id, @RequestBody User user) {
        logger.info("Updating user details for user ID: {}", id);
        User updatedUser = userServiceImpl.updateUser(id, user);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable Long id) {
        boolean isDeleted = userServiceImpl.deleteUserById(id);
        if (isDeleted) {
            logger.info("User with ID: {} deleted successfully", id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            logger.warn("User with ID: {} not found for deletion", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/users/{userId}/kyc")
    public ResponseEntity<User> submitKyc(@PathVariable Long userId, @RequestBody User user) {
        logger.info("Submitting KYC for user ID: {}", userId);
        User updatedUser = userServiceImpl.submitKyc(userId, user);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @GetMapping("/users/{userId}/kyc/status")
    public ResponseEntity<String> getKycStatus(@PathVariable Long userId) {
        logger.info("Fetching KYC status for user ID: {}", userId);
        String kycStatus = userServiceImpl.getKycStatus(userId);
        if (kycStatus == null) {
            logger.warn("User not found or KYC status missing for ID: {}", userId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(kycStatus, HttpStatus.OK);
    }

    @GetMapping("/users/{userId}/kyc")
    public ResponseEntity<User> getKycDetails(@PathVariable Long userId) {
        logger.info("Fetching KYC details for user ID: {}", userId);
        User user = userServiceImpl.getUserById(userId);
        if (user == null) {
            logger.warn("No user found with ID: {}", userId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(user, HttpStatus.OK); // You may want to return only KYC details, not the whole user.
    }

    @DeleteMapping("/users/{userId}/kyc")
    public ResponseEntity<Void> deleteKycDetails(@PathVariable Long userId) {
        logger.info("Deleting KYC details for user ID: {}", userId);
        userServiceImpl.deleteKycDetail(userId);
        logger.info("Deleted KYC details for user ID: {}", userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @PostMapping("/users/{userId}/accounts")
    public ResponseEntity<AccountDTO> addLinkedAccount(@PathVariable Long userId, @RequestBody AccountDTO account) {
        AccountDTO createdAccount = userServiceImpl.addLinkedAccount(userId, account);
        return new ResponseEntity<>(createdAccount, HttpStatus.CREATED);
    }

    @GetMapping("/users/{userId}/accounts")
    public ResponseEntity<List<AccountDTO>> getLinkedAccounts(@PathVariable Long userId) {
        List<AccountDTO> accounts = userServiceImpl.getLinkedAccounts(userId);
        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }

    @PutMapping("/users/{userId}/accounts/{accountId}")
    public ResponseEntity<AccountDTO> updateLinkedAccount(
            @PathVariable Long userId,
            @PathVariable Long accountId,
            @RequestBody AccountDTO account) {
        AccountDTO updatedAccount = userServiceImpl.updateLinkedAccount(userId, accountId, account);
        return new ResponseEntity<>(updatedAccount, HttpStatus.OK);
    }

    @DeleteMapping("/users/{userId}/accounts/{accountId}")
    public ResponseEntity<Void> deleteLinkedAccount(@PathVariable Long userId, @PathVariable Long accountId) {
        userServiceImpl.deleteLinkedAccount(userId, accountId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    // Exception Handler
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleExceptions(Exception e) {
        logger.error("Exception occurred: {}", e.getMessage());
        return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
