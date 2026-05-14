package com.example.demo.controller;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.enums.ResultadoVerificacion;
import com.example.demo.service.VerificacionService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class VerificacionController {

    private final VerificacionService verificacionService;

    public VerificacionController(VerificacionService verificacionService) {
        this.verificacionService = verificacionService;
    }

    @GetMapping("/verificaciones")
    public String listar(Model model) {
        model.addAttribute("verificaciones", verificacionService.findAll());
        return "verificaciones";
    }

    @GetMapping("/verificaciones/seleccionar-prueba")
    public String seleccionarPruebaForm(Model model) {
        model.addAttribute("pruebas", verificacionService.getAllPruebas());
        return "verificacion-seleccionar-prueba";
    }

    @PostMapping("/verificaciones/seleccionar-prueba")
    public String seleccionarPruebaSubmit(@RequestParam Integer pruebaId) {
        return "redirect:/verificaciones/pendientes?pruebaId=" + pruebaId;
    }

    @GetMapping("/verificaciones/pendientes")
    public String vehiculosPendientes(@RequestParam Integer pruebaId, Model model) {
        var prueba = verificacionService.getAllPruebas().stream()
                .filter(p -> p.getIdPrueba().equals(pruebaId))
                .findFirst().orElse(null);
        if (prueba == null) return "redirect:/verificaciones/seleccionar-prueba";
        model.addAttribute("prueba", prueba);
        model.addAttribute("vehiculosPendientes", verificacionService.getVehiculosPendientesPorPrueba(pruebaId));
        model.addAttribute("pruebaId", pruebaId);
        return "verificacion-pendientes";
    }

    @GetMapping("/nueva-verificacion")
    public String nuevaForm(
            @RequestParam(required = false) String vehiculoMatricula,
            @RequestParam(required = false) Integer pruebaId,
            Model model) {
        model.addAttribute("vehiculos", verificacionService.getAllVehiculos());
        model.addAttribute("pruebas", verificacionService.getAllPruebas());
        model.addAttribute("tecnicos", verificacionService.getAllTecnicos());
        model.addAttribute("resultados", ResultadoVerificacion.values());
        model.addAttribute("preselectedVehiculoMatricula", vehiculoMatricula);
        model.addAttribute("preselectedPruebaId", pruebaId);
        model.addAttribute("formTitle", "Nueva verificación");
        model.addAttribute("formAction", "/nueva-verificacion");
        model.addAttribute("submitLabel", "Crear verificación");
        return "nueva-verificacion";
    }

    @PostMapping("/nueva-verificacion")
    public String crear(
            @RequestParam String matricula,
            @RequestParam Integer pruebaId,
            @RequestParam(required = false) String resultado,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam(required = false) String tecnico2Licencia,
            @RequestParam(required = false) String tecnico1Licencia,
            @RequestParam(required = false) Integer fromPruebaId,
            RedirectAttributes redirectAttributes) {
        try {
            verificacionService.save(matricula, pruebaId, resultado, fecha, tecnico2Licencia, tecnico1Licencia);
            redirectAttributes.addFlashAttribute("successMessage", "Verificación creada correctamente");
        } catch (Exception ex) {
            log.error("VerificacionController - Error: {}", ex.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + ex.getMessage());
        }
        if (fromPruebaId != null) return "redirect:/verificaciones/pendientes?pruebaId=" + fromPruebaId;
        return "redirect:/verificaciones";
    }

    @GetMapping("/verificaciones/{id}/editar")
    public String editarForm(@PathVariable Integer id, Model model) {
        var verificacion = verificacionService.findById(id);
        if (verificacion == null) return "redirect:/verificaciones";
        model.addAttribute("verificacion", verificacion);
        model.addAttribute("vehiculos", verificacionService.getAllVehiculos());
        model.addAttribute("pruebas", verificacionService.getAllPruebas());
        model.addAttribute("tecnicos", verificacionService.getAllTecnicos());
        model.addAttribute("resultados", ResultadoVerificacion.values());
        model.addAttribute("preselectedVehiculoMatricula",
                verificacion.getVehiculo() != null ? verificacion.getVehiculo().getMatricula() : null);
        model.addAttribute("preselectedPruebaId",
                verificacion.getPrueba() != null ? verificacion.getPrueba().getIdPrueba() : null);
        model.addAttribute("formTitle", "Editar verificación");
        model.addAttribute("formAction", "/verificaciones/" + id + "/editar");
        model.addAttribute("submitLabel", "Guardar cambios");
        return "nueva-verificacion";
    }

    @PostMapping("/verificaciones/{id}/editar")
    public String editar(
            @PathVariable Integer id,
            @RequestParam String matricula,
            @RequestParam Integer pruebaId,
            @RequestParam(required = false) String resultado,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam(required = false) String tecnico2Licencia,
            @RequestParam(required = false) String tecnico1Licencia,
            RedirectAttributes redirectAttributes) {
        try {
            verificacionService.update(id, matricula, pruebaId, resultado, fecha, tecnico2Licencia, tecnico1Licencia);
            redirectAttributes.addFlashAttribute("successMessage", "Verificación actualizada correctamente");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al actualizar: " + ex.getMessage());
        }
        return "redirect:/verificaciones";
    }

    @PostMapping("/verificaciones/{id}/eliminar")
    public String eliminar(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        if (verificacionService.findById(id) != null) {
            verificacionService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Verificación eliminada correctamente");
        }
        return "redirect:/verificaciones";
    }
}
