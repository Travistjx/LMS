package com.app.lms.service;

import com.app.lms.entity.Fine;
import com.app.lms.entity.IdType;
import com.app.lms.web.FineDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface FineService {
    void calculateFinesForOverdueLoans();

    Page<FineDto> searchFines(String query, Pageable pageable, Optional<Long> id, IdType idType,
                              String statusFilter, String searchBy, String sort, String order);
    Page<FineDto> findPaginated(int pageNo, int pageSize, Optional<Long> id, IdType idType);

    Optional<FineDto> findById(Long id);

    void updateFines(FineDto fine, Long fineId);
    void deleteFines(Long id);
    Double calculateTotalFines(Long id);

    List<FineDto> findFinesByMemberId (Long memberId);

    FineDto convertEntityToDto(Fine fine);
}
