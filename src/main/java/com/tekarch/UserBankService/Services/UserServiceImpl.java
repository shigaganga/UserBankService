package com.tekarch.UserBankService.Services;

import com.tekarch.UserBankService.DTO.AccountDTO;
import com.tekarch.UserBankService.Models.User;
import com.tekarch.UserBankService.Repositories.UserRepository;
import com.tekarch.UserBankService.Services.Interface.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RestTemplate restTemplate;

    @Value("${account.ms.url:http://localhost:8081/accounts}")
    private String accountMsUrl;

    private static final Logger logger = LogManager.getLogger(UserServiceImpl.class);

    private boolean isAccountExists(Long accountId) {
        String url = accountMsUrl + "/" + accountId;
        try {
            logger.info("Validating account existence for Account ID: {}", accountId);
            ResponseEntity<AccountDTO> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    HttpEntity.EMPTY,
                    AccountDTO.class
            );
            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            logger.error("Error occurred while validating Account existence for Account ID {}: {}", accountId, e.getMessage(), e);
            return false;
        }
    }

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

    @Override
    public User updateUser(Long id, User updateUser) {
        User existingUser = getUserById(id);
        if (existingUser == null) {
            throw new EntityNotFoundException("User not found with ID: " + id);
        }
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

    @Override
    public User submitKyc(Long userId, User user) {
        User existingUser = getUserById(userId);
        if (existingUser == null) {
            throw new EntityNotFoundException("User not found with ID: " + userId);
        }
        existingUser.setKycStatus(user.getKycStatus());
        return userRepository.save(existingUser);
    }

    @Override
    public String getKycStatus(Long userId) {
        User user = getUserById(userId);
        if (user == null) {
            throw new EntityNotFoundException("User not found with ID: " + userId);
        }
        return user.getKycStatus();
    }

    @Override
    public void deleteKycDetail(Long userId) {
        User user = getUserById(userId);
        if (user == null) {
            throw new EntityNotFoundException("User not found with ID: " + userId);
        }
        user.setKycStatus("Pending");
        userRepository.save(user);
    }

    @Override
    public User updateUserKycDetails(Long userId, User updatedUser) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));
        existingUser.setUsername(updatedUser.getUsername());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setPassword_hash(updatedUser.getPassword_hash());
        existingUser.setPhone_number(updatedUser.getPhone_number());
        existingUser.setTwo_factor_enabled(updatedUser.getTwo_factor_enabled());
        return userRepository.save(existingUser);
    }

    @Override
    public AccountDTO addLinkedAccount(Long userId, AccountDTO account) {
        String url = accountMsUrl;
        logger.info("Adding linked account for user ID: {}", userId);
        account.setUserId(userId);
        try {
            ResponseEntity<AccountDTO> response = restTemplate.postForEntity(url, account, AccountDTO.class);
            return response.getBody();
        } catch (Exception e) {
            logger.error("Error occurred while adding linked account for user ID {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Failed to add linked account. Please try again.");
        }
    }

    @Override
    public List<AccountDTO> getLinkedAccounts(Long userId) {
        String url = accountMsUrl + "/users/" + userId;
        logger.info("Fetching linked accounts for user ID: {}", userId);
        try {
            ResponseEntity<List<AccountDTO>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<AccountDTO>>() {
                    }
            );
            if (response.getBody() == null || response.getBody().isEmpty()) {
                logger.warn("No linked accounts found for user ID: {}", userId);
            }
            return response.getBody();
        } catch (Exception e) {
            logger.error("Error occurred while fetching linked accounts for user ID {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Failed to fetch linked accounts. Please try again.");
        }
    }

    @Override
    public AccountDTO updateLinkedAccount(Long userId, Long accountId, AccountDTO account) {
        String url = accountMsUrl + "/" + accountId;
        logger.info("Updating linked account ID: {} for user ID: {}", accountId, userId);
        account.setUserId(userId);
        try {
            restTemplate.put(url, account);
            return account; // Returning the updated account object
        } catch (Exception e) {
            logger.error("Error occurred while updating linked account ID {} for user ID {}: {}", accountId, userId, e.getMessage(), e);
            throw new RuntimeException("Failed to update linked account. Please try again.");
        }
    }

    @Override
    public void deleteLinkedAccount(Long userId, Long accountId) {
        String url = accountMsUrl + "/" + accountId;
        logger.info("Deleting linked account ID: {} for user ID: {}", accountId, userId);
        try {
            if (!isAccountExists(accountId)) {
                throw new EntityNotFoundException("Account not found with ID: " + accountId);
            }
            restTemplate.delete(url);
        } catch (Exception e) {
            logger.error("Error occurred while deleting linked account ID {} for user ID {}: {}", accountId, userId, e.getMessage(), e);
            throw new RuntimeException("Failed to delete linked account. Please try again.");
        }
    }
}
