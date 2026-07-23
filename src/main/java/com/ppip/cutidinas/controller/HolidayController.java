package com.ppip.cutidinas.controller;

import com.ppip.cutidinas.model.Holiday;
import com.ppip.cutidinas.repository.HolidayRepository;
import com.ppip.cutidinas.service.HolidayService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/holidays")
@RequiredArgsConstructor
public class HolidayController {

    private final HolidayRepository holidayRepository;
    private final HolidayService holidayService;

    @GetMapping
    public String holidaysPage(Model model) {
        model.addAttribute("newHoliday", new Holiday());
        return "admin/holidays";
    }

    @GetMapping("/api")
    @ResponseBody
    public List<Map<String, Object>> getHolidaysApi() {
        return holidayRepository.findAll().stream().map(h -> {
            Map<String, Object> event = new HashMap<>();
            event.put("id", h.getId());
            event.put("title", h.getDescription());
            event.put("start", h.getDate().toString());
            event.put("allDay", true);
            event.put("color", "#ef4444");
            return event;
        }).collect(Collectors.toList());
    }

    @PostMapping("/add")
    public String addHoliday(@ModelAttribute("newHoliday") Holiday holiday, RedirectAttributes redirectAttributes) {
        if (holidayRepository.findByDate(holiday.getDate()).isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Hari libur sudah ada pada tanggal tersebut!");
            return "redirect:/admin/holidays";
        }
        holidayRepository.save(holiday);
        redirectAttributes.addFlashAttribute("success", "Hari libur berhasil ditambahkan.");
        return "redirect:/admin/holidays";
    }

    @PostMapping("/delete/{id}")
    public String deleteHoliday(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        holidayRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Hari libur berhasil dihapus.");
        return "redirect:/admin/holidays";
    }

    @PostMapping("/sync")
    public String syncHolidays(RedirectAttributes redirectAttributes) {
        try {
            int count = holidayService.syncIndonesianHolidays();
            redirectAttributes.addFlashAttribute("success", "Berhasil mensinkronisasi " + count + " hari libur baru dengan Google Calendar!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Gagal melakukan sinkronisasi: " + e.getMessage());
        }
        return "redirect:/admin/holidays";
    }
}
