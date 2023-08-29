package com.app.lms.service.serviceimpl;

import com.app.lms.entity.Author;
import com.app.lms.entity.Book;
import com.app.lms.entity.Member;
import com.app.lms.entity.Role;
import com.app.lms.repository.RoleRepository;
import com.app.lms.repository.MemberRepository;
import com.app.lms.service.MemberService;
import com.app.lms.web.AuthorDto;
import com.app.lms.web.BookDto;
import com.app.lms.web.MemberDto;
import com.app.lms.web.RoleDto;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
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
            role = new Role();
            role.setName("ROLE_MEMBER");
            roleRepository.save(role);
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

    @Override
    public Optional<MemberDto> findById(Long id){
        Optional<Member>member = memberRepository.findById(id);
        return member.map(this::convertEntityToDto);
    }

    @Override
    public void updateAccounts(MemberDto member, Long memberId, String roles) {
        Optional<Member> selectedMember= memberRepository.findById(memberId);

        if (selectedMember.isPresent()){
            Member existingMember = selectedMember.get();
            Collection<Role> listRole = new ArrayList<>();
            String[] roleNames = roles.split(", ");
            for (String roleName : roleNames) {
                Role oldRole = roleRepository.findByName(roleName);
                if (oldRole == null){
                    Role newRole = new Role();
                    newRole.setName(roleName);
                    listRole.add(newRole);
                }
                else{
                    listRole.add(oldRole);
                }

            }


            existingMember.setRoles(listRole);
            existingMember.setFirstName(member.getFirstName());
            existingMember.setLastName(member.getLastName());
            existingMember.setEmail(member.getEmail());

            memberRepository.save(existingMember);
        }

    }

    @Override
    public MemberDto convertEntityToDto(Member member){
        MemberDto memberDto = new MemberDto();
        memberDto.setFirstName(member.getFirstName());
        memberDto.setLastName(member.getLastName());
        memberDto.setEmail(member.getEmail());
        memberDto.setMember_id(member.getId());

        Collection<RoleDto> rolesDto = new ArrayList<>();

        for (Role role : member.getRoles()) {
            RoleDto roleDto = new RoleDto();
            roleDto.setName(role.getName());
            // Set other properties of AuthorDto as needed
            rolesDto.add(roleDto);
        }
        memberDto.setRoles(rolesDto);

        return memberDto;
    }
}