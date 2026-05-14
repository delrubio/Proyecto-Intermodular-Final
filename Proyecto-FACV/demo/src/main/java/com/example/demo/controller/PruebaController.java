package com.example.demo.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.enums.RolUsuario;
import com.example.demo.model.Organizador;
import com.example.demo.model.Prueba;
import com.example.demo.model.Usuario;
import com.example.demo.repository.OrganizadorRepository;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.service.PruebaService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class PruebaController {

    private final PruebaService pruebaService;
    private final UsuarioRepository usuarioRepository;
    private final OrganizadorRepository organizadorRepository;

    public PruebaController(PruebaService pruebaService,
                            UsuarioRepository usuarioRepository,
                            OrganizadorRepository organizadorRepository) {
        this.pruebaService = pruebaService;
        this.usuarioRepository = usuarioRepository;
        this.organizadorRepository = organizadorRepository;
    }

    @GetMapping("/pruebas")
    public String pruebasView(Model model) {
        log.info("PruebaController - Listado de pruebas");
        model.addAttribute("pruebas", pruebaService.findAll());
        model.addAttribute("searchForm", new Prueba());
        return "pruebas";
    }

    @GetMapping("/nueva-prueba")
    public String nuevaPruebaForm(Model model) {
        model.addAttribute("prueba", new Prueba());
        modelNewForm(model);
        model.addAttribute("organizadores", organizadorRepository.findAll());
        return "nueva-prueba";
    }

    @PostMapping("/nueva-prueba")
    public String nuevaPruebaSubmit(
            @Valid @ModelAttribute("prueba") Prueba prueba,
            BindingResult bindingResult,
            @RequestParam(required = false) String organizadorLicencia,
            RedirectAttributes redirectAttributes,
            Model model,
            Authentication authentication) {
        if (bindingResult.hasErrors()) {
            modelNewForm(model);
            model.addAttribute("organizadores", organizadorRepository.findAll());
            return "nueva-prueba";
        }
        prueba.setNInscritos(0);
        Usuario usuario = usuarioRepository.findByNombre(authentication.getName());
        if (usuario != null && usuario.getRol() == RolUsuario.ORGANIZADOR) {
            prueba.setOrganizador((Organizador) usuario);
        } else if (organizadorLicencia != null && !organizadorLicencia.isBlank()) {
            organizadorRepository.findById(organizadorLicencia).ifPresent(prueba::setOrganizador);
        }
        try {
            pruebaService.save(prueba);
            redirectAttributes.addFlashAttribute("successMessage", "Prueba creada correctamente");
        } catch (Exception ex) {
            log.error("PruebaController - Error al crear prueba: {}", ex.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Error al crear prueba: " + ex.getMessage());
        }
        return "redirect:/pruebas";
    }

    @GetMapping("/pruebas/{id}/editar")
    public String editarPruebaForm(@PathVariable Integer id, Model model) {
        Prueba prueba = pruebaService.findById(id);
        if (prueba == null) return "redirect:/pruebas";
        model.addAttribute("prueba", prueba);
        modelEditForm(model, id);
        model.addAttribute("organizadores", organizadorRepository.findAll());
        return "nueva-prueba";
    }

    @PostMapping("/pruebas/{id}/editar")
    public String editarPruebaSubmit(
            @PathVariable Integer id,
            @Valid @ModelAttribute("prueba") Prueba prueba,
            BindingResult bindingResult,
            @RequestParam(required = false) String organizadorLicencia,
            RedirectAttributes redirectAttributes,
            Model model) {
        Prueba existente = pruebaService.findById(id);
        if (existente == null) return "redirect:/pruebas";
        if (bindingResult.hasErrors()) {
            modelEditForm(model, id);
            model.addAttribute("organizadores", organizadorRepository.findAll());
            return "nueva-prueba";
        }
        prueba.setIdPrueba(id);
        prueba.setNInscritos(existente.getNInscritos());
        if (organizadorLicencia != null && !organizadorLicencia.isBlank()) {
            organizadorRepository.findById(organizadorLicencia).ifPresent(prueba::setOrganizador);
        } else {
            prueba.setOrganizador(existente.getOrganizador());
        }
        try {
            pruebaService.save(prueba);
            redirectAttributes.addFlashAttribute("successMessage", "Prueba actualizada correctamente");
        } catch (Exception ex) {
            log.error("PruebaController - Error al editar prueba: {}", ex.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Error al editar prueba: " + ex.getMessage());
        }
        return "redirect:/pruebas";
    }

    @PostMapping("/pruebas/{id}/eliminar")
    public String eliminarPrueba(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        pruebaService.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Prueba eliminada correctamente");
        return "redirect:/pruebas";
    }

    @PostMapping("/findByTitle")
    public String findByTitleSubmit(@ModelAttribute("searchForm") Prueba searchForm,
                                    @RequestParam(required = false) String nombre,
                                    Model model) {
        model.addAttribute("pruebas", pruebaService.searchByNombre(nombre));
        model.addAttribute("searchForm", searchForm);
        model.addAttribute("organizadores", organizadorRepository.findAll());
        return "pruebas";
    }

    private void modelNewForm(Model model) {
        model.addAttribute("formAction", "/nueva-prueba");
        model.addAttribute("formTitle", "Crear nueva prueba");
        model.addAttribute("submitLabel", "Crear prueba");
    }

    private void modelEditForm(Model model, Integer id) {
        model.addAttribute("formAction", "/pruebas/" + id + "/editar");
        model.addAttribute("formTitle", "Editar prueba");
        model.addAttribute("submitLabel", "Guardar cambios");
    }
}
