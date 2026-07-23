package com.ppip.cutidinas.controller;

import com.ppip.cutidinas.model.Cuti;
import com.ppip.cutidinas.model.PengajuanCuti;
import com.ppip.cutidinas.repository.CutiRepository;
import com.ppip.cutidinas.repository.PengajuanCutiRepository;
import com.ppip.cutidinas.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/cuti/persetujuan")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class PersetujuanCutiController {

    private final PengajuanCutiRepository pengajuanCutiRepository;
    private final CutiRepository cutiRepository;

    @GetMapping
    public String dashboard(@RequestParam(name = "tab", defaultValue = "pending") String tab,
                            @AuthenticationPrincipal CustomUserDetails userDetails,
                            Model model) {
                            
        String myBadgeid = userDetails.getBadgeId();
        
        List<PengajuanCuti> allPending = pengajuanCutiRepository.findByStatusOrderByTanggalMulaiDesc("menunggu persetujuan");

        List<PengajuanCuti> historyList = pengajuanCutiRepository.findByApproverBadgeidOrderByTanggalMulaiDesc(myBadgeid);

        model.addAttribute("pendingCount", allPending.size());
        model.addAttribute("historyCount", historyList.size());
        
        if ("history".equals(tab)) {
            model.addAttribute("pengajuanList", historyList);
            model.addAttribute("activeTab", "history");
        } else {
            model.addAttribute("pengajuanList", allPending);
            model.addAttribute("activeTab", "pending");
        }
        
        return "cuti/persetujuan";
    }

    @PostMapping("/process")
    public String processPengajuan(@RequestParam("id") Long id,
                                   @RequestParam("action") String action, // "accept" or "reject"
                                   @RequestParam(value = "alasanPenolakan", required = false) String alasanPenolakan,
                                   @AuthenticationPrincipal CustomUserDetails userDetails,
                                   RedirectAttributes redirectAttributes) {

        PengajuanCuti pengajuan = pengajuanCutiRepository.findById(id).orElse(null);
        if (pengajuan == null) {
            redirectAttributes.addFlashAttribute("error", "Pengajuan tidak ditemukan.");
            return "redirect:/cuti/persetujuan";
        }

        if (!"menunggu persetujuan".equalsIgnoreCase(pengajuan.getStatus())) {
            redirectAttributes.addFlashAttribute("error", "Pengajuan ini sudah diproses atau belum diajukan.");
            return "redirect:/cuti/persetujuan";
        }

        Cuti cuti = pengajuan.getCuti();
        int deductedDays = cuti.getJenis().equalsIgnoreCase("Cuti Besar") ? pengajuan.getJumlahHariKalender() : pengajuan.getJumlahHariKerja();

        pengajuan.setApproverBadgeid(userDetails.getBadgeId());

        if ("accept".equalsIgnoreCase(action)) {
            pengajuan.setStatus("disetujui");
            // Sisa hari was already deducted when created. Kuota pending needs to be decremented because it's no longer pending.
            cuti.setKuotaPending((cuti.getKuotaPending() != null ? cuti.getKuotaPending() : 0) - deductedDays);
            redirectAttributes.addFlashAttribute("success", "Pengajuan cuti berhasil disetujui.");
        } else if ("reject".equalsIgnoreCase(action)) {
            pengajuan.setStatus("ditolak");
            pengajuan.setAlasanPenolakan(alasanPenolakan);
            
            // Refund the sisa hari
            cuti.setSisaHari(cuti.getSisaHari() + deductedDays);
            // Decrement pending since it's resolved
            cuti.setKuotaPending((cuti.getKuotaPending() != null ? cuti.getKuotaPending() : 0) - deductedDays);
            redirectAttributes.addFlashAttribute("success", "Pengajuan cuti berhasil ditolak.");
        }

        cutiRepository.save(cuti);
        pengajuanCutiRepository.save(pengajuan);

        return "redirect:/cuti/persetujuan";
    }
}
