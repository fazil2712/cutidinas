package com.ppip.cutidinas.controller;

import com.ppip.cutidinas.dto.SaldoCutiDto;
import com.ppip.cutidinas.model.Cuti;
import com.ppip.cutidinas.model.PengajuanCuti;
import com.ppip.cutidinas.model.Holiday;
import com.ppip.cutidinas.model.User;
import com.ppip.cutidinas.repository.CutiRepository;
import com.ppip.cutidinas.repository.HolidayRepository;
import com.ppip.cutidinas.repository.PengajuanCutiRepository;
import com.ppip.cutidinas.repository.UserRepository;
import com.ppip.cutidinas.security.CustomUserDetails;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/cuti/pengajuan")
@RequiredArgsConstructor
public class PengajuanCutiController {

    private final CutiRepository cutiRepository;
    private final PengajuanCutiRepository pengajuanCutiRepository;
    private final HolidayRepository holidayRepository;
    private final UserRepository userRepository;

    @GetMapping
    public String pengajuanPage(Model model, @AuthenticationPrincipal CustomUserDetails userDetails, HttpSession session) {
        String badgeid = userDetails.getBadgeId();
        User user = userRepository.findById(badgeid).orElse(null);
        
        List<Cuti> cutiList = cutiRepository.findByBadgeid(badgeid);
        List<SaldoCutiDto> saldoList = cutiList.stream()
                .map(c -> new SaldoCutiDto(c, c.getKuotaPending() != null ? c.getKuotaPending() : 0))
                .toList();

        int currentYear = LocalDate.now().getYear();
        Cuti activeCuti = cutiList.stream().filter(c -> c.getPeriode() == currentYear).findFirst().orElse(null);

        List<PengajuanCuti> historyList = pengajuanCutiRepository.findByCutiBadgeidOrderByTanggalMulaiDesc(badgeid);

        model.addAttribute("saldoList", saldoList);
        model.addAttribute("historyList", historyList);
        model.addAttribute("activeCuti", activeCuti);
        model.addAttribute("userNik", user != null ? user.getNik() : "");
        model.addAttribute("newPengajuan", new PengajuanCuti());
        
        java.util.Map<String, String> userNames = new java.util.HashMap<>();
        for (User u : userRepository.findAll()) {
            if (u.getBadgeid() != null) {
                String name = u.getName() != null ? u.getName() : u.getBadgeid();
                userNames.put(u.getBadgeid(), name + " (" + u.getBadgeid() + ")");
            }
        }
        model.addAttribute("userNames", userNames);
        
        session.setAttribute("currentModule", "cuti");
        
        return "cuti/pengajuan";
    }

    @PostMapping("/add")
    public String addPengajuan(@ModelAttribute("newPengajuan") PengajuanCuti pengajuan,
                               @org.springframework.web.bind.annotation.RequestParam(value = "file", required = false) org.springframework.web.multipart.MultipartFile file,
                               @AuthenticationPrincipal CustomUserDetails userDetails,
                               RedirectAttributes redirectAttributes) {
                               
        String badgeid = userDetails.getBadgeId();
        int currentYear = LocalDate.now().getYear();
        Cuti activeCuti = cutiRepository.findByBadgeid(badgeid).stream()
                .filter(c -> c.getPeriode() == currentYear).findFirst().orElse(null);

        if (activeCuti == null) {
            redirectAttributes.addFlashAttribute("error", "Anda tidak memiliki saldo cuti aktif tahun ini.");
            return "redirect:/cuti/pengajuan";
        }

        long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(pengajuan.getTanggalMulai(), pengajuan.getTanggalAkhir());
        int kalenderDays = (int) daysBetween + 1;
        
        int workingDays = 0;
        List<Holiday> holidays = holidayRepository.findAll();
        LocalDate curr = pengajuan.getTanggalMulai();
        while (!curr.isAfter(pengajuan.getTanggalAkhir())) {
            boolean isWeekend = (curr.getDayOfWeek().getValue() == 6 || curr.getDayOfWeek().getValue() == 7);
            LocalDate finalCurr = curr;
            boolean isHoliday = holidays.stream().anyMatch(h -> h.getDate().equals(finalCurr));
            
            if (!isWeekend && !isHoliday) {
                workingDays++;
            }
            curr = curr.plusDays(1);
        }

        pengajuan.setJumlahHariKalender(kalenderDays);
        pengajuan.setJumlahHariKerja(workingDays);

        int deductedDays = activeCuti.getJenis().equalsIgnoreCase("Cuti Besar") ? kalenderDays : workingDays;

        if (workingDays == 0 && activeCuti.getJenis().equalsIgnoreCase("Cuti Kecil")) {
            redirectAttributes.addFlashAttribute("error", "Tanggal merah, tidak perlu izin cuti.");
            return "redirect:/cuti/pengajuan";
        }

        if (deductedDays > activeCuti.getSisaHari()) {
            redirectAttributes.addFlashAttribute("error", "Sisa saldo cuti tidak cukup.");
            return "redirect:/cuti/pengajuan";
        }

        activeCuti.setSisaHari(activeCuti.getSisaHari() - deductedDays);
        activeCuti.setKuotaPending((activeCuti.getKuotaPending() != null ? activeCuti.getKuotaPending() : 0) + deductedDays);
        cutiRepository.save(activeCuti);

        pengajuan.setCuti(activeCuti);
        pengajuan.setStatus("tersimpan");
        
        if (file != null && !file.isEmpty()) {
            if (!"application/pdf".equals(file.getContentType()) && (file.getOriginalFilename() == null || !file.getOriginalFilename().toLowerCase().endsWith(".pdf"))) {
                redirectAttributes.addFlashAttribute("error", "File pendukung harus berupa PDF.");
                return "redirect:/cuti/pengajuan";
            }
            if (file.getSize() > 50 * 1024 * 1024) {
                redirectAttributes.addFlashAttribute("error", "Ukuran file maksimal 50MB.");
                return "redirect:/cuti/pengajuan";
            }
            try {
                String originalFilename = org.springframework.util.StringUtils.cleanPath(file.getOriginalFilename());
                // Prefix with timestamp to prevent overwriting, but keep original name readable
                String savedFilename = System.currentTimeMillis() + "_" + originalFilename;
                java.nio.file.Path uploadPath = java.nio.file.Paths.get("uploads").toAbsolutePath().normalize();
                if (!java.nio.file.Files.exists(uploadPath)) {
                    java.nio.file.Files.createDirectories(uploadPath);
                }
                java.nio.file.Path filePath = uploadPath.resolve(savedFilename);
                java.nio.file.Files.copy(file.getInputStream(), filePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                pengajuan.setFilePendukung(savedFilename);
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", "Gagal mengunggah file pendukung.");
                return "redirect:/cuti/pengajuan";
            }
        }

        pengajuanCutiRepository.save(pengajuan);

        redirectAttributes.addFlashAttribute("success", "Pengajuan cuti berhasil disimpan.");
        return "redirect:/cuti/pengajuan";
    }

    @PostMapping("/action")
    public String actionPengajuan(@org.springframework.web.bind.annotation.RequestParam("id") Long id,
                                  @org.springframework.web.bind.annotation.RequestParam("action") String action,
                                  @AuthenticationPrincipal CustomUserDetails userDetails,
                                  RedirectAttributes redirectAttributes) {
        
        PengajuanCuti pengajuan = pengajuanCutiRepository.findById(id).orElse(null);
        if (pengajuan == null || !pengajuan.getCuti().getBadgeid().equals(userDetails.getBadgeId())) {
            redirectAttributes.addFlashAttribute("error", "Data tidak ditemukan atau Anda tidak memiliki akses.");
            return "redirect:/cuti/pengajuan";
        }

        if ("ajukan".equals(action)) {
            if ("tersimpan".equalsIgnoreCase(pengajuan.getStatus())) {
                pengajuan.setStatus("menunggu persetujuan");
                pengajuanCutiRepository.save(pengajuan);
                redirectAttributes.addFlashAttribute("success", "Pengajuan cuti berhasil diajukan.");
            } else {
                redirectAttributes.addFlashAttribute("error", "Hanya pengajuan tersimpan yang dapat diajukan.");
            }
        } else if ("batal".equals(action)) {
            if ("tersimpan".equalsIgnoreCase(pengajuan.getStatus()) || "menunggu persetujuan".equalsIgnoreCase(pengajuan.getStatus())) {
                Cuti cuti = pengajuan.getCuti();
                int deductedDays = cuti.getJenis().equalsIgnoreCase("Cuti Besar") ? pengajuan.getJumlahHariKalender() : pengajuan.getJumlahHariKerja();
                
                cuti.setSisaHari(cuti.getSisaHari() + deductedDays);
                cuti.setKuotaPending((cuti.getKuotaPending() != null ? cuti.getKuotaPending() : 0) - deductedDays);
                cutiRepository.save(cuti);
                
                pengajuan.setStatus("dibatalkan");
                pengajuan.setApproverBadgeid(userDetails.getBadgeId());
                pengajuanCutiRepository.save(pengajuan);
                redirectAttributes.addFlashAttribute("success", "Pengajuan cuti berhasil dibatalkan.");
            } else {
                redirectAttributes.addFlashAttribute("error", "Pengajuan ini tidak dapat dibatalkan.");
            }
        }
        
        return "redirect:/cuti/pengajuan";
    }
}
