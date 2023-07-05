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
}
