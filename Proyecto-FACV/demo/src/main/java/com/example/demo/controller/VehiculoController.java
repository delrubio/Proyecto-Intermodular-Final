package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.model.Piloto;
import com.example.demo.model.Vehiculo;
import com.example.demo.repository.PilotoRepository;
import com.example.demo.service.VehiculoService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

/**
 * Controlador MVC para la gestión de vehículos de competición.
 * <p>
 * Rutas principales:
 * <ul>
 *   <li>{@code GET /vehiculos} – listado de todos los vehículos.</li>
 *   <li>{@code GET/POST /nuevo-vehiculo} – formulario de creación (ADMINISTRADOR, PILOTO).</li>
 *   <li>{@code GET/POST /vehiculos/{matricula}/editar} – formulario de edición (ADMINISTRADOR, PILOTO).</li>
 *   <li>{@code POST /vehiculos/{matricula}/eliminar} – eliminación (ADMINISTRADOR, PILOTO).</li>
 * </ul>
 * La validación Bean Validation ({@code @Valid}) se aplica sobre la entidad {@link com.example.demo.model.Vehiculo}
 * vinculada con {@code @ModelAttribute}.
 * </p>
 */
@Slf4j
@Controller
public class VehiculoController {

    @Autowired VehiculoService vehiculoService;
    @Autowired PilotoRepository pilotoRepository;

    @GetMapping("/vehiculos")
    public String vehiculosView(Model model) {
        log.info("VehiculoController - Listado de vehículos");
        model.addAttribute("vehiculos", vehiculoService.findAll());
        model.addAttribute("searchForm", new Vehiculo());
        return "vehiculos";
    }

    @GetMapping("/nuevo-vehiculo")
    public String nuevoVehiculoForm(Model model) {
        model.addAttribute("vehiculo", new Vehiculo());
        modelNewForm(model);
        addPilotosToModel(model);
        return "nuevo-vehiculo";
    }

    @PostMapping("/nuevo-vehiculo")
    public String nuevoVehiculoSubmit(@Valid @ModelAttribute("vehiculo") Vehiculo vehiculo, BindingResult bindingResult, @RequestParam(required = false) String pilotoLicencia, RedirectAttributes redirectAttributes, Model model) {
        if (bindingResult.hasErrors()) {
            modelNewForm(model);
            addPilotosToModel(model);
            return "nuevo-vehiculo";
        }
        try {
            vehiculoService.save(vehiculo, pilotoLicencia);
            redirectAttributes.addFlashAttribute("successMessage", "Vehículo creado correctamente");
            return "redirect:/vehiculos";
        } catch (RuntimeException ex) {
            log.error("VehiculoController - Error al crear vehículo: {}", ex.getMessage());
            bindingResult.reject("vehiculo.error", ex.getMessage());
            modelNewForm(model);
            addPilotosToModel(model);
            return "nuevo-vehiculo";
        }
    }

    @GetMapping("/vehiculos/{matricula}/editar")
    public String editarVehiculoForm(@PathVariable String matricula, Model model) {
        Vehiculo vehiculo = vehiculoService.findById(matricula);
        if (vehiculo == null) return "redirect:/vehiculos";
        model.addAttribute("vehiculo", vehiculo);
        modelEditForm(model, matricula);
        addPilotosToModel(model);
        return "nuevo-vehiculo";
    }

    @PostMapping("/vehiculos/{matricula}/editar")
    public String editarVehiculoSubmit(@PathVariable String matricula, @Valid @ModelAttribute("vehiculo") Vehiculo vehiculo, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
        if (vehiculoService.findById(matricula) == null) return "redirect:/vehiculos";
        if (bindingResult.hasErrors()) {
            modelEditForm(model, matricula);
            addPilotosToModel(model);
            return "nuevo-vehiculo";
        }
        try {
            vehiculoService.update(matricula, vehiculo);
            redirectAttributes.addFlashAttribute("successMessage", "Vehículo actualizado correctamente");
            return "redirect:/vehiculos";
        } catch (RuntimeException ex) {
            bindingResult.reject("vehiculo.error", ex.getMessage());
            modelEditForm(model, matricula);
            addPilotosToModel(model);
            return "nuevo-vehiculo";
        }
    }

    @PostMapping("/vehiculos/{matricula}/eliminar")
    public String eliminarVehiculo(@PathVariable String matricula, RedirectAttributes redirectAttributes) {
        if (vehiculoService.findById(matricula) != null) {
            vehiculoService.deleteById(matricula);
            redirectAttributes.addFlashAttribute("successMessage", "Vehículo eliminado correctamente");
        }
        return "redirect:/vehiculos";
    }

    @PostMapping("/findByMarca")
    public String findByMarcaSubmit(@ModelAttribute("searchForm") Vehiculo searchForm, Model model) {
        model.addAttribute("vehiculos", vehiculoService.searchByMarca(searchForm.getMarca()));
        model.addAttribute("searchForm", searchForm);
        return "vehiculos";
    }

    private void modelNewForm(Model model) {
        model.addAttribute("formAction", "/nuevo-vehiculo");
        model.addAttribute("formTitle", "Crear nuevo vehículo");
        model.addAttribute("submitLabel", "Crear vehículo");
    }

    private void modelEditForm(Model model, String matricula) {
        model.addAttribute("formAction", "/vehiculos/" + matricula + "/editar");
        model.addAttribute("formTitle", "Editar vehículo");
        model.addAttribute("submitLabel", "Guardar cambios");
    }

    private void addPilotosToModel(Model model) {
        List<Piloto> pilotos = pilotoRepository.findAll();
        model.addAttribute("pilotos", pilotos);
    }
}
