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

@Service
public class PetService implements IPetService {

    private final PetRepository petRepository;

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
             // TODO: Criar PetSpecifications.racaContains(criteria.getRaca())
             // spec = spec.and(PetSpecifications.racaContains(criteria.getRaca()));
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
              // TODO: Criar PetSpecifications.cidadeContains(criteria.getCidade())
              // spec = spec.and(PetSpecifications.cidadeContains(criteria.getCidade()));
          }
         if (criteria.getEstado() != null && !criteria.getEstado().trim().isEmpty()) {
              // TODO: Criar PetSpecifications.estadoEquals(criteria.getEstado())
              // spec = spec.and(PetSpecifications.estadoEquals(criteria.getEstado()));
          }

        return petRepository.findAll(spec, pageable);
    }

    // TODO: Implementar métodos (findById, create, update)
}
