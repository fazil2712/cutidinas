package com.ppip.cutidinas.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "pengajuan_cuti")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PengajuanCuti {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "cuti_id")
    private Cuti cuti;
    
    private String tipeCuti;
    private LocalDate tanggalMulai;
    private LocalDate tanggalAkhir;
    private Integer jumlahHariKalender;
    private Integer jumlahHariKerja;
    private String status;
    private String pendelegasianPekerjaan;
    private String alasanAbsen;
    private String approverBadgeid;
    private String alasanPenolakan;
}
