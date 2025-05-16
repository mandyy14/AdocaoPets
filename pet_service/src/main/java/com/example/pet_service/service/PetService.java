package com.example.pet_service.service;

import com.example.pet_service.dto.PetFilterCriteria;
import com.example.pet_service.model.Pet;
import com.example.pet_service.repository.PetRepository;
import com.example.pet_service.repository.PetSpecifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.pet_service.dto.PetRequestDTO;
import com.example.pet_service.exceptions.PetNaoEncontradoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Optional;

@Service
public class PetService implements IPetService {

    private final PetRepository petRepository;
    private static final Logger logger = LoggerFactory.getLogger(PetService.class);

    @Autowired
    public PetService(PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Pet> listAvailablePets(PetFilterCriteria criteria, Pageable pageable) {

        // condição base: buscar apenas pets não adotados
        Specification<Pet> spec = Specification.where(PetSpecifications.isNotAdopted());

        if (criteria.getNome() != null && !criteria.getNome().trim().isEmpty()) {
            spec = spec.and(PetSpecifications.nomeContains(criteria.getNome()));
        }

        if (criteria.getEspecie() != null && !criteria.getEspecie().trim().isEmpty()) {
            spec = spec.and(PetSpecifications.especieEquals(criteria.getEspecie()));
        }

        if (criteria.getRaca() != null && !criteria.getRaca().trim().isEmpty()) {
            spec = spec.and(PetSpecifications.racaContains(criteria.getRaca()));
        }

        if (criteria.getIdadeMin() != null) {
            spec = spec.and(PetSpecifications.idadeGreaterThanOrEqual(criteria.getIdadeMin()));
        }

        if (criteria.getIdadeMax() != null) {
            spec = spec.and(PetSpecifications.idadeLessThanOrEqual(criteria.getIdadeMax()));
        }

        if (criteria.getGenero() != null && !criteria.getGenero().trim().isEmpty()) {
            spec = spec.and(PetSpecifications.generoEquals(criteria.getGenero()));
        }

        if (criteria.getPorte() != null && !criteria.getPorte().trim().isEmpty()) {
            spec = spec.and(PetSpecifications.porteEquals(criteria.getPorte()));
        }
        
        if (criteria.getCidade() != null && !criteria.getCidade().trim().isEmpty()) {
            spec = spec.and(PetSpecifications.cidadeContains(criteria.getCidade()));
        }

        if (criteria.getEstado() != null && !criteria.getEstado().trim().isEmpty()) {
            spec = spec.and(PetSpecifications.estadoEquals(criteria.getEstado()));
        }

        return petRepository.findAll(spec, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Pet> findPetById(Long id) {
        logger.debug("Buscando pet com ID: {}", id);
        return petRepository.findById(id);
    }

    @Override
    @Transactional
    public Pet createPet(PetRequestDTO petDto) {
        logger.info("Criando novo pet com nome: {}", petDto.getNome());
        Pet pet = new Pet();
        pet.setNome(petDto.getNome());
        pet.setEspecie(petDto.getEspecie());
        pet.setRaca(petDto.getRaca());
        pet.setIdade(petDto.getIdade());
        pet.setGenero(petDto.getGenero());
        pet.setPorte(petDto.getPorte());
        pet.setDescricao(petDto.getDescricao());
        pet.setCidade(petDto.getCidade());
        pet.setEstado(petDto.getEstado());
        pet.setMediaIdentifier(petDto.getMediaIdentifier());
        pet.setAdotado(false);

        return petRepository.save(pet);
    }

    @Override
    @Transactional
    public Optional<Pet> updatePet(Long id, PetRequestDTO petDto) {
        logger.info("Atualizando pet com ID: {}", id);
        return petRepository.findById(id).map(existingPet -> {
            existingPet.setNome(petDto.getNome());
            existingPet.setEspecie(petDto.getEspecie());
            existingPet.setRaca(petDto.getRaca());
            existingPet.setIdade(petDto.getIdade());
            existingPet.setGenero(petDto.getGenero());
            existingPet.setPorte(petDto.getPorte());
            existingPet.setDescricao(petDto.getDescricao());
            existingPet.setCidade(petDto.getCidade());
            existingPet.setEstado(petDto.getEstado());
            existingPet.setMediaIdentifier(petDto.getMediaIdentifier());
            return petRepository.save(existingPet);
        });
    }

    @Override
    @Transactional
    public void deletePet(Long id) {
        logger.info("Deletando pet com ID: {}", id);
        if (!petRepository.existsById(id)) {
            throw new PetNaoEncontradoException("Pet com ID " + id + " não encontrado para deleção.");
        }
        petRepository.deleteById(id);
    }
    
}
