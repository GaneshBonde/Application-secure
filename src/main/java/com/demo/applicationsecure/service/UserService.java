package com.demo.applicationsecure.service;



import com.demo.applicationsecure.entity.User;
import com.demo.applicationsecure.entity.VerificationToken;
import com.demo.applicationsecure.modal.UserModal;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface UserService {
    User registerUser(UserModal userModal);

    void saveVerificationTokenForUser(String token, User user);

    String validateVerificationToken(String token);

    VerificationToken generateNewVErificationToken(String oldToken);

    User findByUserByEmail(String email);

    void createPasswordResetTokenForUser(User user, String token);

    String validatePasswordResetToken(String token);

    Optional<User> getUserByPasswordResetToken(String token);

    void changePassword(User user, String newPassword);

    boolean checkIfValidOldPassword(User user, String oldPassword);
}
