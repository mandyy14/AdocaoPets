package com.example.pet_service.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.pet_service.model.Pet;

@RestController
@RequestMapping("/pets")
public class PetController {

    // Lista de pets em memória
    private List<Pet> repository = new ArrayList<>();

    // GET - Listar todos os pets 
    @GetMapping
    public List<Pet> index() {
        return repository;
    }

    // POST - Cadastrar um novo pet
    @PostMapping
    public ResponseEntity<Pet> create(@RequestBody Pet pet) {
        pet.setId((long) (repository.size() + 1)); // Simula um ID único
        repository.add(pet);
        return ResponseEntity.status(201).body(pet);
    }

    // GET - Buscar um pet por ID
    @GetMapping("/{id}")
    public ResponseEntity<Pet> get(@PathVariable Long id) {
        return repository.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(404).build());
    }

    // PUT - Atualizar um pet existente
    @PutMapping("/{id}")
    public ResponseEntity<Pet> update(@PathVariable Long id, @RequestBody Pet petAtualizado) {
        for (int i = 0; i < repository.size(); i++) {
            Pet petExistente = repository.get(i);
            if (petExistente.getId().equals(id)) {
                petAtualizado.setId(id); // Garante que o ID seja mantido
                repository.set(i, petAtualizado);
                return ResponseEntity.ok(petAtualizado);
            }
        }
        return ResponseEntity.status(404).build();
    }

    // DELETE - Remover um pet
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean removed = repository.removeIf(p -> p.getId().equals(id));
        return removed ? ResponseEntity.noContent().build() : ResponseEntity.status(404).build();
    }
}
