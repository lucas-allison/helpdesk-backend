package com.lucas.helpdesk.services;

import com.lucas.helpdesk.domain.Cliente;
import com.lucas.helpdesk.domain.Pessoa;
import com.lucas.helpdesk.dtos.ClienteDTO;
import com.lucas.helpdesk.repositories.ClienteRepository;
import com.lucas.helpdesk.repositories.PessoaRepository;
import com.lucas.helpdesk.services.exceptions.DataIntegrityViolationException;
import com.lucas.helpdesk.services.exceptions.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

	@Autowired
	private ClienteRepository repository;
	@Autowired
	private PessoaRepository pessoaRepository;
	@Autowired
	private BCryptPasswordEncoder encoder;

	public Cliente findById(Integer id) {
		Optional<Cliente> obj = repository.findById(id);
		return obj.orElseThrow(() -> new ObjectNotFoundException("Objeto não encontrado! Id: " + id));
	}

	public List<Cliente> findAll() {
		return repository.findAll();
	}

	public Cliente create(ClienteDTO requestModel) {
		requestModel.setId(null);
		requestModel.setSenha(encoder.encode(requestModel.getSenha()));
		validaPorCpfEEmail(requestModel);
		Cliente newObj = new Cliente(requestModel);
		return repository.save(newObj);
	}

	public Cliente update(Integer id, ClienteDTO requestModel) {
		requestModel.setId(id);
		Cliente oldObj = findById(id);

		if (!requestModel.getSenha().equals(requestModel.getSenha()))
			requestModel.setSenha(encoder.encode(requestModel.getSenha()));

		validaPorCpfEEmail(requestModel);
		oldObj = new Cliente(requestModel);
		return repository.save(oldObj);
	}

	public void delete(Integer id) {
		Cliente obj = findById(id);

		if (obj.getChamados().size() > 0)
			throw new DataIntegrityViolationException("Técnico possui ordens de serviço e não pode ser deletado!");

		repository.deleteById(id);
	}

	private void validaPorCpfEEmail(ClienteDTO objDTO) {
		Optional<Pessoa> obj = pessoaRepository.findByCpf(objDTO.getCpf());

		if (obj.isPresent() && obj.get().getId() != objDTO.getId())
			throw new DataIntegrityViolationException("CPF já cadastrado no sistema!");

		obj = pessoaRepository.findByEmail(objDTO.getEmail());

		if (obj.isPresent() && obj.get().getId() != objDTO.getId())
			throw new DataIntegrityViolationException("E-mail já cadastrado no sistema!");
	}
}
