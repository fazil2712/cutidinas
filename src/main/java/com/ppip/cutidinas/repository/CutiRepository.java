package com.ppip.cutidinas.repository;

import com.ppip.cutidinas.model.Cuti;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CutiRepository extends JpaRepository<Cuti, Long> {
}
