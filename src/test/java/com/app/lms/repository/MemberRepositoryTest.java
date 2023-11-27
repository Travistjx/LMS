package com.app.lms.repository;

import com.app.lms.entity.BookStatus;
import com.app.lms.entity.Member;
import com.app.lms.entity.Role;
import com.app.lms.web.BookDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Arrays;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

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
        memberRepository.save(member);

        //when
        boolean exists = false;
        Member newMember = memberRepository.findByEmail("georgeng@gmail.com");
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
        Member newMember = memberRepository.findByEmail(email);
        if (newMember == null) exists = false;

        //then
        assertThat(exists).isFalse();
    }

    @Test
    void itShouldCheckIfMemberExistsIfSearchMembersWithStatusAndByAny(){
        Member member = new Member();
        member.setEmail("georgeng@gmail.com");

        member.setFirstName("george");
        member.setLastName("ng");
        member.setPassword("123");

        Role role = new Role();
        role.setName("ROLE_MEMBER");
        member.setRoles(Arrays.asList(role));
        memberRepository.save(member);

        int pageSize = 5;

        Pageable pageable = PageRequest.of(0, pageSize);
        String query = "george";
        String statusFilter = null;

        Sort.Direction direction = Sort.Direction.ASC;
        Sort sortable = Sort.by(direction, "member_id");

        Page<Member> memberList = memberRepository.searchMembersWithStatusAndByAny(query, statusFilter,
                PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortable));

        boolean exists = false;
        if (memberList.hasContent()) exists = true;

        assertThat(exists).isTrue();
    }

    @Test
    void itShouldCheckIfMemberDoesNotExistsIfSearchMembersWithStatusAndByAny(){
        int pageSize = 5;

        Pageable pageable = PageRequest.of(0, pageSize);
        String query = "george";
        String statusFilter = null;

        Sort.Direction direction = Sort.Direction.ASC;
        Sort sortable = Sort.by(direction, "member_id");

        Page<Member> memberList = memberRepository.searchMembersWithStatusAndByAny(query, statusFilter,
                PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortable));

        boolean exists = true;
        if (!memberList.hasContent()) exists = false;

        assertThat(exists).isFalse();
    }

    @Test
    void itShouldCheckIfMemberExistsIfSearchForBooksWithStatusAndNotAny(){
        Member member = new Member();
        member.setEmail("georgeng@gmail.com");

        member.setFirstName("george");
        member.setLastName("ng");
        member.setPassword("123");

        Role role = new Role();
        role.setName("ROLE_MEMBER");
        member.setRoles(Arrays.asList(role));
        memberRepository.save(member);

        int pageSize = 5;

        Pageable pageable = PageRequest.of(0, pageSize);
        String query = "george";
        String searchBy = "firstName";
        String statusFilter = null;

        Sort.Direction direction = Sort.Direction.ASC;
        Sort sortable = Sort.by(direction, "member_id");

        Page<Member> memberList = memberRepository.searchMembersWithStatusAndNotAny(query, statusFilter, searchBy,
                PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortable));

        boolean exists = false;
        if (memberList.hasContent()) exists = true;

        assertThat(exists).isTrue();
    }

    @Test
    void itShouldCheckIfMemberDoesNotExistsIfSearchForBooksWithStatusAndNotAny(){
        int pageSize = 5;

        Pageable pageable = PageRequest.of(0, pageSize);
        String query = "george";
        String searchBy = "name";
        String statusFilter = null;

        Sort.Direction direction = Sort.Direction.ASC;
        Sort sortable = Sort.by(direction, "member_id");

        Page<Member> memberList = memberRepository.searchMembersWithStatusAndNotAny(query, searchBy, statusFilter,
                PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortable));

        boolean exists = true;
        if (!memberList.hasContent()) exists = false;

        assertThat(exists).isFalse();
    }
}