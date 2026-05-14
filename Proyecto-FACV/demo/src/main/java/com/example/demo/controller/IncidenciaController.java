package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.enums.Estado;
import com.example.demo.model.Incidencia;
import com.example.demo.service.IncidenciaService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class IncidenciaController {

    private final IncidenciaService incidenciaService;

    public IncidenciaController(IncidenciaService incidenciaService) {
        this.incidenciaService = incidenciaService;
    }

    @GetMapping("/incidencias")
    public String listar(@RequestParam(required = false) String matricula, Model model) {
        if (matricula != null && !matricula.isBlank()) {
            var vehiculo = incidenciaService.findVehiculoByMatricula(matricula);
            if (vehiculo == null) return "redirect:/incidencias";
            log.info("IncidenciaController - Incidencias para vehículo: {}", matricula);
            model.addAttribute("incidencias", incidenciaService.findByVehiculo(matricula));
            model.addAttribute("vehiculo", vehiculo);
            model.addAttribute("matriculaFiltro", matricula);
        } else {
            log.info("IncidenciaController - Listado global de incidencias");
            model.addAttribute("incidencias", incidenciaService.findAllVisible());
            model.addAttribute("vehiculo", null);
            model.addAttribute("matriculaFiltro", null);
        }
        return "incidencias";
    }

    @GetMapping("/incidencias/{id}/editar")
    public String editarForm(@PathVariable Integer id, Model model) {
        Incidencia incidencia = incidenciaService.findById(id);
        if (incidencia == null) return "redirect:/incidencias";
        model.addAttribute("incidencia", incidencia);
        model.addAttribute("estados", incidenciaService.getAllEstados());
        model.addAttribute("formTitle", "Editar incidencia");
        model.addAttribute("formAction", "/incidencias/" + id + "/editar");
        model.addAttribute("submitLabel", "Guardar cambios");
        return "editar-incidencia";
    }

    @PostMapping("/incidencias/{id}/editar")
    public String editar(
            @PathVariable Integer id,
            @RequestParam String descripcionIncidencia,
            @RequestParam Estado estado,
            RedirectAttributes redirectAttributes) {
        Incidencia existente = incidenciaService.findById(id);
        String matricula = (existente != null && existente.getVehiculo() != null)
                ? existente.getVehiculo().getMatricula() : null;
        try {
            incidenciaService.update(id, descripcionIncidencia, estado);
            redirectAttributes.addFlashAttribute("successMessage", "Incidencia actualizada correctamente");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al actualizar: " + ex.getMessage());
        }
        return matricula != null
                ? "redirect:/incidencias?matricula=" + matricula
                : "redirect:/incidencias";
    }

    @PostMapping("/incidencias/{id}/eliminar")
    public String eliminar(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        Incidencia incidencia = incidenciaService.findById(id);
        String matricula = (incidencia != null && incidencia.getVehiculo() != null)
                ? incidencia.getVehiculo().getMatricula() : null;
        if (incidencia != null) {
            incidenciaService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Incidencia eliminada correctamente");
        }
        return matricula != null
                ? "redirect:/incidencias?matricula=" + matricula
                : "redirect:/incidencias";
    }
}
