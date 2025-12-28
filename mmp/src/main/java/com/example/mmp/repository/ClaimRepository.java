package com.example.mmp.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.mmp.model.Claim;

public interface ClaimRepository extends JpaRepository<Claim, Long> {

    Optional<Claim> findByFeeId(Long feeId);
}
