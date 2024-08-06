package br.com.acme.springboot_essentials.repository;

import br.com.acme.springboot_essentials.domain.Anime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnimeRepository extends JpaRepository<Anime, Long> {
    List<Anime> findByNameContaining(String name);
    List<Anime> findByName(String name);
}
