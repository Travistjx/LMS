package com.app.lms.service.serviceimpl;

import com.app.lms.entity.Member;
import com.app.lms.entity.Role;
import com.app.lms.repository.MemberRepository;
import com.app.lms.repository.RoleRepository;
import com.app.lms.web.MemberDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MemberServiceImplTest {

    @Mock
    private MemberRepository memberRepository;
    private MemberServiceImpl memberService;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setup (){
        memberService = new MemberServiceImpl(memberRepository, roleRepository, passwordEncoder);
    }

    @Test
    void canFindAllUsers() {
        //when
        memberService.findAllUsers();
        verify(memberRepository).findAll();
    }

    @Test
    void canSaveUser() {
        MemberDto member = new MemberDto();
        member.setEmail("georgeng@gmail.com");

        member.setFirstName("george");
        member.setLastName("ng");
        member.setPassword("123");

        memberService.saveUser(member);

        ArgumentCaptor<Member> memberArgumentCaptor = ArgumentCaptor.forClass(Member.class);
        verify(memberRepository).save(memberArgumentCaptor.capture());
        Member capturedMember = memberArgumentCaptor.getValue();

        assertThat(capturedMember.getEmail()).isEqualTo("georgeng@gmail.com");
        assertThat(capturedMember.getFirstName()).isEqualTo("george");
        assertThat(capturedMember.getLastName()).isEqualTo("ng");
        assertThat(capturedMember.getPassword()).isEqualTo(passwordEncoder.encode("123"));
    }

    @Test
    void canFindByEmail() {
        String email = "georgeng@gmail.com";
        memberService.findByEmail(email);

        ArgumentCaptor<String> memberArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(memberRepository).findByEmail(memberArgumentCaptor.capture());
        String capturedEmail = memberArgumentCaptor.getValue();

        assertThat(capturedEmail).isEqualTo("georgeng@gmail.com");
    }



    @Test
    void canConvertEntityToDto() {
        Member member = new Member();
        member.setEmail("georgeng@gmail.com");
        member.setFirstName("george");
        member.setLastName("ng");
        member.setPassword("123");
        Collection<Role> role = new ArrayList<>();
        role.add(new Role("ROLE_MEMBER"));
        member.setRoles(role);

        MemberDto memberDto = memberService.convertEntityToDto(member);

        assertThat(memberDto.getEmail()).isEqualTo("georgeng@gmail.com");
        assertThat(memberDto.getFirstName()).isEqualTo("george");
        assertThat(memberDto.getLastName()).isEqualTo("ng");
        assertThat(memberDto.getPassword()).isNull(); // Password should not be exposed in DTO
        assertThat(memberDto.getRoles().stream().findFirst().get().getName())
                .isEqualTo(member.getRoles().stream().findFirst().get().getName());  // Compare the name of the first role
    }
}