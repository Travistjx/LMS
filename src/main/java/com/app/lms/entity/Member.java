    package com.app.lms.entity;

    import com.app.lms.web.RoleDto;
    import jakarta.persistence.*;

    import java.time.LocalDate;
    import java.util.Collection;
    import java.util.Iterator;

    @Entity
    @Table(name= "member", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
    public class Member {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long member_id;

        @Column(name="first_name")
        private String firstName;
        @Column(name="last_name")
        private String lastName;
        @Column(name="email")
        private String email;
        @Column(name="password")
        private String password;

        @Column(name="birthday")
        private LocalDate birthday;

        @Enumerated(EnumType.STRING)
        private Gender gender;

        private String addressOne;

        private String addressTwo;

        private String postalCode;

        private String libraryCard;

        @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
        @JoinTable(
                name = "members_roles",
                joinColumns = @JoinColumn(
                        name = "member_id", referencedColumnName = "member_id"),
                inverseJoinColumns = @JoinColumn(
                        name = "role_id", referencedColumnName = "role_id"))
        private Collection<Role> roles;

        public Member() {

        }



        public Collection<Role> getRoles() {
            return roles;
        }

        public void setRoles(Collection<Role> roles) {
            this.roles = roles;
        }

        public boolean hasRole(String roleName) {
            Iterator<Role> iterator = this.roles.iterator();
            while (iterator.hasNext()) {
                Role role = iterator.next();
                if (role.getName().equals(roleName)) {
                    return true;
                }
            }

            return false;
        }

        public Member(Long member_id, String firstName, String lastName, String email, String password, Collection<Role> roles, LocalDate birthday, Gender gender, String addressOne, String addressTwo, String postalCode, String libraryCard) {
            this.member_id = member_id;
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.password = password;
            this.roles = roles;
            this.birthday = birthday;
            this.gender = gender;
            this.addressOne = addressOne;
            this.addressTwo = addressTwo;
            this.postalCode = postalCode;
            this.libraryCard = libraryCard;
        }

        public Long getMember_id() {
            return member_id;
        }

        public void setMember_id(Long member_id) {
            this.member_id = member_id;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public LocalDate getBirthday() {
            return birthday;
        }

        public void setBirthday(LocalDate birthday) {
            this.birthday = birthday;
        }

        public Gender getGender() {
            return gender;
        }

        public void setGender(Gender gender) {
            this.gender = gender;
        }

        public String getAddressOne() {
            return addressOne;
        }

        public void setAddressOne(String addressOne) {
            this.addressOne = addressOne;
        }

        public String getAddressTwo() {
            return addressTwo;
        }

        public void setAddressTwo(String addressTwo) {
            this.addressTwo = addressTwo;
        }

        public String getPostalCode() {
            return postalCode;
        }

        public void setPostalCode(String postalCode) {
            this.postalCode = postalCode;
        }

        public String getLibraryCard() {
            return libraryCard;
        }

        public void setLibraryCard(String libraryCard) {
            this.libraryCard = libraryCard;
        }
    }
