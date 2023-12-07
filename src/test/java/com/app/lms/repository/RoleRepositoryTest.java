package com.app.lms.repository;

import com.app.lms.entity.Member;
import com.app.lms.entity.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @Test
    void itShouldCheckRoleExistsByName() {
        //given
        Role role = new Role();
        role.setName("ROLE_MEMBER");
        roleRepository.save(role);

        //when
        boolean exists = false;
        Role newRole = roleRepository.findByName("ROLE_MEMBER");

        if (newRole != null) exists = true;

        //then
        assertThat(exists).isTrue();
    }

    @Test
    void itShouldCheckIfRoleDoesNotExistsByName() {
        //given
        String role = "ROLE_MEMBER";

        //when
        boolean exists = true;
        Role newRole = roleRepository.findByName(role);
        if (newRole == null) exists = false;

        //then
        assertThat(exists).isFalse();
    }
}