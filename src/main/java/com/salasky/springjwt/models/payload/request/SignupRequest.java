package com.salasky.springjwt.models.payload.request;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

import javax.validation.constraints.*;

@Getter
@Setter
public class SignupRequest {
  @NotBlank
  @Size(min = 3, max = 20)
  private String username;

  @NotBlank
  @Size(max = 50)
  @Email
  private String email;

  private Set<String> role;

  @NotBlank
  @Size(min = 6, max = 40)
  private String password;

  @NotBlank
  @Size(min = 3, max = 40)
  private String firstName;


  private String secondName;


  private String lastName;

  @NotBlank
  private String jobTitle;

  @NotBlank
  private String subdivisionName;

  @NotBlank
  private String companyName;

}
