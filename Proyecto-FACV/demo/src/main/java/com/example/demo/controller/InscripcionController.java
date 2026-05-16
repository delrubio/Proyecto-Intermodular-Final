package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.dto.InscripcionForm;
import com.example.demo.model.InscripcionPruebaId;
import com.example.demo.service.InscripcionService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class InscripcionController {

    @Autowired InscripcionService inscripcionService;

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
    public String crear(@ModelAttribute InscripcionForm form, RedirectAttributes redirectAttributes) {
        try {
            inscripcionService.save(form.getMatricula(), form.getPruebaId());
            redirectAttributes.addFlashAttribute("successMessage", "Inscripción realizada correctamente");
        } catch (Exception ex) {
            log.error("InscripcionController - Error: {}", ex.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Error al inscribir: " + ex.getMessage());
        }
        return "redirect:/inscripciones";
    }

    @PostMapping("/inscripciones/eliminar")
    public String eliminar(@ModelAttribute InscripcionForm form, RedirectAttributes redirectAttributes) {
        InscripcionPruebaId id = new InscripcionPruebaId(form.getMatricula(), form.getPruebaId());
        if (inscripcionService.findById(id) != null) {
            inscripcionService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Inscripción eliminada correctamente");
        }
        return "redirect:/inscripciones";
    }
}
