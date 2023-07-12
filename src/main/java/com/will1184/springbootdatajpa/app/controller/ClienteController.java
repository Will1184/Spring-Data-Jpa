package com.will1184.springbootdatajpa.app.controller;

import com.will1184.springbootdatajpa.app.model.entity.Cliente;
import com.will1184.springbootdatajpa.app.model.service.IClienteService;
import com.will1184.springbootdatajpa.app.util.paginable.PageRender;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@Controller
@SessionAttributes("cliente")
public class ClienteController {
    @Autowired
    private IClienteService clienteService;

    @GetMapping(value = "/ver/{id}")
    public String ver(@PathVariable(value = "id") Long id, Map<String,Object> model,
                      RedirectAttributes flash){
        Cliente cliente = clienteService.findOne(id);
        if(cliente==null){
            flash.addFlashAttribute("error","El cliente no existe");
            return "redirect:/listar";
        }
        model.put("cliente",cliente);
        model.put("Titulo","Detalle Cliente: "+cliente.getNombre());
        return "ver";
    }
    @GetMapping(value = "/listar")
    public String listar(@RequestParam(name = "page", defaultValue = "0") int page, Model model){
        Pageable pageRequest = PageRequest.of(page, 4);
        Page<Cliente> clientes = clienteService.findAll(pageRequest);

        PageRender<Cliente> pageRender = new PageRender<>("/listar", clientes);
        model.addAttribute("titulo","Listado de Clientes");
        model.addAttribute("clientes", clientes);
        model.addAttribute("page", pageRender);
        return "listar";
    }
    @RequestMapping(value = "/form") //templates es raiz
    public String crear(Map<String, Object>model){
        Cliente cliente = new Cliente();
        model.put("cliente",cliente);
        model.put("titulo", "Formulario de Cliente");
        return "form";
    }
    @PostMapping(value = "/form")
    public String guardar(@Valid Cliente cliente, BindingResult result, Model model, RedirectAttributes flash,
                          SessionStatus status, @RequestParam("file")MultipartFile foto){
        if (result.hasErrors()){
            model.addAttribute("titulo","Formulario de Cliente");
            return "form";
        }
        if (!foto.isEmpty()){
            String rootPath ="C://Temp//uploads";
            try {
                byte[] bytes=foto.getBytes();
                Path rutaCompleta= Paths.get(rootPath+"//"+foto.getOriginalFilename());
                Files.write(rutaCompleta,bytes);
                flash.addAttribute("info","Se ha subido correctamente '"
                        +foto.getOriginalFilename()+"'");
                cliente.setFoto(foto.getOriginalFilename());
            } catch (IOException e) {
//                throw new RuntimeException(e);
                e.printStackTrace();
            }
        }
        String mensajeFlash=(cliente.getId() != null)? "Cliente editado con éxito": "Cliente creado con exito";

        this.clienteService.save(cliente);
        status.setComplete();
        flash.addFlashAttribute("success", mensajeFlash);
        return "redirect:listar";
    }

    @RequestMapping(value = "/form/{id}") // esto es para buscar una propiedad en especifico
    public String editar(@PathVariable(value = "id") Long id, Map<String,Object> model, RedirectAttributes flash){
        Cliente cliente;
        if (id>0){
            cliente=clienteService.findOne(id);
            if (cliente==null){
                flash.addFlashAttribute("error","El ID del Cliente no existe en la Base de Datos" );
                return "redirect:/listar";
            }
        }else { flash.addFlashAttribute("error","El id del Cliente no puede ser cero" );
            return "redirect:/listar";
        }
        model.put("cliente",cliente);
        model.put("titulo","Editar Cliente");
        return "form";
    }
    @RequestMapping(value = "eliminar/{id}")
    public String eliminar(@PathVariable(value = "id") Long id , RedirectAttributes flash) {
        if (id > 0) {
            clienteService.delete(id);
            flash.addFlashAttribute("success", "Cliente Eliminado con Exito");
        }
        return "redirect:/listar";
    }
}
