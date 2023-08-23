package com.app.lms.repository;

import com.app.lms.entity.Member;
import com.app.lms.entity.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Arrays;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class MemberRepositoryTest {

    @Autowired
    private MemberRepository underTest;

    @Test
    void itShouldCheckIfMemberExistsByEmail() {

        //given
        Member member = new Member();
        member.setEmail("georgeng@gmail.com");

        member.setFirstName("george");
        member.setLastName("ng");
        member.setPassword("123");

        Role role = new Role();
        role.setName("ROLE_MEMBER");
        member.setRoles(Arrays.asList(role));
        underTest.save(member);

        //when
        boolean exists = false;
        Member newMember = underTest.findByEmail("georgeng@gmail.com");
        if (newMember != null) exists = true;

        //then
        assertThat(exists).isTrue();
    }

    @Test
    void itShouldCheckIfMemberDoesNotExistsByEmail() {

        //given
        String email = "georgeng@gmail.com";

        //when
        boolean exists = true;
        Member newMember = underTest.findByEmail(email);
        if (newMember == null) exists = false;

        //then
        assertThat(exists).isFalse();
    }
}