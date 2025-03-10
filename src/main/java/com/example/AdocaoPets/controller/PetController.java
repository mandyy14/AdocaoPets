package com.example.AdocaoPets.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.AdocaoPets.model.Pet;

@RestController
@RequestMapping("/pets")
public class PetController {

     // lista de pets em memoria
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
}
    

