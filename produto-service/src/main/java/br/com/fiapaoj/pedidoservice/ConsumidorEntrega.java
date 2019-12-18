package br.com.fiapaoj.pedidoservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@Configuration
@Component
@RibbonClient(name = "entrega-service", configuration = RibbonConfig.class)
public class ConsumidorEntrega {

	@Autowired
	private RestTemplate restTemplate;

	@LoadBalanced
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@HystrixCommand
	public int consultarPrazoEntrega(String uf) {
		return restTemplate.getForObject("http://entregaService/consultarPrazoEntrega/{UF}", Integer.class, uf);
	}
}
