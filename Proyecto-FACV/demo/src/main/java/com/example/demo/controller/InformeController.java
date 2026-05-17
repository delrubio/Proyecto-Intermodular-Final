package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.dto.InformeForm;
import com.example.demo.service.InformeService;

import lombok.extern.slf4j.Slf4j;

/**
 * Controlador MVC para la gestión de informes de observación.
 * <p>
 * Rutas principales:
 * <ul>
 *   <li>{@code GET /informes} – listado de todos los informes.</li>
 *   <li>{@code GET/POST /nuevo-informe} – creación (ADMINISTRADOR, OBSERVADOR).</li>
 *   <li>{@code GET/POST /informes/{id}/editar} – edición (ADMINISTRADOR, OBSERVADOR).</li>
 *   <li>{@code POST /informes/{id}/eliminar} – eliminación (ADMINISTRADOR, OBSERVADOR).</li>
 * </ul>
 * Los formularios POST usan {@link com.example.demo.dto.InformeForm} como DTO.
 * </p>
 */
@Slf4j
@Controller
public class InformeController {

    @Autowired InformeService informeService;

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
    public String crear(@ModelAttribute InformeForm form, RedirectAttributes redirectAttributes) {
        try {
            informeService.save(form.getPruebaId(), form.getContenido(), form.getFecha(), form.getPuntuacionFinal());
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
    public String editar(@PathVariable Integer id, @ModelAttribute InformeForm form, RedirectAttributes redirectAttributes) {
        try {
            informeService.update(id, form.getContenido(), form.getFecha(), form.getPuntuacionFinal());
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
