package com.demo.applicationsecure.modal;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserModal {


    private String firstName;

    private String lastName;

    private String email;

    private String password;

    private  String matchingPassword;

}
