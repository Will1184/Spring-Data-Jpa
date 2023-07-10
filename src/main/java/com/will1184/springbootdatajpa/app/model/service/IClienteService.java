package com.will1184.springbootdatajpa.app.model.service;

import com.will1184.springbootdatajpa.app.model.entity.Cliente;

import java.util.List;

public interface IClienteService {
    public List<Cliente> findAll();
    void save(Cliente cliente);
    Cliente findOne(Long id);
    void delete(Long id);

}
