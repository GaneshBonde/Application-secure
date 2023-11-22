package com.demo.applicationsecure.controller;



import com.demo.applicationsecure.entity.User;
import com.demo.applicationsecure.entity.VerificationToken;
import com.demo.applicationsecure.event.RegistrationCompleteEvent;
import com.demo.applicationsecure.modal.PasswordModal;
import com.demo.applicationsecure.modal.UserModal;
import com.demo.applicationsecure.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.UUID;

@RestController
@Slf4j
public class RegistrationController {


    @Autowired
    private UserService userService;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @PostMapping("/register")
    public String registerString(@RequestBody UserModal userModal, final HttpServletRequest request){
        User user =userService.registerUser(userModal);
        applicationEventPublisher.publishEvent(new RegistrationCompleteEvent(user, applicationUrl(request)));
        return "Success";
    }

    @GetMapping("/verifyRegistration")
    public String verifyRegistration(@RequestParam String token){

        String result = userService.validateVerificationToken(token);
        if(result.equalsIgnoreCase("valid")){
            return "User Verifies Successfully";
        }

        return "Bad User";
    }

    @GetMapping("/resendVerifyToken")
    public String resendVerificationToken(@RequestParam("token") String oldToken, HttpServletRequest request ){
        VerificationToken verificationToken = userService.generateNewVErificationToken(oldToken);
        User user = verificationToken.getUser();
        resendVerificationToken(user, applicationUrl(request),verificationToken);
        return "Verification Link Sent";
    }

    @PostMapping("/resetPassword")
    public String resetPassword(@RequestBody PasswordModal passwordModal, HttpServletRequest request){
        User user = userService.findByUserByEmail(passwordModal.getEmail());
        String url = "";
        if(user != null){
            String token = UUID.randomUUID().toString();
            userService.createPasswordResetTokenForUser(user, token);
            url = passwordResetTokenMail(user,applicationUrl(request), token);

        }
        return url;
    }

    @PostMapping("/savePassword")
    public String savePassword(@RequestParam("token") String token , @RequestBody PasswordModal passwordModal){
        String result = userService.validatePasswordResetToken(token);

        if(!result.equalsIgnoreCase("valid")){
            return "Invalid Token";
        }
        Optional<User> user = userService.getUserByPasswordResetToken(token);
        if(user.isPresent()){
            userService.changePassword(user.get(),passwordModal.getNewPassword());
            return "Password Reset Successfully";
        }else {
            return "Invalid Token";
        }
    }

    @PostMapping("/changePassword")
    public String changePassword(@RequestBody PasswordModal passwordModal){
        User user= userService.findByUserByEmail(passwordModal.getEmail());
        if(!userService.checkIfValidOldPassword(user,passwordModal.getOldPassword())){
            return "Invalid Old Password !!";
        }
        // Save New Password
        userService.changePassword(user,passwordModal.getNewPassword());
        return "Password Changed Successfully";
    }

    private String passwordResetTokenMail(User user, String applicationUrl, String token) {
        String url = applicationUrl+"/savePassword?token="+token;

        log.info("Click the link view to verify your account: {}",url);
        return url;
    }

    private void resendVerificationToken(User user, String applicationUrl,VerificationToken verificationToke) {

        String url = applicationUrl+"/savePassword?token="+verificationToke.getToken();

        log.info("Click the link view to Reset your Password: {}",url);
    }

    private String applicationUrl(HttpServletRequest request){
        return "http://"+request.getServerName() + ":" +request.getServerPort() +request.getContextPath();
    }
}
