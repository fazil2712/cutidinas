package com.ppip.cutidinas.repository;

import com.ppip.cutidinas.model.PengajuanCuti;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PengajuanCutiRepository extends JpaRepository<PengajuanCuti, Long> {
}
