package com.app.pedido.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.pedido.model.ItemPedido;

@Repository
public interface ItemPedidoDAO extends JpaRepository<ItemPedido, Integer> {

}
