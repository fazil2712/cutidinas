package com.ppip.cutidinas.service;

import com.ppip.cutidinas.model.Cuti;
import com.ppip.cutidinas.model.User;
import com.ppip.cutidinas.repository.CutiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CutiService {

    private final CutiRepository cutiRepository;

    public void generateCutiIfNeeded(User user) {
        if (user.getTahunMasuk() == null) return;

        LocalDate nowWib = LocalDate.now(ZoneId.of("Asia/Jakarta"));
        int currentYear = nowWib.getYear();
        
        int yearsOfService = currentYear - user.getTahunMasuk().getYear();
        
        // 0 cuti balance during the first year
        if (yearsOfService <= 0) return;
        
        // Check if cuti for this year already exists
        Optional<Cuti> existingCuti = cutiRepository.findByBadgeidAndPeriode(user.getBadgeid(), currentYear);
        if (existingCuti.isPresent()) return;

        boolean isCutiBesar = (yearsOfService % 3 == 0);
        
        Cuti newCuti = new Cuti();
        newCuti.setBadgeid(user.getBadgeid());
        newCuti.setJenis(isCutiBesar ? "Cuti Besar" : "Cuti Kecil");
        newCuti.setPeriode(currentYear);
        newCuti.setTotalHari(isCutiBesar ? 15 : 12);
        newCuti.setSisaHari(newCuti.getTotalHari());
        
        newCuti.setTanggalMulai(LocalDate.of(currentYear, user.getTahunMasuk().getMonth(), user.getTahunMasuk().getDayOfMonth()));
        newCuti.setTanggalAkhir(newCuti.getTanggalMulai().plusYears(1).minusDays(1));
        
        cutiRepository.save(newCuti);
    }
}
