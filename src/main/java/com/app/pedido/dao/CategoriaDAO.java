package com.app.pedido.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.pedido.model.Categoria;

@Repository
public interface CategoriaDAO extends JpaRepository<Categoria, Integer> {

}
