package com.ppip.cutidinas.controller;

import com.ppip.cutidinas.model.User;
import com.ppip.cutidinas.model.Cuti;
import com.ppip.cutidinas.repository.UserRepository;
import com.ppip.cutidinas.repository.CutiRepository;
import com.ppip.cutidinas.service.CutiService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.UUID;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CutiRepository cutiRepository;
    private final CutiService cutiService;

    // Define the upload directory inside static so it can be served directly
    private static final String UPLOAD_DIR = "src/main/resources/static/uploads/";

    @GetMapping("/users")
    public String listUsers(Model model) {
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("newUser", new User());
        model.addAttribute("cutis", cutiRepository.findAll());
        return "admin/users";
    }

    @PostMapping("/users/add")
    public String addUser(@ModelAttribute("newUser") User user, 
                          @RequestParam("role") String role, 
                          @RequestParam("file") MultipartFile file,
                          RedirectAttributes redirectAttributes) {
        
        if(userRepository.existsById(user.getBadgeid())) {
            redirectAttributes.addFlashAttribute("error", "Badge ID already exists.");
            return "redirect:/admin/users";
        }

        user.setPermissions(Arrays.asList(role));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        if (!file.isEmpty()) {
            try {
                Path uploadPath = Paths.get(UPLOAD_DIR);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                Path filePath = uploadPath.resolve(filename);
                Files.copy(file.getInputStream(), filePath);
                user.setProfilePicture("/uploads/" + filename);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        userRepository.save(user);
        cutiService.generateCutiIfNeeded(user);
        redirectAttributes.addFlashAttribute("success", "User created successfully!");
        return "redirect:/admin/users";
    }
    
    @GetMapping("/users/edit/{id}")
    public String editUserForm(@PathVariable("id") String badgeid, Model model) {
        User user = userRepository.findById(badgeid).orElse(null);
        if(user != null) {
            model.addAttribute("user", user);
            model.addAttribute("currentRole", user.getPermissions().contains("ROLE_ADMIN") ? "ROLE_ADMIN" : "ROLE_USER");
            return "admin/edit";
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/edit/{id}")
    public String updateUser(@PathVariable("id") String badgeid, 
                             @ModelAttribute("user") User updatedUser, 
                             @RequestParam("role") String role, 
                             @RequestParam("file") MultipartFile file,
                             RedirectAttributes redirectAttributes) {
        User existingUser = userRepository.findById(badgeid).orElse(null);
        if(existingUser != null) {
            existingUser.setName(updatedUser.getName());
            existingUser.setNik(updatedUser.getNik());
            existingUser.setEmail(updatedUser.getEmail());
            existingUser.setUnitKerja(updatedUser.getUnitKerja());
            existingUser.setTahunMasuk(updatedUser.getTahunMasuk());
            existingUser.setPermissions(Arrays.asList(role));
            
            if(updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
            }
            
            if (!file.isEmpty()) {
                try {
                    Path uploadPath = Paths.get(UPLOAD_DIR);
                    if (!Files.exists(uploadPath)) {
                        Files.createDirectories(uploadPath);
                    }
                    String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                    Path filePath = uploadPath.resolve(filename);
                    Files.copy(file.getInputStream(), filePath);
                    existingUser.setProfilePicture("/uploads/" + filename);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            userRepository.save(existingUser);
            redirectAttributes.addFlashAttribute("success", "User updated successfully!");
        }
        return "redirect:/admin/users";
    }
    
    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable("id") String badgeid, RedirectAttributes redirectAttributes) {
        userRepository.deleteById(badgeid);
        redirectAttributes.addFlashAttribute("success", "User deleted successfully!");
        return "redirect:/admin/users";
    }

    @GetMapping("/cuti/edit/{id}")
    public String editCutiForm(@PathVariable("id") Long id, Model model) {
        Cuti cuti = cutiRepository.findById(id).orElse(null);
        if(cuti != null) {
            model.addAttribute("cuti", cuti);
            return "admin/edit-cuti";
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/cuti/edit/{id}")
    public String updateCuti(@PathVariable("id") Long id, 
                             @ModelAttribute("cuti") Cuti updatedCuti, 
                             RedirectAttributes redirectAttributes) {
        Cuti existing = cutiRepository.findById(id).orElse(null);
        if(existing != null) {
            existing.setJenis(updatedCuti.getJenis());
            existing.setPeriode(updatedCuti.getPeriode());
            existing.setTanggalMulai(updatedCuti.getTanggalMulai());
            existing.setTanggalAkhir(updatedCuti.getTanggalAkhir());
            existing.setTotalHari(updatedCuti.getTotalHari());
            existing.setSisaHari(updatedCuti.getSisaHari());
            cutiRepository.save(existing);
            redirectAttributes.addFlashAttribute("success", "Cuti record updated successfully!");
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/cuti/delete/{id}")
    public String deleteCuti(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        cutiRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Cuti record deleted successfully!");
        return "redirect:/admin/users";
    }
}
