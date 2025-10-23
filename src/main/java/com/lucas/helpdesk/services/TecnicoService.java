package com.lucas.helpdesk.services;

import com.lucas.helpdesk.domain.Pessoa;
import com.lucas.helpdesk.domain.Tecnico;
import com.lucas.helpdesk.dtos.TecnicoDTO;
import com.lucas.helpdesk.repositories.PessoaRepository;
import com.lucas.helpdesk.repositories.TecnicoRepository;
import com.lucas.helpdesk.services.exceptions.DataIntegrityViolationException;
import com.lucas.helpdesk.services.exceptions.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TecnicoService {

	@Autowired
	private TecnicoRepository repository;
	@Autowired
	private PessoaRepository pessoaRepository;
	@Autowired
	private BCryptPasswordEncoder encoder;

	public Tecnico findById(Integer id) {
		Optional<Tecnico> obj = repository.findById(id);
		return obj.orElseThrow(() -> new ObjectNotFoundException("Objeto não encontrado! Id: " + id));
	}

	public List<Tecnico> findAll() {
		return repository.findAll();
	}

	public Tecnico create(TecnicoDTO requestModel) {
		requestModel.setId(null);
		requestModel.setSenha(encoder.encode(requestModel.getSenha()));
		validaPorCpfEEmail(requestModel);
		Tecnico newObj = new Tecnico(requestModel);
		return repository.save(newObj);
	}

	private void validaPorCpfEEmail(TecnicoDTO objDTO) {
		Optional<Pessoa> obj = pessoaRepository.findByCpf(objDTO.getCpf());

		if (obj.isPresent() && obj.get().getId() != objDTO.getId())
			throw new DataIntegrityViolationException("CPF já cadastrado no sistema!");

		obj = pessoaRepository.findByEmail(objDTO.getEmail());

		if (obj.isPresent() && obj.get().getId() != objDTO.getId())
			throw new DataIntegrityViolationException("E-mail já cadastrado no sistema!");
	}

	public Tecnico update(Integer id, TecnicoDTO requestModel) {
		requestModel.setId(id);
		Tecnico oldObj = findById(id);
		validaPorCpfEEmail(requestModel);
		oldObj = new Tecnico(requestModel);
		return repository.save(oldObj);
	}

	public void delete(Integer id) {
		Tecnico obj = findById(id);

		if (obj.getChamados().size() > 0)
			throw new DataIntegrityViolationException("Técnico possui ordens de serviço e não pode ser deletado!");

		repository.deleteById(id);
	}
}
