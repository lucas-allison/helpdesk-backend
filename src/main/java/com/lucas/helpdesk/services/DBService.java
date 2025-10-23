package com.lucas.helpdesk.services;

import com.lucas.helpdesk.domain.Chamado;
import com.lucas.helpdesk.domain.Cliente;
import com.lucas.helpdesk.domain.Tecnico;
import com.lucas.helpdesk.domain.enums.Perfil;
import com.lucas.helpdesk.domain.enums.Prioridade;
import com.lucas.helpdesk.domain.enums.Status;
import com.lucas.helpdesk.repositories.ChamadoRepository;
import com.lucas.helpdesk.repositories.ClienteRepository;
import com.lucas.helpdesk.repositories.TecnicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class DBService {
	
	@Autowired
	private TecnicoRepository tecnicoRepository;
	@Autowired
	private ClienteRepository clienteRepository;
	@Autowired
	private ChamadoRepository chamadoRepository;
	@Autowired
	private BCryptPasswordEncoder encoder;
		
	public void instanciaDB() {
		Tecnico tec1 = new Tecnico(null, "Lucas", "010.094.540-64", "teste@teste.com", encoder.encode("123"));
		tec1.addPerfis(Perfil.ADMIN);
		
		Cliente cli1 = new Cliente(null, "Linus Torvalds", "237.061.440-42", "torvalds@mail.com", encoder.encode("123"));
		
		Chamado c1 = new Chamado(null, Prioridade.MEDIA, Status.ANDAMENTO, "Chamado 01", "Primeiro Chamado", tec1, cli1);
		
		tecnicoRepository.saveAll(Arrays.asList(tec1));
		clienteRepository.saveAll(Arrays.asList(cli1));
		chamadoRepository.saveAll(Arrays.asList(c1));	
	}
}
