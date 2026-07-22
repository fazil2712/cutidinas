package com.ppip.cutidinas.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "cuti")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cuti {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String badgeid;
    private String jenis;
    private Integer periode;
    private LocalDate tanggalMulai;
    private LocalDate tanggalAkhir;
    private Integer totalHari;
    private Integer sisaHari;
}
