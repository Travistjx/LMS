package com.app.lms.service.serviceimpl;

import com.app.lms.entity.Member;
import com.app.lms.entity.Role;
import com.app.lms.repository.RoleRepository;
import com.app.lms.repository.MemberRepository;
import com.app.lms.service.MemberService;
import com.app.lms.web.MemberDto;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberServiceImpl(MemberRepository memberRepository,
                             RoleRepository roleRepository,
                             PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void saveUser(MemberDto memberDto) {
        Member member = new Member();
        member.setFirstName(memberDto.getFirstName());
        member.setLastName(memberDto.getLastName());
        member.setEmail(memberDto.getEmail());

        //encrypt the password once we integrate spring security
        //user.setPassword(userDto.getPassword());
        member.setPassword(passwordEncoder.encode(memberDto.getPassword()));
        Role role = roleRepository.findByName("ROLE_MEMBER");
        if(role == null){
            role = checkRoleExist();
        }
        member.setRoles(Arrays.asList(role));
        memberRepository.save(member);
    }

    @Override
    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    @Override
    public List<MemberDto> findAllUsers() {
        List<Member> members = memberRepository.findAll();
        return members.stream().map((user) -> convertEntityToDto(user))
                .collect(Collectors.toList());
    }

    private MemberDto convertEntityToDto(Member member){
        MemberDto memberDto = new MemberDto();
        memberDto.setFirstName(member.getFirstName());
        memberDto.setLastName(member.getLastName());
        memberDto.setEmail(member.getEmail());
        return memberDto;
    }

    private Role checkRoleExist() {
        Role role = new Role();
        role.setName("ROLE_MEMBER");
        return roleRepository.save(role);
    }
}