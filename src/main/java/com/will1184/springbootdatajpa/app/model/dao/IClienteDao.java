package com.will1184.springbootdatajpa.app.model.dao;

import com.will1184.springbootdatajpa.app.model.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;


public interface IClienteDao extends JpaRepository<Cliente, Long> {
}
