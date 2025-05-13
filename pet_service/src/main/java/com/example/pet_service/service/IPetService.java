package com.example.pet_service.service;

import com.example.pet_service.dto.PetFilterCriteria;
import com.example.pet_service.dto.PetRequestDTO;
import com.example.pet_service.model.Pet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

public interface IPetService {
    Page<Pet> listAvailablePets(PetFilterCriteria criteria, Pageable pageable);
    Optional<Pet> findPetById(Long id);
    Pet createPet(PetRequestDTO petDto);
    Optional<Pet> updatePet(Long id, PetRequestDTO petDto);
    void deletePet(Long id);
}
