package com.tekarch.UserBankService.Services;
import com.tekarch.UserBankService.Controller.UserController;
import com.tekarch.UserBankService.DTO.AccountDTO;
import com.tekarch.UserBankService.Models.User;
import com.tekarch.UserBankService.Repositories.UserRepository;
import com.tekarch.UserBankService.Services.Interface.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.RestTemplate;

import java.util.List;
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private static final String ACCOUNT_MS_URL = "http://localhost:9093/accounts";
    private final RestTemplate restTemplate;
    private static final Logger logger= LogManager.getLogger(UserController.class);

    private boolean isAccountExists(Long accountId) {
        String url = ACCOUNT_MS_URL + "/" + accountId;
        try {
            logger.info("Validating user existence for User ID: {}", accountId);
            ResponseEntity<AccountDTO> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    HttpEntity.EMPTY,
                    AccountDTO.class
            );
            if(response.getStatusCode() == HttpStatus.OK) {
                return true;
            }else{
                return false;
            }

        } catch (Exception e) {
            logger.error("Error validating Account existence for Account ID {}: {}", accountId, e.getMessage());
            return false;
        }}
    @Override
    public User addUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
    /*
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + id));
    }
     */

    @Override
    public User updateUser(Long id, User updateUser) {
        User existingUser = getUserById(id);
        existingUser.setUsername(updateUser.getUsername());
        existingUser.setEmail(updateUser.getEmail());
        existingUser.setPassword_hash(updateUser.getPassword_hash());
        existingUser.setPhone_number(updateUser.getPhone_number());
        existingUser.setTwo_factor_enabled(updateUser.getTwo_factor_enabled());
        existingUser.setKycStatus(updateUser.getKycStatus());
        return userRepository.save(existingUser);
    }

    @Override
    public boolean deleteUserById(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    /*public User submitKyc(Long userId, User user) {
        Optional<User> existingUserOpt = userRepository.findById(userId);
        if (existingUserOpt.isPresent()) {
            User existingUser = existingUserOpt.get();
            existingUser.setKycStatus(user.getKycStatus());
            existingUser.setKycDocuments(user.getKycDocuments());
            return userRepository.save(existingUser); // Save updated KYC details
        }
        return null; // user not found

     */

    @Override
    public User submitKyc(Long userId, User user) {
        User existingUser = getUserById(userId);
        existingUser.setKycStatus(user.getKycStatus());
        return userRepository.save(existingUser);
    }

    @Override
    public String getKycStatus(Long userId) {
        User user = getUserById(userId);
        return user.getKycStatus();
    }

    @Override
    public void deleteKycDetail(Long userId) {
        User user = getUserById(userId);
        user.setKycStatus("Pending");
        userRepository.save(user);
    }

    @Override
    public User updateUserKycDetails(Long userId, User updatedUser) {
        User existingUser = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));
        existingUser.setUsername(updatedUser.getUsername());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setPassword_hash(updatedUser.getPassword_hash());
        existingUser.setPhone_number(updatedUser.getPhone_number());
        existingUser.setTwo_factor_enabled(updatedUser.getTwo_factor_enabled());
        return userRepository.save(existingUser);
    }


    @ExceptionHandler
    public ResponseEntity<?> respondWithError(Exception e){
        logger.error("Exception Occured while creating a student.Details:{}",e.getMessage());
        return new ResponseEntity<>("could not create a student Exception occured.More info:"+e.getMessage(),HttpStatus.BAD_REQUEST);

    }
    @Override
    public AccountDTO addLinkedAccount(Long userId, AccountDTO account) {
        String url = ACCOUNT_MS_URL;
        logger.info("Adding linked account for user ID: {}", userId);
        account.setUserId(userId);
        try {
            ResponseEntity<AccountDTO> response = restTemplate.postForEntity(url, account, AccountDTO.class);
            return response.getBody();
        } catch (Exception e) {
            logger.error("Error occurred while adding linked account: {}", e.getMessage());
            throw new RuntimeException("Failed to add linked account. Please try again.");
        }
    }

    @Override
    public List<AccountDTO> getLinkedAccounts(Long userId) {
        String url = ACCOUNT_MS_URL + "/users/" + userId;
        logger.info("Fetching linked accounts for user ID: {}", userId);
        try {
            ResponseEntity<List<AccountDTO>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<AccountDTO>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            logger.error("Error occurred while fetching linked accounts: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch linked accounts. Please try again.");
        }
    }

    @Override
    public AccountDTO updateLinkedAccount(Long userId, Long accountId, AccountDTO account) {
        String url = ACCOUNT_MS_URL + "/" + accountId;
        logger.info("Updating linked account ID: {} for user ID: {}", accountId, userId);
        account.setUserId(userId);
        try {
            restTemplate.put(url, account);
            return account; // Returning the updated account object
        } catch (Exception e) {
            logger.error("Error occurred while updating linked account: {}", e.getMessage());
            throw new RuntimeException("Failed to update linked account. Please try again.");
        }
    }

    @Override
    public void deleteLinkedAccount(Long userId, Long accountId) {
        String url = ACCOUNT_MS_URL + "/" + accountId;
        logger.info("Deleting linked account ID: {} for user ID: {}", accountId, userId);
        try {
            restTemplate.delete(url);
        } catch (Exception e) {
            logger.error("Error occurred while deleting linked account: {}", e.getMessage());
            throw new RuntimeException("Failed to delete linked account. Please try again.");
        }
    }

}


