package br.com.acme.springboot_essentials.integration;

import br.com.acme.springboot_essentials.domain.AcmeUser;
import br.com.acme.springboot_essentials.domain.Anime;
import br.com.acme.springboot_essentials.repository.AcmeUserRepository;
import br.com.acme.springboot_essentials.repository.AnimeRepository;
import br.com.acme.springboot_essentials.requests.AnimePostRequestBody;
import br.com.acme.springboot_essentials.util.AnimeCreator;
import br.com.acme.springboot_essentials.util.AnimePostRequestBodyCreator;
import br.com.acme.springboot_essentials.wrapper.PageableResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class AnimeControllerIT {
    @Autowired
    @Qualifier(value = "testRestTemplateRoleUser")
    private TestRestTemplate testRestTemplate;

//    @LocalServerPort
//    private int port;

    @Autowired
    private AnimeRepository animeRepository;

    @Autowired
    private AcmeUserRepository acmeUserRepository;

    @TestConfiguration
    @Lazy
    static class Config {

        @Bean(name = "testRestTemplateRoleUser")
        public TestRestTemplate testRestTemplateRoleUserCreator(@Value("${local.server.port}") int port) {
            RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
                    .rootUri("http://localhost:" + port)
                    .basicAuthentication("teste", "senha");
            return new TestRestTemplate(restTemplateBuilder);
        }

    }

    @Test
    @DisplayName("List returns list of anime inside page object when successful")
    void list_ReturnsListOfAnimesInsidePageObject_WhenSuccessful(){
        Anime animeSaved = animeRepository.save(AnimeCreator.createAnimeToBeSaved());

        AcmeUser user = AcmeUser
                .builder()
                .name("teste")
                .username("teste")
                .password("$2a$10$NjR3keMSY/YqB1TvWwPOF..WG5FA3Ux4LXTo9W/vK1gnebBAybnsm")
                .authorities("ROLE_USER")
                .build();

        acmeUserRepository.save(user);
        String expectedName = animeSaved.getName();

        PageableResponse<Anime> animePage = testRestTemplate.exchange("/animes", HttpMethod.GET, null,
                new ParameterizedTypeReference<PageableResponse<Anime>>() {
                }).getBody();

        Assertions.assertThat(animePage).isNotNull();
        Assertions.assertThat(animePage.toList())
                .isNotEmpty()
                .hasSize(1);
        Assertions.assertThat(animePage.toList().get(0).getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("ListAll returns list of anime when successful")
    void list_ReturnsListOfAnimes_WhenSuccessful(){
        Anime animeSaved = animeRepository.save(AnimeCreator.createAnimeToBeSaved());

        String expectedName = animeSaved.getName();

        List<Anime> animes = testRestTemplate.exchange("/animes/all", HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Anime>>() {
                }).getBody();

        Assertions.assertThat(animes).isNotNull();
        Assertions.assertThat(animes)
                .isNotEmpty()
                .hasSize(1);
        Assertions.assertThat(animes.get(0).getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("findById returns anime when successful")
    void findById_ReturnAnime_WhenSuccessful(){
        Anime animeSaved = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
        Long expectedId = animeSaved.getId();
        Anime anime = testRestTemplate
                .getForObject("/animes/{id}", Anime.class, expectedId);

        Assertions.assertThat(anime).isNotNull();

        Assertions.assertThat(anime.getId())
                .isNotNull()
                .isEqualTo(expectedId);
    }

    @Test
    @DisplayName("findByName returns anime when successful")
    void findByName_ReturnAnime_WhenSuccessful(){
        Anime animeSaved = animeRepository.save(AnimeCreator.createAnimeToBeSaved());

        String expectedName = animeSaved.getName();

        String url = String.format("/animes/find?name=%s", animeSaved.getName());

        List<Anime> animes = testRestTemplate.exchange(url, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Anime>>() {
                }).getBody();

        Assertions.assertThat(animes).isNotNull();
        Assertions.assertThat(animes)
                .isNotEmpty()
                .hasSize(1);
        Assertions.assertThat(animes.get(0).getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("findByName returns empty list when anime is not found")
    void findByName_ReturnEmptyList_WhenAnimeIsNotFound(){

        List<Anime> animes = testRestTemplate.exchange("/animes/find?name=Chuchu", HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Anime>>() {
                }).getBody();

        Assertions.assertThat(animes)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("save persist anime when successful")
    void save_PersistAnime_WhenSuccessful(){
        AnimePostRequestBody animePostRequestBody = AnimePostRequestBodyCreator
                .createAnimePostRequestBody();
        ResponseEntity<Anime> animeResponseEntity = testRestTemplate
                .postForEntity("/animes", animePostRequestBody, Anime.class);

        Assertions.assertThat(animeResponseEntity)
                .isNotNull();
        Assertions.assertThat(animeResponseEntity.getStatusCode())
                .isEqualTo(HttpStatus.CREATED);
        Assertions.assertThat(animeResponseEntity.getBody())
                .isNotNull();
        Assertions.assertThat(animeResponseEntity.getBody().getId())
                .isNotNull();
    }

    @Test
    @DisplayName("replace updates anime when successful")
    void replace_UpdatesAnime_WhenSuccessful(){
        Anime animeSaved = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
        animeSaved.setName("New Name");
        ResponseEntity<Void> animeResponseEntity = testRestTemplate
                .exchange("/animes", HttpMethod.PUT, new HttpEntity<>(animeSaved), Void.class);

        Assertions.assertThat(animeResponseEntity)
                .isNotNull();
        Assertions.assertThat(animeResponseEntity.getStatusCode())
                .isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("delete removes anime when successful")
    void delete_RemovesAnime_WhenSuccessful(){

        Anime animeSaved = animeRepository.save(AnimeCreator.createAnimeToBeSaved());

        ResponseEntity<Void> animeResponseEntity = testRestTemplate
                .exchange("/animes/{id}", HttpMethod.DELETE, null, Void.class, animeSaved.getId());

        Assertions.assertThat(animeResponseEntity)
                .isNotNull();
        Assertions.assertThat(animeResponseEntity.getStatusCode())
                .isEqualTo(HttpStatus.NO_CONTENT);
    }

}
