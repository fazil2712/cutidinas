package com.ppip.cutidinas.repository;

import com.ppip.cutidinas.model.Cuti;
import com.ppip.cutidinas.model.PengajuanCuti;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PengajuanCutiRepository extends JpaRepository<PengajuanCuti, Long> {

    @Query("SELECT COALESCE(SUM(p.jumlahHariKerja), 0) FROM PengajuanCuti p WHERE p.cuti = :cuti AND p.status = 'MENUNGGU_PERSETUJUAN'")
    int sumPendingHariByCuti(@Param("cuti") Cuti cuti);
}
