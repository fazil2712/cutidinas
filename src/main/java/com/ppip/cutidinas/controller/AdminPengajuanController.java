package com.ppip.cutidinas.controller;

import com.ppip.cutidinas.model.Cuti;
import com.ppip.cutidinas.model.Holiday;
import com.ppip.cutidinas.model.PengajuanCuti;
import com.ppip.cutidinas.repository.CutiRepository;
import com.ppip.cutidinas.repository.HolidayRepository;
import com.ppip.cutidinas.repository.PengajuanCutiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/admin/pengajuan")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN') or hasAuthority('PERM_KELOLA_PENGAJUAN')")
public class AdminPengajuanController {

    private final PengajuanCutiRepository pengajuanCutiRepository;
    private final CutiRepository cutiRepository;
    private final HolidayRepository holidayRepository;

    @GetMapping
    public String listPengajuan(Model model) {
        List<PengajuanCuti> pengajuanList = pengajuanCutiRepository.findAllByOrderByTanggalMulaiDesc();
        model.addAttribute("pengajuanList", pengajuanList);
        return "admin/pengajuan";
    }

    @PostMapping("/edit")
    public String editPengajuan(@RequestParam("id") Long id,
                                @RequestParam("tanggalMulai") LocalDate tanggalMulai,
                                @RequestParam("tanggalAkhir") LocalDate tanggalAkhir,
                                @RequestParam("pendelegasianPekerjaan") String pendelegasianPekerjaan,
                                @RequestParam("alasanAbsen") String alasanAbsen,
                                @RequestParam("tipeCuti") String tipeCuti,
                                @RequestParam("status") String status,
                                @org.springframework.security.core.annotation.AuthenticationPrincipal com.ppip.cutidinas.security.CustomUserDetails userDetails,
                                RedirectAttributes redirectAttributes) {

        PengajuanCuti pengajuan = pengajuanCutiRepository.findById(id).orElse(null);
        if (pengajuan == null) {
            redirectAttributes.addFlashAttribute("error", "Data tidak ditemukan.");
            return "redirect:/admin/pengajuan";
        }

        Cuti cuti = pengajuan.getCuti();
        int oldDeducted = cuti.getJenis().equalsIgnoreCase("Cuti Besar") ? pengajuan.getJumlahHariKalender() : pengajuan.getJumlahHariKerja();

        // 1. Revert original deduction if it was active
        boolean isOldStatusActive = "tersimpan".equalsIgnoreCase(pengajuan.getStatus()) || "menunggu persetujuan".equalsIgnoreCase(pengajuan.getStatus()) || "disetujui".equalsIgnoreCase(pengajuan.getStatus());
        if (isOldStatusActive) {
            cuti.setSisaHari(cuti.getSisaHari() + oldDeducted);
            if ("tersimpan".equalsIgnoreCase(pengajuan.getStatus()) || "menunggu persetujuan".equalsIgnoreCase(pengajuan.getStatus())) {
                cuti.setKuotaPending((cuti.getKuotaPending() != null ? cuti.getKuotaPending() : 0) - oldDeducted);
            }
        }

        // 2. Calculate new days
        long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(tanggalMulai, tanggalAkhir);
        int newKalenderDays = (int) daysBetween + 1;
        
        int newWorkingDays = 0;
        List<Holiday> holidays = holidayRepository.findAll();
        LocalDate curr = tanggalMulai;
        while (!curr.isAfter(tanggalAkhir)) {
            boolean isWeekend = (curr.getDayOfWeek().getValue() == 6 || curr.getDayOfWeek().getValue() == 7);
            LocalDate finalCurr = curr;
            boolean isHoliday = holidays.stream().anyMatch(h -> h.getDate().equals(finalCurr));
            if (!isWeekend && !isHoliday) {
                newWorkingDays++;
            }
            curr = curr.plusDays(1);
        }

        int newDeducted = cuti.getJenis().equalsIgnoreCase("Cuti Besar") ? newKalenderDays : newWorkingDays;

        if (newWorkingDays == 0 && cuti.getJenis().equalsIgnoreCase("Cuti Kecil")) {
            redirectAttributes.addFlashAttribute("error", "Tanggal merah, tidak perlu izin cuti.");
            return "redirect:/admin/pengajuan";
        }

        // 3. Apply new deduction if the new status is active
        boolean isNewStatusActive = "tersimpan".equalsIgnoreCase(status) || "menunggu persetujuan".equalsIgnoreCase(status) || "disetujui".equalsIgnoreCase(status);
        if (isNewStatusActive) {
            if (newDeducted > cuti.getSisaHari()) {
                redirectAttributes.addFlashAttribute("error", "Sisa saldo cuti tidak cukup.");
                return "redirect:/admin/pengajuan";
            }
            cuti.setSisaHari(cuti.getSisaHari() - newDeducted);
            if ("tersimpan".equalsIgnoreCase(status) || "menunggu persetujuan".equalsIgnoreCase(status)) {
                cuti.setKuotaPending((cuti.getKuotaPending() != null ? cuti.getKuotaPending() : 0) + newDeducted);
            }
        }
        
        cutiRepository.save(cuti);

        pengajuan.setTanggalMulai(tanggalMulai);
        pengajuan.setTanggalAkhir(tanggalAkhir);
        pengajuan.setJumlahHariKalender(newKalenderDays);
        pengajuan.setJumlahHariKerja(newWorkingDays);
        pengajuan.setPendelegasianPekerjaan(pendelegasianPekerjaan);
        pengajuan.setAlasanAbsen(alasanAbsen);
        pengajuan.setTipeCuti(tipeCuti);
        pengajuan.setStatus(status);
        if (userDetails != null) {
            pengajuan.setApproverBadgeid(userDetails.getBadgeId());
        }

        pengajuanCutiRepository.save(pengajuan);

        redirectAttributes.addFlashAttribute("success", "Pengajuan berhasil diperbarui.");
        return "redirect:/admin/pengajuan";
    }

    @PostMapping("/delete")
    public String deletePengajuan(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
        PengajuanCuti pengajuan = pengajuanCutiRepository.findById(id).orElse(null);
        if (pengajuan != null) {
            Cuti cuti = pengajuan.getCuti();
            int oldDeducted = cuti.getJenis().equalsIgnoreCase("Cuti Besar") ? pengajuan.getJumlahHariKalender() : pengajuan.getJumlahHariKerja();
            
            boolean isOldStatusActive = "tersimpan".equalsIgnoreCase(pengajuan.getStatus()) || "menunggu persetujuan".equalsIgnoreCase(pengajuan.getStatus()) || "disetujui".equalsIgnoreCase(pengajuan.getStatus());
            if (isOldStatusActive) {
                cuti.setSisaHari(cuti.getSisaHari() + oldDeducted);
                if ("tersimpan".equalsIgnoreCase(pengajuan.getStatus()) || "menunggu persetujuan".equalsIgnoreCase(pengajuan.getStatus())) {
                    cuti.setKuotaPending((cuti.getKuotaPending() != null ? cuti.getKuotaPending() : 0) - oldDeducted);
                }
            }
            
            cutiRepository.save(cuti);
            pengajuanCutiRepository.delete(pengajuan);
            redirectAttributes.addFlashAttribute("success", "Pengajuan berhasil dihapus.");
        }
        return "redirect:/admin/pengajuan";
    }
}
