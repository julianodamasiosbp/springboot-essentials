package br.com.acme.springboot_essentials.client;

import br.com.acme.springboot_essentials.domain.Anime;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Log4j2
public class SpringClient {
    public static void main(String[] args) {
        String url = "http://localhost:8080/animes/";
        int animeId = 7;
        ResponseEntity<Anime> entity = new RestTemplate()
                .getForEntity("http://localhost:8080/animes/7", Anime.class);
        log.info(entity);

        Anime anime = new RestTemplate()
                .getForObject("http://localhost:8080/animes/{id}",
                        Anime.class, animeId);

        log.info(anime);

        Anime[] animes = new RestTemplate()
                .getForObject("http://localhost:8080/animes/all",
                        Anime[].class, animeId);

        log.info(Arrays.toString(animes));

        ResponseEntity<List<Anime>> exchange = new RestTemplate()
                .exchange("http://localhost:8080/animes/all",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<Anime>>() {});

        log.info(exchange.getBody());

        Anime kingdom = Anime.builder().name("Kingdom").build();

        Anime kingdomSaved = new RestTemplate().postForObject(url, kingdom, Anime.class);
        log.info(kingdomSaved);

        Anime samurai = Anime.builder().name("Samurai").build();

        ResponseEntity<Anime> samuraiSaved = new RestTemplate().exchange(url, HttpMethod.POST,new HttpEntity<>(samurai), Anime.class);
        log.info(samuraiSaved);
    }
}

