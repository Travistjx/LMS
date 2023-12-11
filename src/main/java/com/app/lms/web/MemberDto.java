package com.app.lms.web;

import com.app.lms.entity.Gender;
import com.app.lms.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Collection;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MemberDto {

    private Long member_id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;

    private Collection<RoleDto> roles;

    private LocalDate birthday;

    private Gender gender;

    private String addressOne;

    private String addressTwo;

    private String postalCode;
}
