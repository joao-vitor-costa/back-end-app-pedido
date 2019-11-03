package com.app.pedido.service;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.pedido.dao.ClienteDAO;
import com.app.pedido.dao.ItemPedidoDAO;
import com.app.pedido.dao.PagamentoDAO;
import com.app.pedido.dao.PedidoDAO;
import com.app.pedido.model.Cliente;
import com.app.pedido.model.ItemPedido;
import com.app.pedido.model.PagamentoComBoleto;
import com.app.pedido.model.Pedido;
import com.app.pedido.model.enums.EstadoPagamento;
import com.app.pedido.security.UserSS;
import com.app.pedido.service.exception.AuthorizationException;
import com.app.pedido.service.exception.ObjectNotFoundException;

@Service
public class PedidoService {

	@Autowired
	private PedidoDAO pedidoDAO;

	@Autowired
	private BoletoService boletoService;

	@Autowired
	private PagamentoDAO pagamentoDAO;

	@Autowired
	private ProdutoService produtoService;

	@Autowired
	private ItemPedidoDAO itemPedidoDAO;

	@Autowired
	private ClienteDAO clienteDAO;

	@Autowired
	private IEmailService emailService;

	@Autowired
	private ClienteService clienteService;

	public Pedido findById(Integer id) {
		Optional<Pedido> obj = pedidoDAO.findById(id);
		return obj.orElseThrow(() -> new ObjectNotFoundException(
				"Objeto não encontrado! " + "Id:" + id + " " + "Tipo:" + Pedido.class.getName()));
	}

	@Transactional(rollbackFor = Exception.class)
	public Pedido insert(Pedido obj) {
		obj.setId(null);
		obj.setInstante(new Date());
		obj.setCliente(clienteDAO.findById(obj.getCliente().getId()).get());
		obj.getPagamento().setEstado(EstadoPagamento.PENDENTE);
		obj.getPagamento().setPedido(obj);
		if (obj.getPagamento() instanceof PagamentoComBoleto) {
			PagamentoComBoleto pagto = (PagamentoComBoleto) obj.getPagamento();
			boletoService.preencherPagamentoComBoleto(pagto, obj.getInstante());
		}
		obj = pedidoDAO.save(obj);
		pagamentoDAO.save(obj.getPagamento());
		for (ItemPedido ip : obj.getItens()) {
			ip.setDesconto(0.0);
			ip.setProduto(produtoService.findById(ip.getProduto().getId()));
			ip.setPreco(ip.getProduto().getPreco());
			ip.setPedido(obj);
		}
		itemPedidoDAO.saveAll(obj.getItens());
		emailService.sendOrderConfirmationHtmlEmail(obj);
		return obj;
	}

	public Page<Pedido> findPage(Integer page, Integer linesPerPage, String orderBy, String direction) {
		UserSS user = UserService.authenticated();
		if (user == null) {
			throw new AuthorizationException("Acesso negado");
		}
		PageRequest pageRequest = PageRequest.of(page, linesPerPage, Direction.valueOf(direction), orderBy);
		Cliente cliente = clienteService.findById(user.getId());
		return pedidoDAO.findByCliente(cliente, pageRequest);
	}

}
