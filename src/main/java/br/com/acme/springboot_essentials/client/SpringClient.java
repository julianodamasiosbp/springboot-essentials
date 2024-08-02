package br.com.acme.springboot_essentials.client;

import br.com.acme.springboot_essentials.domain.Anime;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Log4j2
public class SpringClient {
    public static void main(String[] args) {
        String url = "http://localhost:8080/animes/";
        int animeId = 7;

        log.info("=== Rest Template GET using getForEntity returning a ResponseEntity ===");
        ResponseEntity<Anime> entity = new RestTemplate()
                .getForEntity("http://localhost:8080/animes/7", Anime.class);

        log.info(entity);

        log.info("=== Rest Template GET using getForObject returning an Object ===");
        Anime anime = new RestTemplate()
                .getForObject("http://localhost:8080/animes/{id}",
                        Anime.class, animeId);

        log.info(anime);


        log.info("=== Rest Template GET using getForObject returning a list of Objects ===");
        Anime[] animes = new RestTemplate()
                .getForObject("http://localhost:8080/animes/all",
                        Anime[].class, animeId);

        log.info(Arrays.toString(animes));

        log.info("=== Rest Template GET using EXCHANGE returning a ResponseEntity of a list of Anime ===");
        ResponseEntity<List<Anime>> exchange = new RestTemplate()
                .exchange("http://localhost:8080/animes/all",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<Anime>>() {});

        log.info(exchange.getBody());

        log.info("=== Rest Template POST using postForObject saving an Entity ===");
        Anime kingdom = Anime.builder().name("Kingdom").build();

        Anime kingdomSaved = new RestTemplate().postForObject(url, kingdom, Anime.class);

        log.info(kingdomSaved);

        log.info("=== Rest Template POST using EXCHANGE saving an Entity ===");
        Anime samurai = Anime.builder().name("Samurai").build();

        ResponseEntity<Anime> samuraiSaved = new RestTemplate()
                .exchange(url, HttpMethod.POST,new HttpEntity<>(samurai, createJsonHeader()), Anime.class);

        log.info(samuraiSaved);

        log.info("=== Rest Template PUT using EXCHANGE updating an Entity ===");
        Anime animeToBeUpdated = samuraiSaved.getBody();
        if (animeToBeUpdated != null) {
            animeToBeUpdated.setName("Samurai Champloo");
        }

        ResponseEntity<Void> samuraiUpdated = new RestTemplate()
                .exchange(url, HttpMethod.PUT,new HttpEntity<>(animeToBeUpdated, createJsonHeader()), Void.class);

        log.info(samuraiUpdated);

        log.info("=== Rest Template DELETE using EXCHANGE to delete an Entity ===");
        ResponseEntity<Void> samuraiDelete = new RestTemplate()
                .exchange(url + "{id}", HttpMethod.DELETE,
                        null,
                        Void.class,
                        animeToBeUpdated.getId());
        log.info(samuraiDelete);

    }
    private static HttpHeaders createJsonHeader() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return httpHeaders;
    }
}

