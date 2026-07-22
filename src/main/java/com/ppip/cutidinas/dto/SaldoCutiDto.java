package com.ppip.cutidinas.dto;

import com.ppip.cutidinas.model.Cuti;
import lombok.Getter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Getter
public class SaldoCutiDto {

    private final String jenisCuti;
    private final String periode;
    private final int totalKuota;
    private final int kuotaTerpakai;
    private final int kuotaPending;
    private final int sisaSaldo;
    private final String validMulai;
    private final String validHingga;

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("d MMMM yyyy", new Locale("id", "ID"));

    public SaldoCutiDto(Cuti cuti, int kuotaPending) {
        this.jenisCuti    = cuti.getJenis();
        this.periode      = String.valueOf(cuti.getPeriode());
        this.totalKuota   = cuti.getTotalHari() != null ? cuti.getTotalHari() : 0;
        this.kuotaPending = kuotaPending;
        this.sisaSaldo    = cuti.getSisaHari() != null ? cuti.getSisaHari() : 0;
        this.kuotaTerpakai = this.totalKuota - this.sisaSaldo - kuotaPending;
        this.validMulai   = format(cuti.getTanggalMulai());
        this.validHingga  = format(cuti.getTanggalAkhir());
    }

    private String format(LocalDate date) {
        return date != null ? date.format(FMT) : "-";
    }
}
