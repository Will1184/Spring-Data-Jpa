package com.will1184.springbootdatajpa.app.model.service;

import com.will1184.springbootdatajpa.app.exception.ResourceNotFoundException;
import com.will1184.springbootdatajpa.app.model.dao.IClienteDao;
import com.will1184.springbootdatajpa.app.model.entity.Cliente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClienteServiceImpl implements IClienteService {
    @Autowired
    private IClienteDao clienteDao;

    @Transactional(readOnly = true)
    @Override
    public List<Cliente> findAll() {
        return (List<Cliente>) clienteDao.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public Page<Cliente> findAll(Pageable pageable) {
        return clienteDao.findAll(pageable);
    }

    @Transactional
    @Override
    public void save(Cliente cliente) {
        clienteDao.save(cliente);
    }

    @Transactional(readOnly = true)
    @Override
    public Cliente findOne(Long id) {
        return clienteDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Persona does not exist with id: " + id));
    }

    @Transactional()
    @Override
    public void delete(Long id) {
        Cliente cliente = clienteDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Persona does not exist with id: " + id));

        clienteDao.delete(cliente);
    }
}
