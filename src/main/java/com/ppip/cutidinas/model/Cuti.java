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
@Table(name = "cuti")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cuti {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String jenis;
    private Integer sisaHari;
    private Integer totalHari;
    private String periode;
    private LocalDate tanggalMulai;
    private LocalDate tanggalAkhir;
    
    @ManyToOne
    @JoinColumn(name = "badgeid")
    private User user;
}
