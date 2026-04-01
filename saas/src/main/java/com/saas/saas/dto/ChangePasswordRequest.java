package com.saas.saas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ChangePasswordRequest {

    @NotBlank(
            message="Old password required"
    )
    private String oldPassword;

    @Size(
            min=6,
            message="Password minimum 6 characters"
    )
    private String newPassword;

    public ChangePasswordRequest(){}

    public String getOldPassword(){  //get teh password old from the user and temp store in this method when needed called by some function.

        return oldPassword;

    }

    public void setOldPassword(

            String oldPassword

    ){

        this.oldPassword =
                oldPassword;

    }

    public String getNewPassword(){

        return newPassword;

    }

    public void setNewPassword(

            String newPassword

    ){

        this.newPassword =
                newPassword;

    }

}