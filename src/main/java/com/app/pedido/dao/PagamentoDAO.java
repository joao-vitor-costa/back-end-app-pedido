package com.app.pedido.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.pedido.model.Pagamento;

@Repository
public interface PagamentoDAO extends JpaRepository<Pagamento, Integer> {

}
