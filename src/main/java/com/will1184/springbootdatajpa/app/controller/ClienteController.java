package com.will1184.springbootdatajpa.app.controller;

import com.will1184.springbootdatajpa.app.model.entity.Cliente;
import com.will1184.springbootdatajpa.app.model.service.IClienteService;
import com.will1184.springbootdatajpa.app.util.paginable.PageRender;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

@Controller
@SessionAttributes("cliente")
public class ClienteController {

    @Autowired
    private IClienteService clienteService;
    private final static  String UPLOAD_FOLDER="/uploads";
    private final Logger log= LoggerFactory.getLogger(getClass());
    @GetMapping(value = "/upload/{filename:.+}")
    public ResponseEntity<Resource> verFoto(@PathVariable String filename){
        Path pathFoto = Paths.get(UPLOAD_FOLDER).resolve(filename).toAbsolutePath();
        log.info("PathFoto: "+pathFoto);
        Resource resource= null;
        try {
            resource= new UrlResource(pathFoto.toUri());
            if (!resource.exists() && !resource.isReadable()){
                throw  new RuntimeException(
                        "Error: No se puede cargar la imagen "+pathFoto.toString());
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,"attachment;filename=\""
                +resource.getFilename()+"\"").body(resource);
    }

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

            if (cliente.getId()!=null && cliente.getId()>0 &&
                    cliente.getFoto()!=null && cliente.getFoto().length()>0) {
                Path rootPath = Paths.get(UPLOAD_FOLDER).resolve(cliente.getFoto()).toAbsolutePath();
                File archivo = rootPath.toFile();
                if (archivo.exists() && archivo.canRead()) {
                    if (archivo.delete()) {
                        archivo.delete();
                    }

                }
            }
            String uniqueFileName = UUID.randomUUID().toString()+"-"+foto.getOriginalFilename();
            Path rootPath = Paths.get(UPLOAD_FOLDER).resolve(uniqueFileName);
            Path rootAbsolute = rootPath.toAbsolutePath();
            log.info("rootPath: "+rootPath);
            log.info("rootAbsolutePath: "+rootAbsolute);
            try {
                Files.copy(foto.getInputStream(),rootAbsolute);
                flash.addAttribute("info","Se ha subido correctamente '"
                        +uniqueFileName+"'");
                cliente.setFoto(uniqueFileName);
                String mensajeFlash=(cliente.getId() != null)? "Cliente editado con éxito": "Cliente creado con exito";

                this.clienteService.save(cliente);
                status.setComplete();
                flash.addFlashAttribute("success", mensajeFlash);
            } catch (IOException e) {
//                throw new RuntimeException(e);
                e.printStackTrace();
            }
        }

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
            Cliente cliente = clienteService.findOne(id);
            clienteService.delete(id);
            flash.addFlashAttribute("success", "Cliente Eliminado con Exito");
            Path rootPath=Paths.get(UPLOAD_FOLDER).resolve(cliente.getFoto()).toAbsolutePath();
            File archivo = rootPath.toFile();
            if (archivo.exists() && archivo.canRead()){
                if (archivo.delete()){
                    flash.addFlashAttribute("info","Foto eliminada"+cliente.getFoto()+
                            " se elimino correctamente");
                }
            }

        }
        return "redirect:/listar";
    }
}
