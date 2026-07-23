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
    
    @ManyToOne
    @JoinColumn(name = "badgeid", insertable = false, updatable = false)
    private User user;
    private String jenis;
    private Integer periode;
    private LocalDate tanggalMulai;
    private LocalDate tanggalAkhir;
    private Integer totalHari = 0;
    private Integer sisaHari = 0;
    private Integer kuotaPending = 0;
}
