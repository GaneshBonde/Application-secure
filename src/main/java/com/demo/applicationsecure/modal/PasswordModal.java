package com.demo.applicationsecure.modal;

import lombok.Data;

@Data
public class PasswordModal {

    private String email;
    private String oldPassword;
    private String newPassword;
}
