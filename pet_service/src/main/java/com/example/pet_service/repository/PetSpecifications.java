package com.example.pet_service.repository;

import com.example.pet_service.model.Pet;
import org.springframework.data.jpa.domain.Specification;

public class PetSpecifications {

    // Filtra por parte do nome (case-insensitive)
    public static Specification<Pet> nomeContains(String nome) {
        return (root, query, cb) -> {
            if (nome == null || nome.trim().isEmpty()) {
                return cb.conjunction();
            }
            return cb.like(cb.lower(root.get("nome")), "%" + nome.toLowerCase() + "%");
        };
    }

    // Filtra por espécie (case-insensitive)
    public static Specification<Pet> especieEquals(String especie) {
         return (root, query, cb) -> {
            if (especie == null || especie.trim().isEmpty()) {
                return cb.conjunction();
            }
            return cb.equal(cb.lower(root.get("especie")), especie.toLowerCase());
         };
    }

    // Filtra por idade mínima
    public static Specification<Pet> idadeGreaterThanOrEqual(Integer idadeMin) {
        return (root, query, cb) -> {
             if (idadeMin == null) {
                return cb.conjunction();
            }
            return cb.greaterThanOrEqualTo(root.get("idade"), idadeMin);
        };
    }

    // Filtra por idade máxima
    public static Specification<Pet> idadeLessThanOrEqual(Integer idadeMax) {
         return (root, query, cb) -> {
             if (idadeMax == null) {
                 return cb.conjunction();
             }
             return cb.lessThanOrEqualTo(root.get("idade"), idadeMax);
         };
    }

     // Filtra por gênero (case-insensitive)
     public static Specification<Pet> generoEquals(String genero) {
         return (root, query, cb) -> {
             if (genero == null || genero.trim().isEmpty()) {
                 return cb.conjunction();
             }
             return cb.equal(cb.lower(root.get("genero")), genero.toLowerCase());
         };
     }

    // Filtra por porte (case-insensitive)
     public static Specification<Pet> porteEquals(String porte) {
         return (root, query, cb) -> {
             if (porte == null || porte.trim().isEmpty()) {
                 return cb.conjunction();
             }
             return cb.equal(cb.lower(root.get("porte")), porte.toLowerCase());
         };
     }

     // Filtro padrão para buscar apenas pets NÃO adotados
    public static Specification<Pet> isNotAdopted() {
        return (root, query, cb) -> cb.equal(root.get("adotado"), false);
    }

    // Filtro por raça
    public static Specification<Pet> racaContains(String raca) {
        return (root, query, cb) -> {
            if (raca == null || raca.trim().isEmpty()) {
                return cb.conjunction();
            }
            return cb.like(cb.lower(root.get("raca")), "%" + raca.toLowerCase() + "%");
        };
    }

    // Filtro por cidade
    public static Specification<Pet> cidadeContains(String cidade) {
        return (root, query, cb) -> {
            if (cidade == null || cidade.trim().isEmpty()) {
                return cb.conjunction();
            }
            return cb.like(cb.lower(root.get("cidade")), "%" + cidade.toLowerCase() + "%");
        };
    }

    // Filtro por estado (uppercase. ex: SP)
    public static Specification<Pet> estadoEquals(String estado) {
        return (root, query, cb) -> {
            if (estado == null || estado.trim().isEmpty()) {
                return cb.conjunction();
            }
            return cb.equal(cb.upper(root.get("estado")), estado.toUpperCase());
        };
    }

}
