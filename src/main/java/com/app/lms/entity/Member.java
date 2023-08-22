    package com.app.lms.entity;

    import jakarta.persistence.*;

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

        public Member(String firstName, String lastName, String email, String password, Collection<Role> roles) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.password = password;
            this.roles = roles;
        }

        public Long getId() {
            return member_id;
        }

        public void setId(Long id) {
            this.member_id = id;
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


    }
