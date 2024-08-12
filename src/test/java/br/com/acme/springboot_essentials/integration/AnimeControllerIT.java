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
    private TestRestTemplate testRestTemplateRoleUser;

    @Autowired
    @Qualifier(value = "testRestTemplateRoleAdmin")
    private TestRestTemplate testRestTemplateRoleAdmin;

//    @LocalServerPort
//    private int port;

    @Autowired
    private AnimeRepository animeRepository;

    @Autowired
    private AcmeUserRepository acmeUserRepository;

    private static final AcmeUser USER = AcmeUser
            .builder()
            .name("acmeuser")
                .username("acmeuser")
                .password("$2a$10$NjR3keMSY/YqB1TvWwPOF..WG5FA3Ux4LXTo9W/vK1gnebBAybnsm")
                .authorities("ROLE_USER")
                .build();

    private static final AcmeUser ADMIN = AcmeUser
            .builder()
            .name("juliano")
            .username("juliano")
            .password("$2a$10$NjR3keMSY/YqB1TvWwPOF..WG5FA3Ux4LXTo9W/vK1gnebBAybnsm")
            .authorities("ROLE_USER,ROLE_ADMIN")
            .build();

    @TestConfiguration
    @Lazy
    static class Config {

        @Bean(name = "testRestTemplateRoleUser")
        public TestRestTemplate testRestTemplateRoleUserCreator(@Value("${local.server.port}") int port) {
            RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
                    .rootUri("http://localhost:" + port)
                    .basicAuthentication("acmeuser", "senha");
            return new TestRestTemplate(restTemplateBuilder);
        }
        @Bean(name = "testRestTemplateRoleAdmin")
        public TestRestTemplate testRestTemplateRoleAdminCreator(@Value("${local.server.port}") int port) {
            RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
                    .rootUri("http://localhost:" + port)
                    .basicAuthentication("juliano", "senha");
            return new TestRestTemplate(restTemplateBuilder);
        }

    }

    @Test
    @DisplayName("List returns list of anime inside page object when successful")
    void list_ReturnsListOfAnimesInsidePageObject_WhenSuccessful(){
        Anime animeSaved = animeRepository.save(AnimeCreator.createAnimeToBeSaved());

        acmeUserRepository.save(USER);
        String expectedName = animeSaved.getName();

        PageableResponse<Anime> animePage = testRestTemplateRoleUser.exchange("/animes", HttpMethod.GET, null,
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
        acmeUserRepository.save(USER);

        String expectedName = animeSaved.getName();

        List<Anime> animes = testRestTemplateRoleUser.exchange("/animes/all", HttpMethod.GET, null,
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
        acmeUserRepository.save(USER);
        Long expectedId = animeSaved.getId();
        Anime anime = testRestTemplateRoleUser
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
        acmeUserRepository.save(USER);

        String expectedName = animeSaved.getName();

        String url = String.format("/animes/find?name=%s", animeSaved.getName());

        List<Anime> animes = testRestTemplateRoleUser.exchange(url, HttpMethod.GET, null,
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
        acmeUserRepository.save(USER);
        List<Anime> animes = testRestTemplateRoleUser.exchange("/animes/find?name=Chuchu", HttpMethod.GET, null,
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
        acmeUserRepository.save(ADMIN);
        ResponseEntity<Anime> animeResponseEntity = testRestTemplateRoleAdmin
                .postForEntity("/animes/admin", animePostRequestBody, Anime.class);

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
    @DisplayName("save returns 403 when user is not admin")
    void save_Returns403_WhenUserIsNotAdmin(){
        AnimePostRequestBody animePostRequestBody = AnimePostRequestBodyCreator
                .createAnimePostRequestBody();
        acmeUserRepository.save(USER);
        ResponseEntity<Anime> animeResponseEntity = testRestTemplateRoleUser
                .postForEntity("/animes/admin", animePostRequestBody, Anime.class);

        Assertions.assertThat(animeResponseEntity)
                .isNotNull();
        Assertions.assertThat(animeResponseEntity.getStatusCode())
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("replace updates anime when successful")
    void replace_UpdatesAnime_WhenSuccessful(){
        Anime animeSaved = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
        acmeUserRepository.save(ADMIN);
        animeSaved.setName("New Name");
        ResponseEntity<Void> animeResponseEntity = testRestTemplateRoleAdmin
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
        acmeUserRepository.save(ADMIN);

        ResponseEntity<Void> animeResponseEntity = testRestTemplateRoleAdmin
                .exchange("/animes/admin/{id}", HttpMethod.DELETE, null, Void.class, animeSaved.getId());

        Assertions.assertThat(animeResponseEntity)
                .isNotNull();
        Assertions.assertThat(animeResponseEntity.getStatusCode())
                .isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("delete returns 403 when user is not admin")
    void delete_Returns403_WhenUserIsNotAdmin(){

        Anime animeSaved = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
        acmeUserRepository.save(USER);

        ResponseEntity<Void> animeResponseEntity = testRestTemplateRoleUser
                .exchange("/animes/admin/{id}", HttpMethod.DELETE, null, Void.class, animeSaved.getId());

        Assertions.assertThat(animeResponseEntity)
                .isNotNull();
        Assertions.assertThat(animeResponseEntity.getStatusCode())
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

}
