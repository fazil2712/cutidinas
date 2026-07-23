package com.ppip.cutidinas.repository;

import com.ppip.cutidinas.model.Cuti;
import com.ppip.cutidinas.model.PengajuanCuti;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PengajuanCutiRepository extends JpaRepository<PengajuanCuti, Long> {

    java.util.List<PengajuanCuti> findByCutiBadgeidOrderByTanggalMulaiDesc(String badgeid);
    java.util.List<PengajuanCuti> findAllByOrderByTanggalMulaiDesc();
    
    java.util.List<PengajuanCuti> findByStatusOrderByTanggalMulaiDesc(String status);
    java.util.List<PengajuanCuti> findByApproverBadgeidOrderByTanggalMulaiDesc(String approverBadgeid);
}
