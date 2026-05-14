package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.model.InscripcionPruebaId;
import com.example.demo.service.InscripcionService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class InscripcionController {

    private final InscripcionService inscripcionService;

    public InscripcionController(InscripcionService inscripcionService) {
        this.inscripcionService = inscripcionService;
    }

    @GetMapping("/inscripciones")
    public String listar(Model model) {
        log.info("InscripcionController - Listado de inscripciones");
        model.addAttribute("inscripciones", inscripcionService.findAll());
        return "inscripciones";
    }

    @GetMapping("/nueva-inscripcion")
    public String nuevaForm(Model model) {
        model.addAttribute("vehiculos", inscripcionService.getAllVehiculos());
        model.addAttribute("pruebas", inscripcionService.getAllPruebas());
        return "nueva-inscripcion";
    }

    @PostMapping("/nueva-inscripcion")
    public String crear(
            @RequestParam String matricula,
            @RequestParam Integer pruebaId,
            RedirectAttributes redirectAttributes) {
        try {
            inscripcionService.save(matricula, pruebaId);
            redirectAttributes.addFlashAttribute("successMessage", "Inscripción realizada correctamente");
        } catch (Exception ex) {
            log.error("InscripcionController - Error: {}", ex.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Error al inscribir: " + ex.getMessage());
        }
        return "redirect:/inscripciones";
    }

    @PostMapping("/inscripciones/eliminar")
    public String eliminar(
            @RequestParam String matricula,
            @RequestParam Integer pruebaId,
            RedirectAttributes redirectAttributes) {
        InscripcionPruebaId id = new InscripcionPruebaId(matricula, pruebaId);
        if (inscripcionService.findById(id) != null) {
            inscripcionService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Inscripción eliminada correctamente");
        }
        return "redirect:/inscripciones";
    }
}
