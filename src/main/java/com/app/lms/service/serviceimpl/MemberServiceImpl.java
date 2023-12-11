package com.app.lms.service.serviceimpl;

import com.app.lms.entity.*;
import com.app.lms.repository.RoleRepository;
import com.app.lms.repository.MemberRepository;
import com.app.lms.service.MemberService;
import com.app.lms.web.AuthorDto;
import com.app.lms.web.BookDto;
import com.app.lms.web.MemberDto;
import com.app.lms.web.RoleDto;
import org.springframework.data.domain.*;
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

    // Save/Create account
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
        member.setDeleted(false);

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

    // Find Member by Email
    @Override
    public MemberDto findByEmail(String email) {
        Member member = memberRepository.findByEmail(email);
        if (member == null){
            return null;
        }
        MemberDto memberDto = convertEntityToDto(member);
        return memberDto;
    }


    // FInd all list of users/accounts
    @Override
    public List<MemberDto> findAllUsers() {
        List<Member> members = memberRepository.findAll();
        return members.stream().map((user) -> convertEntityToDto(user))
                .collect(Collectors.toList());
    }

    // Find member by Member Id
    @Override
    public Optional<MemberDto> findById(Long id){
        Optional<Member>member = memberRepository.findById(id);
        return member.map(this::convertEntityToDto);
    }

    // Update name of existing user/account
    @Override
    public void updateName(MemberDto memberDto, String email){
        Member member = memberRepository.findByEmail(email);

        // Check if member is found before attempting to update
        if (member != null) {
            member.setFirstName(memberDto.getFirstName());
            member.setLastName(memberDto.getLastName());
            memberRepository.save(member);
        } else {
            throw new NullPointerException("Member not found for email: " + email);
        }
    }

    // Update email of existing user/account
    @Override
    public void updateEmail(MemberDto memberDto, String email){
        Member member = memberRepository.findByEmail(email);
        member.setEmail(memberDto.getEmail());
        memberRepository.save(member);
    }

    // Update gender of existing user/account
    @Override
    public void updateGender(MemberDto memberDto, String email){
        Member member = memberRepository.findByEmail(email);
        member.setGender(memberDto.getGender());
        memberRepository.save(member);
    }

    // Update address 1 of existing user/account
    @Override
    public void updateAddressOne(MemberDto memberDto, String email){
        Member member = memberRepository.findByEmail(email);
        member.setAddressOne(memberDto.getAddressOne());
        memberRepository.save(member);
    }

    // Update address 2 of existing user/account
    @Override
    public void updateAddressTwo(MemberDto memberDto, String email){
        Member member = memberRepository.findByEmail(email);
        member.setAddressTwo(memberDto.getAddressTwo());
        memberRepository.save(member);
    }

    // Update postal code of existing user/account
    @Override
    public void updatePostalCode(MemberDto memberDto, String email){
        Member member = memberRepository.findByEmail(email);
        member.setPostalCode(memberDto.getPostalCode());
        memberRepository.save(member);
    }

    // Update password of existing user/account
    @Override
    public void updatePassword(String newPassword, String email){
        Member member = memberRepository.findByEmail(email);
        member.setPassword(passwordEncoder.encode(newPassword));
        memberRepository.save(member);
    }

    // Check if password entered matches database
    @Override
    public boolean checkPassword(String password, String email){
        Member member = memberRepository.findByEmail(email);
        String hashedPassword = member.getPassword();
        return passwordEncoder.matches(password, hashedPassword);
    }

    // Update existing account
    @Override
    public void updateAccounts(MemberDto member, Long memberId, String roles) {
        Optional<Member> selectedMember= memberRepository.findById(memberId);

        // if exists, update account with the changes
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

            if (member.getPassword() != null && !member.getPassword().isEmpty()) {
                existingMember.setPassword(passwordEncoder.encode(member.getPassword()));
            }
            existingMember.setRoles(listRole);
            existingMember.setFirstName(member.getFirstName());
            existingMember.setLastName(member.getLastName());
            existingMember.setEmail(member.getEmail());
            existingMember.setGender(member.getGender());
            existingMember.setBirthday(member.getBirthday());
            existingMember.setAddressOne(member.getAddressTwo());
            existingMember.setAddressOne(member.getAddressOne());
            existingMember.setPostalCode(member.getPostalCode());

            memberRepository.save(existingMember);
        }

    }

    // Delete member/account based on Member Id
    public void deleteMembers (Long id){
        Member memberToDelete = memberRepository.findById(id).orElse(null);

        if (memberToDelete != null) {
            memberToDelete.setDeleted(true);
            memberRepository.save( memberToDelete);
        }
    }

    // Retrieves pages for members based on search filter
    @Override
    public Page<MemberDto> searchMembers(String query, Pageable pageable, String statusFilter, String searchBy,
                                         String sort, String order) {
        Page<Member> memberPage = null;

        // set status
        String status = "";
        if (statusFilter.equals("ALL")){
            status = null;
        }
        else {
            status = statusFilter;
        }

        //set sort order
        Sort.Direction direction = Sort.Direction.ASC;

        if (order.equals("desc")){
            direction = Sort.Direction.DESC;
        }

        // set sort option
        Sort sortable = Sort.by(direction, "member_id"); // Default sorting by title

        if (sort != null) {
            switch (sort) {
                case "firstName":
                    sortable = Sort.by(direction, "firstName");
                    break;
                case "lastName":
                    sortable = Sort.by(direction, "lastName");
                    break;
                case "email":
                    sortable = Sort.by(direction, "email");
                    break;
            }
        }

        if (searchBy.equals("any")){
            memberPage = memberRepository.searchMembersWithStatusAndByAny(query, status, PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortable));
        }
        else { // if searchBy != "any"
            memberPage = memberRepository.searchMembersWithStatusAndNotAny(query, status, searchBy, PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortable));
        }
        List<MemberDto> memberDtoList = memberPage.getContent().stream()
                .map(this::convertEntityToDto)
                .collect(Collectors.toList());

        return new PageImpl<>(memberDtoList, pageable, memberPage.getTotalElements());
    }

    // Retrieve pages of all members
    @Override
    public Page<MemberDto> findPaginated(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        Page<Member> memberPage = this.memberRepository.findAll(pageable);

        List<MemberDto> memberDtoList = memberPage.getContent().stream()
                .map(this::convertEntityToDto)
                .collect(Collectors.toList());

        return new PageImpl<>(memberDtoList, pageable, memberPage.getTotalElements());
    }

    // Convert Member entity to MemberDto
    @Override
    public MemberDto convertEntityToDto(Member member){
        MemberDto memberDto = new MemberDto();
        memberDto.setFirstName(member.getFirstName());
        memberDto.setLastName(member.getLastName());
        memberDto.setEmail(member.getEmail());
        memberDto.setMember_id(member.getMember_id());
        memberDto.setGender(member.getGender());
        memberDto.setBirthday(member.getBirthday());
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