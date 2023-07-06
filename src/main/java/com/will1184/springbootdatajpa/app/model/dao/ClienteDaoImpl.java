package com.will1184.springbootdatajpa.app.model.dao;

import com.will1184.springbootdatajpa.app.entity.Cliente;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository("clienteDaoJPA")
public class ClienteDaoImpl implements IClienteDao{
    @PersistenceContext
    private EntityManager manager;

    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true)
    @Override
    public List<Cliente> findAll() {
        return manager.createQuery("FROM Cliente ").getResultList();
    }

    @Override
    @Transactional
    public void save(Cliente cliente) {
        if (cliente.getId()!=null && cliente.getId()>0){
            manager.merge(cliente);
        }else {
            manager.merge(cliente);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public Cliente findOne(Long id) {
        return manager.find(Cliente.class,id);
    }

    @Transactional()
    @Override
    public void delete(Long id) {
        Cliente cliente = findOne(id);
        manager.remove(cliente);
    }
}
