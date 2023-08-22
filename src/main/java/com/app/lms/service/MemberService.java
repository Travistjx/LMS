package com.app.lms.service;

import com.app.lms.entity.Member;
import com.app.lms.web.MemberDto;

import java.util.List;

public interface MemberService {
    void saveUser(MemberDto registration);
    Member findByEmail(String email);

    List<MemberDto> findAllUsers();
}
