package com.will1184.springbootdatajpa.app.controller;

import com.will1184.springbootdatajpa.app.model.entity.Cliente;
import com.will1184.springbootdatajpa.app.model.dao.IClienteDao;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
public class ClienteController {
    @Autowired
    private IClienteDao clienteDao;

    @RequestMapping(value = "/")
    public String home(){
        return  "/layout/layout";
    }

    @RequestMapping(value = "/listar", method = RequestMethod.GET)
    public String listar(Model model){
        model.addAttribute("titulo","Listado de Clientes");
        model.addAttribute("clientes",clienteDao.findAll());
        return  "listar";
    }
    @RequestMapping(value="/form")
    public String crear(Map<String, Object> model){
        Cliente cliente = new Cliente();
        model.put("cliente",cliente);
        return "form";
    }
    @RequestMapping(value = "/form",method = {RequestMethod.POST})
    public String guardar(@Valid  Cliente cliente, RedirectAttributes flash, BindingResult result, Model model){
        if (result.hasErrors()){
            model.addAttribute("titulo","Formulario de Cliente");
            return "form";
        }
        clienteDao.save(cliente);
        flash.addAttribute("success","Cliente creado cone exito");
        return "redirect:listar";
    }
    @RequestMapping(value = "/form/{id}")
    public String editar(@PathVariable (value = "id") Long id,Map<String,Object> model,RedirectAttributes flash){
        Cliente cliente = null;
        if (id>0){
            cliente=clienteDao.findOne(id);
            if (cliente==null){
                flash.addAttribute("success","Cliente no existe");
                return "redirect:/listar";
            }
        }else {
            flash.addAttribute("success","El id del cliente no puede ser cero");
            return "redirect:/listar";
        }
        model.put("cliente",cliente);
        model.put("titulo","Editar Cliente");
        flash.addAttribute("success","Cliente editado con exito");
        return "form";
    }
    @RequestMapping(value = "/eliminar/{id}")
    public String eliminar(@PathVariable (value = "id") Long id,RedirectAttributes flash){
        if (id>0){
            clienteDao.delete(id);
        }
        flash.addAttribute("success","Cliente elimindo con exito");
        return "redirect:/listar";
    }
}
