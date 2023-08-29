package com.app.lms.service;

import com.app.lms.entity.Member;
import com.app.lms.web.MemberDto;

import java.util.List;
import java.util.Optional;

public interface MemberService {
    void saveUser(MemberDto registration);
    Member findByEmail(String email);

    List<MemberDto> findAllUsers();

    MemberDto convertEntityToDto(Member member);

    Optional<MemberDto> findById(Long id);

    void updateAccounts(MemberDto member, Long memberId, String roles);
}
