package br.com.fiapaoj.entregaservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("entregaService")
public class EntregaAPI {
	
	@Autowired
	private EntregaRespository entregaRespository;
	
	@Autowired
	private KafkaTemplate<String, EntregaSlaDto> kafkaTemplate;
	
	@GetMapping(path = "/consultarPrazoEntrega/{UF}", produces = MediaType.APPLICATION_JSON_VALUE)
	public int getPrazoEntrega(@PathVariable(name = "UF") String uf) {
		Entrega e = entregaRespository.findOneByUfIgnoreCase(uf).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		
		return e.getSla();
	}

	@PostMapping(path = "/alterarPrazoEntrega/{UF}/{prazoEntrega}", produces = MediaType.APPLICATION_JSON_VALUE)
	public Entrega setPrazoEntrega(@PathVariable(name = "UF") String uf,
			@PathVariable(name = "prazoEntrega") int prazoEntrega) {
		
		Entrega e = entregaRespository.findOneByUfIgnoreCase(uf).orElse(new Entrega());
		
		if(StringUtils.isEmpty(e.getUf())) {
			e.setUf(uf);
		}
		
		e.setSla(prazoEntrega);
		
		e = entregaRespository.save(e);
		
		EntregaSlaDto entregaSlaDto = new EntregaSlaDto();
		entregaSlaDto.setPrazo(e.getSla());
		entregaSlaDto.setUf(e.getUf());
		
		kafkaTemplate.send(KafkaConfig.TOPICO, entregaSlaDto);
		return e;
	}
	
}
