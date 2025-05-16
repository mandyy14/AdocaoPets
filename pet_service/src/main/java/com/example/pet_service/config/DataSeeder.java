package com.example.pet_service.config;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.pet_service.model.Pet;
import com.example.pet_service.repository.PetRepository;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataSeeder.class);

    @Autowired
    private PetRepository petRepository;

    @Override
    public void run(String... args) throws Exception {
        logger.info("Executando DataSeeder para popular Pets...");

        if (petRepository.count() == 0) {
            logger.info("Nenhum pet encontrado, populando banco de dados...");

            Pet pet1 = new Pet();
            pet1.setNome("Biscoito");
            pet1.setEspecie("CACHORRO");
            pet1.setRaca("Golden Retriever (Filhote)");
            pet1.setIdade(1);
            pet1.setGenero("MACHO");
            pet1.setPorte("MEDIO");
            pet1.setDescricao("Muito brincalhão e cheio de energia, adora buscar bolinhas. Precisa de espaço.");
            pet1.setCidade("Campinas");
            pet1.setEstado("SP");
            pet1.setAdotado(false);
            pet1.setMediaIdentifier("1.webp");

            Pet pet2 = new Pet();
            pet2.setNome("Luna");
            pet2.setEspecie("GATO");
            pet2.setRaca("SRD (Pelo Curto)");
            pet2.setIdade(3);
            pet2.setGenero("FEMEA");
            pet2.setPorte("PEQUENO");
            pet2.setDescricao("Gatinha preta muito esperta e independente. Gosta de observar da janela.");
            pet2.setCidade("Belo Horizonte");
            pet2.setEstado("MG");
            pet2.setAdotado(false);
            pet2.setMediaIdentifier("2.webp");

            Pet pet3 = new Pet();
            pet3.setNome("Thor");
            pet3.setEspecie("CACHORRO");
            pet3.setRaca("Pastor Alemão");
            pet3.setIdade(4);
            pet3.setGenero("MACHO");
            pet3.setPorte("GRANDE");
            pet3.setDescricao("Leal e protetor, ótimo cão de guarda mas também muito dócil com a família.");
            pet3.setCidade("Porto Alegre");
            pet3.setEstado("RS");
            pet3.setAdotado(false);
            pet3.setMediaIdentifier("3.jpg");

            Pet pet4 = new Pet();
            pet4.setNome("Mel");
            pet4.setEspecie("GATO");
            pet4.setRaca("SRD (Tricolor)");
            pet4.setIdade(2);
            pet4.setGenero("FEMEA");
            pet4.setPorte("MEDIO");
            pet4.setDescricao("Muito tranquila e carinhosa, adora um colo e um carinho na barriga.");
            pet4.setCidade("Salvador");
            pet4.setEstado("BA");
            pet4.setAdotado(false);
            pet4.setMediaIdentifier("4.jpg");


            try {
                petRepository.saveAll(List.of(pet1, pet2, pet3, pet4));
                logger.info("Pets de exemplo populados com sucesso!");
            } catch (Exception e) {
                logger.error("Erro ao salvar pets de exemplo: {}", e.getMessage(), e);
            }

        } else {
            logger.info("Banco de dados de Pets já contém dados. Seeding não executado.");
        }
    }
}