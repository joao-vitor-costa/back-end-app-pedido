package com.app.pedido.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.pedido.model.Endereco;

@Repository
public interface EnderecoDAO extends JpaRepository<Endereco, Integer> {

}
