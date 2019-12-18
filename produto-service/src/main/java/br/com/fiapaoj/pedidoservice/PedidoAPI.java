package br.com.fiapaoj.pedidoservice;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("pedidoService")
public class PedidoAPI {

	@Autowired
	private PedidoRepository pedidoRepository;

	@Autowired
	private ConsumidorEntrega entregaService;

//	@PostMapping(path = "/atualizarEntregaDoPedido/{Id}?prazo= {prazo}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@KafkaListener(topics = KafkaConfig.TOPICO)
	public List<Pedido> atualizarEntrega(EntregaSlaDto slaAlterado) {
		List<Pedido> pedidos = pedidoRepository.findAllByUfIgnoreCase(slaAlterado.getUf());

		pedidos.forEach((p) -> {
			LocalDate dtCriacao = p.getDataCriacao();
			LocalDate dtEntrega = dtCriacao.plusDays(slaAlterado.getPrazo());

			p.setDataEntrega(dtEntrega);
		});

		return pedidoRepository.saveAll(pedidos);
	}

	@PostMapping(path = "/criarPedido")
	public Pedido criarPedido(@RequestBody Pedido pedido) {
		Optional<LocalDate> promessaDtCriacao = Optional.ofNullable(pedido.getDataCriacao());

		LocalDate dtCriacao = promessaDtCriacao.orElse(LocalDate.now());

		LocalDate dtEntrega;
		try {
			int dias = entregaService.consultarPrazoEntrega(pedido.getUf());

			dtEntrega = dtCriacao.plusDays(dias);

		} catch (RestClientException e) {
			dtEntrega = pedido.getDataEntrega();
		}
		pedido.setDataEntrega(dtEntrega);

		return pedidoRepository.save(pedido);
	}

	@GetMapping(path = "/listarPedidoPorUF/{UF}", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Pedido> listarPedidosPorUf(@PathVariable(name = "UF") String uf) {
		return pedidoRepository.findAllByUfIgnoreCase(uf);
	}

	@GetMapping(path = "/listarPedidoPorId/{ID}", produces = MediaType.APPLICATION_JSON_VALUE)
	public Pedido listarPedidoPorId(@PathVariable(name = "ID") long id) {
		return pedidoRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
	}

}
