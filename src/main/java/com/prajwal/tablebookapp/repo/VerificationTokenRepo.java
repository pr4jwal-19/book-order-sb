package com.prajwal.tablebookapp.repo;

import com.prajwal.tablebookapp.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerificationTokenRepo extends JpaRepository<VerificationToken, Long> {

    Optional<VerificationToken> findByToken(String token);

}
