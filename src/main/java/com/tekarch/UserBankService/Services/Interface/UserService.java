package com.tekarch.UserBankService.Services.Interface;

import com.tekarch.UserBankService.Models.User;

import java.util.List;

public interface UserService {

    User addUser(User user);
    List<User> getAllUsers();
    User getUserById(Long id);
    User updateUser(Long id,User user);
    boolean  deleteUserById(Long id);
    // User deleteUserById(Long id);
    User submitKyc(Long userId,User user);
    String getKycStatus(Long userId);
   // User getKycDetail(Long userId);
    void deleteKycDetail(Long userId);
    User updateUserKycDetails(Long userId,User updatedUser);
}
