package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.dto.VerificacionForm;
import com.example.demo.enums.ResultadoVerificacion;
import com.example.demo.service.VerificacionService;

import lombok.extern.slf4j.Slf4j;

/**
 * Controlador MVC para la gestión del proceso de verificación técnica.
 * <p>
 * Rutas principales:
 * <ul>
 *   <li>{@code GET /verificaciones} – listado global (ADMINISTRADOR, TECNICO).</li>
 *   <li>{@code GET/POST /verificaciones/seleccionar-prueba} – selección de prueba para ver pendientes.</li>
 *   <li>{@code GET /verificaciones/pendientes?pruebaId=} – vehículos inscritos sin verificar.</li>
 *   <li>{@code GET/POST /nueva-verificacion} – crear verificación (ADMINISTRADOR, TECNICO).</li>
 *   <li>{@code GET/POST /verificaciones/{id}/editar} – editar verificación.</li>
 *   <li>{@code POST /verificaciones/{id}/eliminar} – eliminar verificación.</li>
 * </ul>
 * Los formularios POST usan {@link com.example.demo.dto.VerificacionForm} como DTO.
 * </p>
 */
@Slf4j
@Controller
public class VerificacionController {

    @Autowired VerificacionService verificacionService;

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
        var prueba = verificacionService.getAllPruebas().stream().filter(p -> p.getIdPrueba().equals(pruebaId)).findFirst().orElse(null);
        if (prueba == null) return "redirect:/verificaciones/seleccionar-prueba";
        model.addAttribute("prueba", prueba);
        model.addAttribute("vehiculosPendientes", verificacionService.getVehiculosPendientesPorPrueba(pruebaId));
        model.addAttribute("pruebaId", pruebaId);
        return "verificacion-pendientes";
    }

    @GetMapping("/nueva-verificacion")
    public String nuevaForm(@RequestParam(required = false) String vehiculoMatricula, @RequestParam(required = false) Integer pruebaId,Model model) {
        if (pruebaId != null) {
            model.addAttribute("vehiculos", verificacionService.getVehiculosPendientesPorPrueba(pruebaId));
        } else {
            model.addAttribute("vehiculos", verificacionService.getAllVehiculos());
        }
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
    public String crear(@ModelAttribute VerificacionForm form, RedirectAttributes redirectAttributes) {
        try {
            verificacionService.save(form.getMatricula(), form.getPruebaId(), form.getResultado(),form.getFecha(), form.getTecnico2Licencia(), form.getTecnico1Licencia());
            redirectAttributes.addFlashAttribute("successMessage", "Verificación creada correctamente");
        } catch (Exception ex) {
            log.error("VerificacionController - Error: {}", ex.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + ex.getMessage());
        }
        if (form.getFromPruebaId() != null) return "redirect:/verificaciones/pendientes?pruebaId=" + form.getFromPruebaId();
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
        model.addAttribute("preselectedVehiculoMatricula", verificacion.getVehiculo() != null ? verificacion.getVehiculo().getMatricula() : null);
        model.addAttribute("preselectedPruebaId", verificacion.getPrueba() != null ? verificacion.getPrueba().getIdPrueba() : null);
        model.addAttribute("formTitle", "Editar verificación");
        model.addAttribute("formAction", "/verificaciones/" + id + "/editar");
        model.addAttribute("submitLabel", "Guardar cambios");
        return "nueva-verificacion";
    }

    @PostMapping("/verificaciones/{id}/editar")
    public String editar(@PathVariable Integer id, @ModelAttribute VerificacionForm form, RedirectAttributes redirectAttributes) {
        try {
            verificacionService.update(id, form.getMatricula(), form.getPruebaId(), form.getResultado(), form.getFecha(), form.getTecnico2Licencia(), form.getTecnico1Licencia());
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
