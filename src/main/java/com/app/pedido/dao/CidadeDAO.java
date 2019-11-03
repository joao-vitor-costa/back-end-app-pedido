package com.app.pedido.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.pedido.model.Cidade;

@Repository
public interface CidadeDAO extends JpaRepository<Cidade, Integer> {

}
