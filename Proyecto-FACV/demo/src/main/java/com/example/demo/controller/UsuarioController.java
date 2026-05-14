package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.enums.RolUsuario;
import com.example.demo.model.Administrador;
import com.example.demo.model.Observador;
import com.example.demo.model.Organizador;
import com.example.demo.model.Piloto;
import com.example.demo.model.Tecnico;
import com.example.demo.model.Usuario;
import com.example.demo.service.UsuarioService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/usuarios")
    public String usuariosRedirect() {
        return "redirect:/admin/usuarios";
    }

    // ── Listado ──────────────────────────────────────────────────────────────────

    @GetMapping("/admin/usuarios")
    public String adminListar(
            @RequestParam(required = false) String busqueda,
            @RequestParam(required = false) String rol,
            Model model) {
        log.info("UsuarioController - Admin: listado de usuarios");
        var lista = usuarioService.findAll();

        if (rol != null && !rol.isBlank()) {
            try {
                RolUsuario rolEnum = RolUsuario.valueOf(rol);
                lista = lista.stream().filter(u -> u.getRol() == rolEnum).toList();
            } catch (IllegalArgumentException ignored) {}
        }
        if (busqueda != null && !busqueda.isBlank()) {
            String b = busqueda.toLowerCase();
            lista = lista.stream()
                    .filter(u -> u.getNombre().toLowerCase().contains(b)
                              || u.getApellidos().toLowerCase().contains(b)
                              || (u.getEmail() != null && u.getEmail().toLowerCase().contains(b))
                              || (u.getLicencia() != null && u.getLicencia().toLowerCase().contains(b)))
                    .toList();
        }

        model.addAttribute("usuarios", lista);
        model.addAttribute("roles", RolUsuario.values());
        model.addAttribute("busqueda", busqueda);
        model.addAttribute("rolFiltro", rol);
        return "admin/usuarios";
    }

    // ── Crear ────────────────────────────────────────────────────────────────────

    @GetMapping("/admin/usuarios/nuevo")
    public String adminNuevoForm(Model model) {
        model.addAttribute("usuario", null);
        model.addAttribute("roles", RolUsuario.values());
        model.addAttribute("formTitle", "Nuevo usuario");
        model.addAttribute("formAction", "/admin/usuarios/nuevo");
        model.addAttribute("submitLabel", "Crear usuario");
        return "admin/nuevo-usuario";
    }

    @PostMapping("/admin/usuarios/nuevo")
    public String adminNuevoSubmit(
            @RequestParam String licencia,
            @RequestParam String nombre,
            @RequestParam String apellidos,
            @RequestParam String email,
            @RequestParam(required = false) String fechaNacimiento,
            @RequestParam(required = false) String telefono,
            @RequestParam(required = false) String localidad,
            @RequestParam String rol,
            @RequestParam String rawPassword,
            @RequestParam(required = false) String federacion,
            @RequestParam(required = false) Boolean presidenteFacv,
            @RequestParam(required = false) Byte experiencia,
            @RequestParam(required = false) String club,
            @RequestParam(required = false) Integer carrerasGanadas,
            @RequestParam(required = false) Byte nivelTecnico,
            @RequestParam(required = false) String descripcion,
            RedirectAttributes redirectAttributes) {
        try {
            RolUsuario rolEnum = RolUsuario.valueOf(rol);
            usuarioService.crear(licencia, nombre, apellidos, email, fechaNacimiento,
                    telefono, localidad, rolEnum, rawPassword,
                    federacion, presidenteFacv, experiencia,
                    club, carrerasGanadas, nivelTecnico, descripcion);
            redirectAttributes.addFlashAttribute("successMessage", "Usuario creado correctamente");
            return "redirect:/admin/usuarios";
        } catch (Exception ex) {
            log.error("UsuarioController - Error al crear usuario: {}", ex.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Error al crear usuario: " + ex.getMessage());
            return "redirect:/admin/usuarios/nuevo";
        }
    }

    // ── Editar ───────────────────────────────────────────────────────────────────

    @GetMapping("/admin/usuarios/{licencia}/editar")
    public String adminEditarForm(@PathVariable String licencia, Model model) {
        Usuario usuario = usuarioService.findById(licencia);
        if (usuario == null) return "redirect:/admin/usuarios";
        model.addAttribute("usuario", usuario);
        model.addAttribute("roles", RolUsuario.values());
        model.addAttribute("formTitle", "Editar usuario");
        model.addAttribute("formAction", "/admin/usuarios/" + licencia + "/editar");
        model.addAttribute("submitLabel", "Guardar cambios");
        if (usuario instanceof Observador obs)      model.addAttribute("rolFederacion", obs.getFederacion());
        if (usuario instanceof Organizador org)     model.addAttribute("rolClub", org.getClub());
        if (usuario instanceof Piloto p)            { model.addAttribute("rolClub", p.getClub()); model.addAttribute("rolCarrerasGanadas", p.getCarrerasGanadas()); }
        if (usuario instanceof Tecnico t)           { model.addAttribute("rolNivelTecnico", t.getNivelTecnico()); model.addAttribute("rolDescripcion", t.getDescripcion()); }
        if (usuario instanceof Administrador a)     { model.addAttribute("rolPresidenteFacv", a.getPresidenteFacv()); model.addAttribute("rolExperiencia", a.getExperiencia()); }
        return "admin/nuevo-usuario";
    }

    @PostMapping("/admin/usuarios/{licencia}/editar")
    public String adminEditarSubmit(
            @PathVariable String licencia,
            @RequestParam String nombre,
            @RequestParam String apellidos,
            @RequestParam String email,
            @RequestParam(required = false) String fechaNacimiento,
            @RequestParam(required = false) String telefono,
            @RequestParam(required = false) String localidad,
            @RequestParam(required = false) String rawPassword,
            @RequestParam(required = false) String federacion,
            @RequestParam(required = false) Boolean presidenteFacv,
            @RequestParam(required = false) Byte experiencia,
            @RequestParam(required = false) String club,
            @RequestParam(required = false) Integer carrerasGanadas,
            @RequestParam(required = false) Byte nivelTecnico,
            @RequestParam(required = false) String descripcion,
            RedirectAttributes redirectAttributes) {
        try {
            var result = usuarioService.actualizar(licencia, nombre, apellidos, email,
                    fechaNacimiento, telefono, localidad, rawPassword,
                    federacion, presidenteFacv, experiencia,
                    club, carrerasGanadas, nivelTecnico, descripcion);
            if (result == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Usuario no encontrado");
            } else {
                redirectAttributes.addFlashAttribute("successMessage", "Usuario actualizado correctamente");
            }
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al actualizar: " + ex.getMessage());
        }
        return "redirect:/admin/usuarios";
    }

    // ── Eliminar ─────────────────────────────────────────────────────────────────

    @PostMapping("/admin/usuarios/{licencia}/eliminar")
    public String adminEliminar(@PathVariable String licencia, RedirectAttributes redirectAttributes) {
        try {
            usuarioService.deleteById(licencia);
            redirectAttributes.addFlashAttribute("successMessage", "Usuario eliminado correctamente");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al eliminar: " + ex.getMessage());
        }
        return "redirect:/admin/usuarios";
    }
}
