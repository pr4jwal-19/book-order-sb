package com.prajwal.tablebookapp.repo;

import com.prajwal.tablebookapp.model.CafeTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CafeTableRepo extends JpaRepository<CafeTable, Long> {
}
