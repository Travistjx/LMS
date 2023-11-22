package com.app.lms.service;

import com.app.lms.entity.Member;
import com.app.lms.web.MemberDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface MemberService {
    void saveUser(MemberDto registration);
    MemberDto findByEmail(String email);

    List<MemberDto> findAllUsers();

    MemberDto convertEntityToDto(Member member);

    Optional<MemberDto> findById(Long id);

    void updateAccounts(MemberDto member, Long memberId, String roles);

    Page<MemberDto> findPaginated(int pageNo, int pageSize);

    void deleteMembers(Long id);

    Page<MemberDto> searchMembers(String query, Pageable pageable, String statusFilter, String searchBy);

    void updateName(MemberDto memberDto, String email);
    void updateEmail(MemberDto memberDto, String email);

    void updateGender(MemberDto memberDto, String email);

    void updateAddressOne(MemberDto memberDto, String email);
    void updateAddressTwo(MemberDto memberDto, String email);
    void updatePostalCode(MemberDto memberDto, String email);

    boolean checkPassword(String password, String email);
    void updatePassword(String newPassword, String email);
}
