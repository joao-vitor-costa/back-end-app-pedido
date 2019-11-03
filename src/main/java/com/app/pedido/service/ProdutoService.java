package com.app.pedido.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.app.pedido.dao.CategoriaDAO;
import com.app.pedido.dao.ProdutoDAO;
import com.app.pedido.model.Categoria;
import com.app.pedido.model.Produto;
import com.app.pedido.service.exception.ObjectNotFoundException;

@Service
public class ProdutoService {

	@Autowired
	private ProdutoDAO produtoDAO;

	@Autowired
	private CategoriaDAO categoriaDAO;

	public Produto findById(Integer id) {
		Optional<Produto> obj = produtoDAO.findById(id);
		return obj.orElseThrow(() -> new ObjectNotFoundException(
				"Objeto não encontrado! " + "Id:" + id + " " + "Tipo:" + Produto.class.getName()));
	}

	public Page<Produto> search(String nome, List<Integer> ids, Integer page, Integer linesPerPage, String orderBy,
			String direction) {
		PageRequest pageRequest = PageRequest.of(page, linesPerPage, Direction.valueOf(direction), orderBy);
		List<Categoria> categorias = categoriaDAO.findAllById(ids);
		return produtoDAO.findDistinctByNomeContainingAndCategoriasIn(nome, categorias, pageRequest);
	}

}
