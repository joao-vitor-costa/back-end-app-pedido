package com.app.pedido.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.app.pedido.model.Categoria;
import com.app.pedido.model.Produto;

@Repository
public interface ProdutoDAO extends JpaRepository<Produto, Integer> {

	/*
	 * Usando JPQL
	 * 
	 * @Transactional(readOnly = true)
	 * 
	 * @Query("SELECT DISTINCT obj FROM Produto obj INNER JOIN obj.categorias cat WHERE obj.nome LIKE %:nome% AND cat IN :categorias"
	 * ) Page<Produto> search(@Param("nome") String nome, @Param("categorias")
	 * List<Categoria> categorias, Pageable pageable);
	 */

	/* Usando o Spring */
	@Transactional(readOnly = true)
	Page<Produto> findDistinctByNomeContainingAndCategoriasIn(String nome, List<Categoria> categorias,
			Pageable pageRequest);

}
