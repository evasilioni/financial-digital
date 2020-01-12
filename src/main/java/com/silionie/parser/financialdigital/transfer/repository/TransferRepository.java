package com.silionie.parser.financialdigital.transfer.repository;

import com.silionie.parser.financialdigital.transfer.entities.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, Long> {

    List<Transfer> findBySource(Long source);
    List<Transfer> findByDestination(Long destination);
    List<Transfer> findByTransferDateBetween(LocalDateTime startDate, LocalDateTime endDate);

}
