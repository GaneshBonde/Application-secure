package com.demo.applicationsecure.event.listener;

import com.demo.applicationsecure.entity.User;
import com.demo.applicationsecure.event.RegistrationCompleteEvent;
import com.demo.applicationsecure.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;


import java.util.UUID;

@Component
@Slf4j
public class RegistrationCompleteEventListener implements ApplicationListener<RegistrationCompleteEvent> {

    @Autowired
    private UserService userService;

    @Override
    public void onApplicationEvent(RegistrationCompleteEvent event) {
        // Create the Verification Token for the User with Link

        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        userService.saveVerificationTokenForUser(token,user);
        // Send Mail to user

        String url = event.getApplicationUrl()+"/verifyRegistration?token="+token;

        log.info("Click the link view to verify your account: {}",url);
    }

}
