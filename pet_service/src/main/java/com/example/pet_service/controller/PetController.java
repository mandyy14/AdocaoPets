package com.example.pet_service.controller;

import com.example.pet_service.dto.PetFilterCriteria;
import com.example.pet_service.dto.PetRequestDTO;
import com.example.pet_service.model.Pet;
import com.example.pet_service.service.IPetService;

import jakarta.validation.Valid;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;

@RestController
@RequestMapping("/api/pets")
@Tag(name = "Pets", description = "Endpoints para gerenciamento e listagem de pets para adoção")
public class PetController {

    private final IPetService petService;

    @Autowired
    public PetController(IPetService petService) {
        this.petService = petService;
    }

    @GetMapping("/disponiveis")
    @Operation(summary = "Listar Pets Disponíveis",
    description = "Retorna uma lista paginada e filtrável de pets disponíveis para adoção. " +
                  "Parâmetros de filtro (nome, especie, idadeMin, idadeMax, etc.), " +
                  "paginação (page, size) e ordenação (sort=campo,direcao) podem ser passados via query string.")
    public ResponseEntity<Page<Pet>> listAvailablePets(
            PetFilterCriteria criteria,
            @ParameterObject Pageable pageable
    ) {
        Page<Pet> petPage = petService.listAvailablePets(criteria, pageable);
        return ResponseEntity.ok(petPage);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar Pet por ID",
               description = "Retorna os detalhes de um pet específico baseado no seu ID.")
    @Parameter(name = "id", description = "ID do pet a ser buscado", required = true, example = "1")
    public ResponseEntity<Pet> getPetById(@PathVariable Long id) {
        return petService.findPetById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Cadastrar Novo Pet",
               description = "Adiciona um novo pet ao sistema para adoção. Requer dados do pet e o identificador da mídia (foto). (Endpoint a ser protegido futuramente)")
    public ResponseEntity<Pet> createPet(@Valid @RequestBody PetRequestDTO petDto) {
        Pet createdPet = petService.createPet(petDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPet);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar Pet Existente",
               description = "Atualiza os dados de um pet existente. (Endpoint a ser protegido futuramente)")
    @Parameter(name = "id", description = "ID do pet a ser atualizado", required = true, example = "1")
    public ResponseEntity<Pet> updatePet(@PathVariable Long id, @Valid @RequestBody PetRequestDTO petDto) {
        return petService.updatePet(id, petDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar Pet por ID",
               description = "Remove um pet do sistema. (Endpoint a ser protegido futuramente)")
    @Parameter(name = "id", description = "ID do pet a ser deletado", required = true, example = "1")
    public ResponseEntity<Void> deletePet(@PathVariable Long id) {
        petService.deletePet(id);
        return ResponseEntity.noContent().build();
    }

}
