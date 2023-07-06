package com.will1184.springbootdatajpa.app.model.dao;

import com.will1184.springbootdatajpa.app.entity.Cliente;

import java.util.List;

public interface IClienteDao {
     List<Cliente> findAll();
     void save(Cliente cliente);
     Cliente findOne(Long id);
     void delete(Long id);
}
