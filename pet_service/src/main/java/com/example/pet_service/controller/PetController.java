package com.example.pet_service.controller;

import com.example.pet_service.dto.PetFilterCriteria;
import com.example.pet_service.model.Pet;
import com.example.pet_service.service.IPetService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pets")
public class PetController {

    private final IPetService petService;

    @Autowired
    public PetController(IPetService petService) {
        this.petService = petService;
    }

    /**
     * Endpoint para listar pets disponíveis com filtros, paginação e ordenação.
     * Filtros: passados como query parameters (ex: ?nome=Bob&especie=CACHORRO)
     * Paginação: query parameters 'page' (número da página, 0-based) e 'size' (itens por página).
     * Ordenação: query parameter 'sort' (ex: sort=nome,asc ou sort=idade,desc). Múltiplos sorts: sort=especie,asc&sort=nome,desc
     * @param criteria DTO que agrupa os parâmetros de filtro (Spring mapeia automaticamente os query params para os campos do DTO).
     * @param pageable Objeto que agrupa os parâmetros de paginação e ordenação (Spring cria automaticamente). @ParameterObject ajuda na documentação.
     * @return ResponseEntity contendo a Page<Pet> com os resultados e metadados de paginação.
     */
    @GetMapping("/disponiveis") 
    public ResponseEntity<Page<Pet>> listAvailablePets(
            PetFilterCriteria criteria,
            @ParameterObject Pageable pageable
    ) {
        Page<Pet> petPage = petService.listAvailablePets(criteria, pageable);
        return ResponseEntity.ok(petPage);
    }

    // TODO: Adicionar endpoints GET /api/pets/{id}, POST /api/pets, PUT /api/pets/{id}, DELETE /api/pets/{id}
}
