package br.com.fiapaoj.pedidoservice;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@Entity
@DynamicInsert
@DynamicUpdate
public class Pedido {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(insertable = false)
	private Long id;

	private String status;

	@JsonFormat(pattern = "dd/MM/yyyy")
	private LocalDate dataCriacao;

	@JsonFormat(pattern = "dd/MM/yyyy")
	private LocalDate dataEntrega;

	private String produto;

	private String endereco;

	@Column(nullable = false)
	@JsonProperty(required = true)
	private String uf;

	@Column(nullable = false)
	private String municipio;

	private String cep;
}
