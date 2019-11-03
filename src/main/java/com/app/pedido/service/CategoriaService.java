package com.app.pedido.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.pedido.dao.CategoriaDAO;
import com.app.pedido.dto.CategoriaDTO;
import com.app.pedido.model.Categoria;
import com.app.pedido.service.exception.DataIntegrityException;
import com.app.pedido.service.exception.ObjectNotFoundException;

@Service
public class CategoriaService {

	@Autowired
	private CategoriaDAO categoriaDAO;

	public Categoria findById(Integer id) {
		Optional<Categoria> obj = categoriaDAO.findById(id);
		return obj.orElseThrow(() -> new ObjectNotFoundException(
				"Objeto não encontrado! " + "Id:" + id + " " + "Tipo:" + Categoria.class.getName()));
	}

	@Transactional(rollbackFor = Exception.class)
	public Categoria insert(Categoria obj) {
		obj.setId(null);
		return categoriaDAO.save(obj);
	}

	@Transactional(rollbackFor = Exception.class)
	public Categoria update(Categoria obj) {
		Categoria newObj = findById(obj.getId());
		updateData(newObj, obj);
		return categoriaDAO.save(newObj);
	}

	@Transactional(rollbackFor = Exception.class)
	public void delete(Integer id) {
		findById(id);
		try {
			categoriaDAO.deleteById(id);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityException("Não é possivel excluir uma categoria que possui produtos.");
		}

	}

	public List<Categoria> findAll() {
		return categoriaDAO.findAll();
	}

	public Page<Categoria> findPage(Integer page, Integer linesPerPage, String orderBy, String direction) {
		PageRequest pageRequest = PageRequest.of(page, linesPerPage, Direction.valueOf(direction), orderBy);
		return categoriaDAO.findAll(pageRequest);
	}

	public Categoria fromDTO(CategoriaDTO objDto) {
		return new Categoria(objDto.getId(), objDto.getNome());
	}

	private void updateData(Categoria newObj, Categoria obj) {
		newObj.setNome(obj.getNome());

	}

}
