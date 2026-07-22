package com.ppip.cutidinas.repository;

import com.ppip.cutidinas.model.Cuti;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CutiRepository extends JpaRepository<Cuti, Long> {
    List<Cuti> findByBadgeid(String badgeid);
    List<Cuti> findByBadgeidOrderByPeriodeDesc(String badgeid);
    Optional<Cuti> findByBadgeidAndPeriode(String badgeid, Integer periode);
}
