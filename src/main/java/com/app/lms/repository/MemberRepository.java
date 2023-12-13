package com.app.lms.repository;

import com.app.lms.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Member findByEmail(String email);

    @Query("SELECT l FROM Member l WHERE l.deleted IS NULL OR l.deleted = false")
    List<Member> findAll();

    @Query("SELECT l FROM Member l WHERE l.deleted IS NULL OR l.deleted = false")
    Page<Member> findAll(Pageable pageable);

    @Query("SELECT l FROM Member l " +
            "LEFT JOIN l.roles r " +
            "WHERE l.deleted = false AND " +
            "(l.member_id LIKE %:query% OR " +
            "l.firstName LIKE %:query% OR " +
            "l.lastName LIKE %:query% OR " +
            "l.email LIKE %:query% OR " +
            "r.name LIKE %:query%) AND " +
            "(:statusFilter IS NULL OR r.name = :statusFilter)")
    Page<Member> searchMembersWithStatusAndByAny(@Param("query") String query,
                                                 @Param("statusFilter") String statusFilter,
                                                 Pageable pageable);


    @Query("SELECT l FROM Member l " +
            "LEFT JOIN l.roles r " +
            "WHERE l.deleted = false AND " +
            "((:searchBy = 'memberId' AND l.member_id LIKE %:query%) OR " +
            "(:searchBy = 'firstName' AND l.firstName LIKE %:query%) OR " +
            "(:searchBy = 'lastName' AND l.lastName LIKE %:query%) OR " +
            "(:searchBy = 'email' AND l.email LIKE %:query%)) AND " +
            "(:statusFilter IS NULL OR r.name = :statusFilter)")
    Page<Member> searchMembersWithStatusAndNotAny(@Param("query") String query,
                                                  @Param("statusFilter") String statusFilter,
                                                  @Param("searchBy") String searchBy,
                                                  Pageable pageable);


}
