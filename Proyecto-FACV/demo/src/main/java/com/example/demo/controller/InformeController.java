package com.example.demo.controller;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.service.InformeService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class InformeController {

    private final InformeService informeService;

    public InformeController(InformeService informeService) {
        this.informeService = informeService;
    }

    @GetMapping("/informes")
    public String listar(Model model) {
        log.info("InformeController - Listado de informes");
        model.addAttribute("informes", informeService.findAll());
        return "informes";
    }

    @GetMapping("/nuevo-informe")
    public String nuevaForm(Model model) {
        model.addAttribute("pruebas", informeService.getAllPruebas());
        model.addAttribute("formTitle", "Nuevo informe");
        model.addAttribute("formAction", "/nuevo-informe");
        model.addAttribute("submitLabel", "Crear informe");
        return "nuevo-informe";
    }

    @PostMapping("/nuevo-informe")
    public String crear(
            @RequestParam Integer pruebaId,
            @RequestParam(required = false) String contenido,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam(required = false) BigDecimal puntuacionFinal,
            RedirectAttributes redirectAttributes) {
        try {
            informeService.save(pruebaId, contenido, fecha, puntuacionFinal);
            redirectAttributes.addFlashAttribute("successMessage", "Informe creado correctamente");
        } catch (Exception ex) {
            log.error("InformeController - Error: {}", ex.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Error al crear informe: " + ex.getMessage());
        }
        return "redirect:/informes";
    }

    @GetMapping("/informes/{id}/editar")
    public String editarForm(@PathVariable Integer id, Model model) {
        var informe = informeService.findById(id);
        if (informe == null) return "redirect:/informes";
        model.addAttribute("informe", informe);
        model.addAttribute("pruebas", informeService.getAllPruebas());
        model.addAttribute("formTitle", "Editar informe");
        model.addAttribute("formAction", "/informes/" + id + "/editar");
        model.addAttribute("submitLabel", "Guardar cambios");
        return "nuevo-informe";
    }

    @PostMapping("/informes/{id}/editar")
    public String editar(
            @PathVariable Integer id,
            @RequestParam(required = false) String contenido,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam(required = false) BigDecimal puntuacionFinal,
            RedirectAttributes redirectAttributes) {
        try {
            informeService.update(id, contenido, fecha, puntuacionFinal);
            redirectAttributes.addFlashAttribute("successMessage", "Informe actualizado correctamente");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al actualizar: " + ex.getMessage());
        }
        return "redirect:/informes";
    }

    @PostMapping("/informes/{id}/eliminar")
    public String eliminar(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        if (informeService.findById(id) != null) {
            informeService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Informe eliminado correctamente");
        }
        return "redirect:/informes";
    }
}
