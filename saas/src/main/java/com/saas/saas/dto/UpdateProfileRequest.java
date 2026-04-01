package com.saas.saas.dto;

import jakarta.validation.constraints.NotBlank;

public class UpdateProfileRequest {

    @NotBlank(
            message="Name required"
    )
    private String name;

    public UpdateProfileRequest(){}

    public String getName(){
        return name;
    }

    public void setName(
            String name
    ){
        this.name=name;
    }

}