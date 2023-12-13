package com.app.lms.service.serviceimpl;

import com.app.lms.entity.Gender;
import com.app.lms.entity.Member;
import com.app.lms.entity.Role;
import com.app.lms.repository.MemberRepository;
import com.app.lms.repository.RoleRepository;
import com.app.lms.web.MemberDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

        when(passwordEncoder.encode("123")).thenReturn("456");

        memberService.saveUser(member);

        ArgumentCaptor<Member> memberArgumentCaptor = ArgumentCaptor.forClass(Member.class);
        verify(memberRepository).save(memberArgumentCaptor.capture());
        Member capturedMember = memberArgumentCaptor.getValue();

        assertThat(capturedMember.getEmail()).isEqualTo("georgeng@gmail.com");
        assertThat(capturedMember.getFirstName()).isEqualTo("george");
        assertThat(capturedMember.getLastName()).isEqualTo("ng");
        assertThat(capturedMember.getPassword()).isEqualTo("456");
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
    void canFindById(){
        memberService.findById((long)1);
        verify(memberRepository).findById((long)1);
    }

    @Test
    void canUpdateName(){
        Member member = new Member();
        member.setEmail("georgeng@gmail.com");
        when(memberRepository.findByEmail("georgeng@gmail.com")).thenReturn(member);


        MemberDto memberDto = new MemberDto();
        memberDto.setFirstName("george");
        memberDto.setLastName("ng");
        memberService.updateName(memberDto, "georgeng@gmail.com");

        ArgumentCaptor<Member> memberArgumentCaptor = ArgumentCaptor.forClass(Member.class);
        verify(memberRepository).save(memberArgumentCaptor.capture());
        Member capturedMember = memberArgumentCaptor.getValue();

        assertThat(capturedMember.getFirstName()).isEqualTo(memberDto.getFirstName());
        assertThat(capturedMember.getLastName()).isEqualTo(memberDto.getLastName());

    }

    @Test
    void canUpdateEmail(){
        Member member = new Member();
        member.setEmail("georgeng@gmail.com");
        when(memberRepository.findByEmail("georgeng@gmail.com")).thenReturn(member);


        MemberDto memberDto = new MemberDto();
        memberDto.setEmail("georgeng1@gmail.com");
        memberService.updateEmail(memberDto, "georgeng@gmail.com");

        ArgumentCaptor<Member> memberArgumentCaptor = ArgumentCaptor.forClass(Member.class);
        verify(memberRepository).save(memberArgumentCaptor.capture());
        Member capturedMember = memberArgumentCaptor.getValue();

        assertThat(capturedMember.getEmail()).isEqualTo(memberDto.getEmail());
    }

    @Test
    void canUpdateGender(){
        Member member = new Member();
        member.setGender(Gender.MALE);
        when(memberRepository.findByEmail("georgeng@gmail.com")).thenReturn(member);

        MemberDto memberDto = new MemberDto();
        memberDto.setGender(Gender.FEMALE);
        memberService.updateGender(memberDto, "georgeng@gmail.com");

        ArgumentCaptor<Member> memberArgumentCaptor = ArgumentCaptor.forClass(Member.class);
        verify(memberRepository).save(memberArgumentCaptor.capture());
        Member capturedMember = memberArgumentCaptor.getValue();

        assertThat(capturedMember.getGender()).isEqualTo(memberDto.getGender());
    }

    @Test
    void canUpdateAddressOne(){
        Member member = new Member();
        member.setAddressOne("xxx");
        when(memberRepository.findByEmail("georgeng@gmail.com")).thenReturn(member);

        MemberDto memberDto = new MemberDto();
        memberDto.setAddressOne("qqq");
        memberService.updateAddressOne(memberDto, "georgeng@gmail.com");

        ArgumentCaptor<Member> memberArgumentCaptor = ArgumentCaptor.forClass(Member.class);
        verify(memberRepository).save(memberArgumentCaptor.capture());
        Member capturedMember = memberArgumentCaptor.getValue();

        assertThat(capturedMember.getAddressOne()).isEqualTo(memberDto.getAddressOne());
    }

    @Test
    void canUpdateAddressTwo(){
        Member member = new Member();
        member.setAddressTwo("xxx");
        when(memberRepository.findByEmail("georgeng@gmail.com")).thenReturn(member);

        MemberDto memberDto = new MemberDto();
        memberDto.setAddressTwo("qqq");
        memberService.updateAddressTwo(memberDto, "georgeng@gmail.com");

        ArgumentCaptor<Member> memberArgumentCaptor = ArgumentCaptor.forClass(Member.class);
        verify(memberRepository).save(memberArgumentCaptor.capture());
        Member capturedMember = memberArgumentCaptor.getValue();

        assertThat(capturedMember.getAddressTwo()).isEqualTo(memberDto.getAddressTwo());
    }

    @Test
    void canUpdatePostalCode(){
        Member member = new Member();
        member.setPostalCode("xxx");
        when(memberRepository.findByEmail("georgeng@gmail.com")).thenReturn(member);

        MemberDto memberDto = new MemberDto();
        memberDto.setPostalCode("qqq");
        memberService.updatePostalCode(memberDto, "georgeng@gmail.com");

        ArgumentCaptor<Member> memberArgumentCaptor = ArgumentCaptor.forClass(Member.class);
        verify(memberRepository).save(memberArgumentCaptor.capture());
        Member capturedMember = memberArgumentCaptor.getValue();
        System.out.println(capturedMember.getPostalCode());
        System.out.println(member.getPostalCode());
        System.out.println(memberDto.getPostalCode());
        assertThat(capturedMember.getPostalCode()).isEqualTo(memberDto.getPostalCode());
    }

    @Test
    void canUpdatePassword(){
        Member member = new Member();
        member.setPassword("xxx");
        when(memberRepository.findByEmail("georgeng@gmail.com")).thenReturn(member);
        when(passwordEncoder.encode("TTT")).thenReturn("SSS");

        memberService.updatePassword("TTT", "georgeng@gmail.com");

        ArgumentCaptor<Member> memberArgumentCaptor = ArgumentCaptor.forClass(Member.class);
        verify(memberRepository).save(memberArgumentCaptor.capture());
        Member capturedMember = memberArgumentCaptor.getValue();
        assertThat(capturedMember.getPassword()).isEqualTo(member.getPassword());
    }

    @Test
    void canCheckPassword(){
        Member member = new Member();
        member.setPassword("SSS");
        when(memberRepository.findByEmail("georgeng@gmail.com")).thenReturn(member);
        when(passwordEncoder.matches("TTT", "SSS")).thenReturn(true);

        boolean result = memberService.checkPassword("TTT", "georgeng@gmail.com");
        assertThat(result).isTrue();
    }

    @Test
    void canUpdateAccount(){
        Member member = new Member();
        member.setFirstName("george");
        Collection<Role> listRole = new ArrayList<>();
        Role newRole = new Role();
        newRole.setName("ROLE_MEMBER");
        listRole.add(newRole);
        member.setRoles(listRole);
        when(memberRepository.findById((long)1)).thenReturn(Optional.of(member));

        member.setMember_id((long)1);

        String roles = "ROLE_MEMBER";
        MemberDto memberDto = new MemberDto();
        memberDto.setFirstName("James");
        memberService.updateAccounts(memberDto, (long)1, roles);

        ArgumentCaptor<Member> memberArgumentCaptor = ArgumentCaptor.forClass(Member.class);
        verify(memberRepository).save(memberArgumentCaptor.capture());
        Member capturedMember = memberArgumentCaptor.getValue();

        assertThat(capturedMember.getFirstName()).isEqualTo(memberDto.getFirstName());
    }

    @Test
    void canDeleteMembers(){
        Member member = new Member();
        member.setFirstName("george");
        Collection<Role> listRole = new ArrayList<>();
        Role newRole = new Role();
        newRole.setName("ROLE_MEMBER");
        listRole.add(newRole);
        member.setRoles(listRole);
        member.setMember_id((long)1);
        when(memberRepository.findById((long)1)).thenReturn(Optional.of(member));

        memberService.deleteMembers(member.getMember_id());
        ArgumentCaptor<Member> memberArgumentCaptor = ArgumentCaptor.forClass(Member.class);
        verify(memberRepository).save(memberArgumentCaptor.capture());
        Member capturedMember = memberArgumentCaptor.getValue();

        assertThat(capturedMember.isDeleted()).isEqualTo(true);
    }

    //generate a page of members that fit the search criteria
    @Test
    void canSearchMembers(){
        int pageSize = 5;
        Pageable pageable = PageRequest.of(0, pageSize);
        String query = "george";
        String searchBy = "firstName";
        String statusFilter = "ROLE_MEMBER";
        String sort = "member_id";
        String order = "asc";

        Sort.Direction direction = Sort.Direction.ASC;
        Sort sortable = Sort.by(direction, "member_id");

        when(memberRepository.searchMembersWithStatusAndNotAny(query, statusFilter, searchBy, PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortable)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        memberService.searchMembers(query, pageable, statusFilter, searchBy, sort, order);

        verify(memberRepository).searchMembersWithStatusAndNotAny(query, statusFilter, searchBy, PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortable));
    }

    @Test
    void canFindPaginated(){
        int pageSize = 5;
        int pageNo = 1;

        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        when(memberRepository.findAll(pageable))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        memberService.findPaginated(pageNo, pageSize);

        verify(memberRepository).findAll(pageable);
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

        assertThat(memberDto.getEmail()).isEqualTo(member.getEmail());
        assertThat(memberDto.getFirstName()).isEqualTo(member.getFirstName());
        assertThat(memberDto.getLastName()).isEqualTo(member.getLastName());
        assertThat(memberDto.getPassword()).isNull(); // Password should not be exposed in DTO
        assertThat(memberDto.getRoles().stream().findFirst().get().getName())
                .isEqualTo(member.getRoles().stream().findFirst().get().getName());  // Compare the name of the first role
    }
}