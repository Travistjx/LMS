    package com.app.lms.entity;

    import com.app.lms.web.RoleDto;
    import jakarta.persistence.*;
    import lombok.AllArgsConstructor;
    import lombok.Getter;
    import lombok.NoArgsConstructor;
    import lombok.Setter;

    import java.time.LocalDate;
    import java.util.Collection;
    import java.util.Iterator;
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
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

        private boolean deleted;

        @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
        @JoinTable(
                name = "members_roles",
                joinColumns = @JoinColumn(
                        name = "member_id", referencedColumnName = "member_id"),
                inverseJoinColumns = @JoinColumn(
                        name = "role_id", referencedColumnName = "role_id"))
        private Collection<Role> roles;

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
    }
