package br.com.acme.springboot_essentials;

import br.com.acme.springboot_essentials.domain.Anime;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnimeRepository {
    List<Anime> listAll();
}
