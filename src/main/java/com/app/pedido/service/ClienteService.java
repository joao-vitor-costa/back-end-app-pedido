package com.app.pedido.service;

import java.awt.image.BufferedImage;
import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.app.pedido.dao.ClienteDAO;
import com.app.pedido.dao.EnderecoDAO;
import com.app.pedido.dto.ClienteDTO;
import com.app.pedido.dto.ClienteNewDTO;
import com.app.pedido.model.Cidade;
import com.app.pedido.model.Cliente;
import com.app.pedido.model.Endereco;
import com.app.pedido.model.enums.Perfil;
import com.app.pedido.model.enums.TipoCliente;
import com.app.pedido.security.UserSS;
import com.app.pedido.service.exception.AuthorizationException;
import com.app.pedido.service.exception.DataIntegrityException;
import com.app.pedido.service.exception.ObjectNotFoundException;

@Service
public class ClienteService {

	@Autowired
	private ClienteDAO clienteDAO;

	@Autowired
	private EnderecoDAO enderecoDAO;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	private S3Service s3Service;

	@Autowired
	private ImageService imageService;

	@Value("${img.prefix.client.profile}")
	private String prefix;

	@Value("${img.profile.size}")
	private Integer size;

	public Cliente findById(Integer id) {

		UserSS user = UserService.authenticated();
		if (user == null || !user.hasRole(Perfil.ADMIN) && !id.equals(user.getId())) {
			throw new AuthorizationException("Acesso negado");
		}

		Optional<Cliente> obj = clienteDAO.findById(id);
		return obj.orElseThrow(() -> new ObjectNotFoundException(
				"Objeto não encontrado! " + "Id:" + id + " " + "Tipo:" + Cliente.class.getName()));
	}

	@Transactional(rollbackFor = Exception.class)
	public Cliente insert(Cliente obj) {
		obj.setId(null);
		enderecoDAO.saveAll(obj.getEnderecos());
		return clienteDAO.save(obj);
	}

	@Transactional(rollbackFor = Exception.class)
	public Cliente update(Cliente obj) {
		Cliente newObj = findById(obj.getId());
		updateData(newObj, obj);
		return clienteDAO.save(newObj);
	}

	@Transactional(rollbackFor = Exception.class)
	public void delete(Integer id) {
		findById(id);
		try {
			clienteDAO.deleteById(id);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityException("Não é possivel excluir porque há Pedidos relacionados.");
		}

	}

	public List<Cliente> findAll() {
		return clienteDAO.findAll();
	}

	public Page<Cliente> findPage(Integer page, Integer linesPerPage, String orderBy, String direction) {
		PageRequest pageRequest = PageRequest.of(page, linesPerPage, Direction.valueOf(direction), orderBy);
		return clienteDAO.findAll(pageRequest);
	}

	public Cliente fromDTO(ClienteDTO objDto) {
		return new Cliente(objDto.getId(), objDto.getNome(), objDto.getEmail(), null, null, null);
	}

	public Cliente fromDTO(ClienteNewDTO objDto) {
		Cliente cliente = new Cliente(null, objDto.getNome(), objDto.getEmail(), objDto.getCpfOuCnpj(),
				TipoCliente.toEnum(objDto.getTipoCliente()), bCryptPasswordEncoder.encode(objDto.getSenha()));
		Cidade cidade = new Cidade(objDto.getCidadeId());
		Endereco endereco = new Endereco(null, objDto.getLogradouro(), objDto.getNumero(), objDto.getComplmento(),
				objDto.getBairro(), objDto.getCep(), cliente, cidade);
		cliente.getEnderecos().add(endereco);

		if (objDto.getTelefone1() != null) {
			cliente.getTelefones().add(objDto.getTelefone1());
		}

		if (objDto.getTelefone2() != null) {
			cliente.getTelefones().add(objDto.getTelefone2());
		}

		if (objDto.getTelefone3() != null) {
			cliente.getTelefones().add(objDto.getTelefone3());
		}

		return cliente;

	}

	private void updateData(Cliente newObj, Cliente obj) {
		newObj.setNome(obj.getNome());
		newObj.setEmail(obj.getEmail());
	}

	public URI uploadProfilePicture(MultipartFile multipartFile) {
		UserSS user = UserService.authenticated();
		if (user == null) {
			throw new AuthorizationException("Acesso negado");
		}

		BufferedImage jpgImage = imageService.getJpgImageFromFile(multipartFile);
		jpgImage = imageService.cropSquare(jpgImage);
		jpgImage = imageService.resize(jpgImage, size);

		String fileName = prefix + user.getId() + ".jpg";

		return s3Service.uploadFile(imageService.getInputStream(jpgImage, "jpg"), fileName, "image");
	}

}
