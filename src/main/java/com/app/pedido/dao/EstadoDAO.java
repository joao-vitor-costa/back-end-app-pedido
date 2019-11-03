package com.app.pedido.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.pedido.model.Estado;

@Repository
public interface EstadoDAO extends JpaRepository<Estado, Integer> {

}
