package com.ppip.cutidinas.controller;

import com.ppip.cutidinas.dto.SaldoCutiDto;
import com.ppip.cutidinas.model.Cuti;
import com.ppip.cutidinas.repository.CutiRepository;
import com.ppip.cutidinas.repository.PengajuanCutiRepository;
import com.ppip.cutidinas.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import jakarta.servlet.http.HttpSession;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/cuti")
public class CutiController {

    private final CutiRepository cutiRepository;
    private final PengajuanCutiRepository pengajuanCutiRepository;

    public CutiController(CutiRepository cutiRepository,
                          PengajuanCutiRepository pengajuanCutiRepository) {
        this.cutiRepository = cutiRepository;
        this.pengajuanCutiRepository = pengajuanCutiRepository;
    }

    @GetMapping
    public String dashboard(Model model,
                            @AuthenticationPrincipal CustomUserDetails userDetails, HttpSession session) {

        String badgeid = userDetails.getBadgeId();
        List<Cuti> cutiList = cutiRepository.findByBadgeid(badgeid);

        List<SaldoCutiDto> saldoList = cutiList.stream()
                .map(c -> {
                    int pending = pengajuanCutiRepository.sumPendingHariByCuti(c);
                    return new SaldoCutiDto(c, pending);
                })
                .toList();

        model.addAttribute("saldoList", saldoList);
        model.addAttribute("tahun", LocalDate.now().getYear());
        model.addAttribute("namaUser", userDetails.getFullName());
        session.setAttribute("currentModule", "cuti");

        return "cuti/dashboard";
    }
}
