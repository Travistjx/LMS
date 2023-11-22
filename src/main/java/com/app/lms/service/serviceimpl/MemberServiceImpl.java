package com.app.lms.service.serviceimpl;

import com.app.lms.entity.*;
import com.app.lms.repository.RoleRepository;
import com.app.lms.repository.MemberRepository;
import com.app.lms.service.MemberService;
import com.app.lms.web.AuthorDto;
import com.app.lms.web.BookDto;
import com.app.lms.web.MemberDto;
import com.app.lms.web.RoleDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
        member.setGender(memberDto.getGender());
        member.setAddressOne(memberDto.getAddressOne());
        member.setAddressTwo(memberDto.getAddressTwo());
        member.setBirthday(memberDto.getBirthday());
        member.setPostalCode(memberDto.getPostalCode());
        member.setLibraryCard(memberDto.getLibraryCard());

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
    public MemberDto findByEmail(String email) {
        Member member = memberRepository.findByEmail(email);
        if (member == null){
            return null;
        }
        MemberDto memberDto = convertEntityToDto(member);
        return memberDto;
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
    public void updateName(MemberDto memberDto, String email){
        Member member = memberRepository.findByEmail(email);
        member.setFirstName(memberDto.getFirstName());
        member.setLastName(memberDto.getLastName());
        memberRepository.save(member);
    }

    @Override
    public void updateEmail(MemberDto memberDto, String email){
        Member member = memberRepository.findByEmail(email);
        member.setEmail(memberDto.getEmail());
        memberRepository.save(member);
    }

    @Override
    public void updateGender(MemberDto memberDto, String email){
        Member member = memberRepository.findByEmail(email);
        member.setGender(memberDto.getGender());
        memberRepository.save(member);
    }

    @Override
    public void updateAddressOne(MemberDto memberDto, String email){
        Member member = memberRepository.findByEmail(email);
        member.setAddressOne(memberDto.getAddressOne());
        memberRepository.save(member);
    }

    @Override
    public void updateAddressTwo(MemberDto memberDto, String email){
        Member member = memberRepository.findByEmail(email);
        member.setAddressTwo(memberDto.getAddressTwo());
        memberRepository.save(member);
    }

    @Override
    public void updatePostalCode(MemberDto memberDto, String email){
        Member member = memberRepository.findByEmail(email);
        member.setPostalCode(memberDto.getPostalCode());
        memberRepository.save(member);
    }

    @Override
    public void updatePassword(String newPassword, String email){
        Member member = memberRepository.findByEmail(email);
        member.setPassword(passwordEncoder.encode(newPassword));
        memberRepository.save(member);
    }

    @Override
    public boolean checkPassword(String password, String email){
        Member member = memberRepository.findByEmail(email);
        String hashedPassword = member.getPassword();
        return passwordEncoder.matches(password, hashedPassword);
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
            existingMember.setLibraryCard(member.getLibraryCard());
            existingMember.setGender(member.getGender());
            existingMember.setBirthday(member.getBirthday());
            existingMember.setAddressOne(member.getAddressOne());
            existingMember.setAddressOne(member.getAddressTwo());
            existingMember.setPostalCode(member.getPostalCode());

            memberRepository.save(existingMember);
        }

    }

    public void deleteMembers (Long id){
        Member memberToDelete = memberRepository.findById(id).orElse(null);

        if (memberToDelete != null) {
            // Remove the roles from the member
            memberToDelete.setRoles(Collections.emptyList());

            // Delete the member
            memberRepository.delete(memberToDelete);
        }
    }

    @Override
    public Page<MemberDto> searchMembers(String query, Pageable pageable, String statusFilter, String searchBy) {
        Page<Member> memberPage = null;
        String status = "";
        if (statusFilter.equals("ALL")){
            status = null;
        }
        else {
            status = statusFilter;
        }

        if (searchBy.equals("any")){
            memberPage = memberRepository.searchMembersWithStatusAndByAny(query, status, pageable);
        }
        else {
            memberPage = memberRepository.searchMembersWithStatusAndNotAny(query, status, searchBy, pageable);
        }
        List<MemberDto> memberDtoList = memberPage.getContent().stream()
                .map(this::convertEntityToDto)
                .collect(Collectors.toList());

        return new PageImpl<>(memberDtoList, pageable, memberPage.getTotalElements());
    }

    @Override
    public Page<MemberDto> findPaginated(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        Page<Member> memberPage = this.memberRepository.findAll(pageable);

        List<MemberDto> memberDtoList = memberPage.getContent().stream()
                .map(this::convertEntityToDto)
                .collect(Collectors.toList());

        return new PageImpl<>(memberDtoList, pageable, memberPage.getTotalElements());
    }

    @Override
    public MemberDto convertEntityToDto(Member member){
        MemberDto memberDto = new MemberDto();
        memberDto.setFirstName(member.getFirstName());
        memberDto.setLastName(member.getLastName());
        memberDto.setEmail(member.getEmail());
        memberDto.setMember_id(member.getMember_id());
        memberDto.setGender(member.getGender());
        memberDto.setBirthday(member.getBirthday());
        memberDto.setLibraryCard(member.getLibraryCard());
        memberDto.setAddressOne(member.getAddressOne());
        memberDto.setAddressTwo(member.getAddressTwo());
        memberDto.setPostalCode(member.getPostalCode());


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