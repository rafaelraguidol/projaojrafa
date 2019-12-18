package br.com.fiapaoj.entregaservice;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EntregaRespository extends JpaRepository<Entrega, Long>{

	Optional<Entrega> findOneByUfIgnoreCase(String uf);
}
