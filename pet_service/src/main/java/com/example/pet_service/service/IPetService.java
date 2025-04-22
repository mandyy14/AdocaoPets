package com.example.pet_service.service;

import com.example.pet_service.dto.PetFilterCriteria;
import com.example.pet_service.model.Pet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IPetService {

    /**
     * Lista pets disponíveis para adoção, aplicando filtros, paginação e ordenação.
     * @param criteria Objeto DTO contendo os critérios de filtro opcionais.
     * @param pageable Objeto contendo informações de paginação (página, tamanho) e ordenação (campo, direção).
     * @return Uma Page contendo a lista de Pets da página atual e metadados de paginação.
     */
    Page<Pet> listAvailablePets(PetFilterCriteria criteria, Pageable pageable);

    // TODO: Adicionar métodos do serviço (findById, createPet, updatePet)
    
}
