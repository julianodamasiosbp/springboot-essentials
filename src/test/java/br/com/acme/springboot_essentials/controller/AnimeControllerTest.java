package br.com.acme.springboot_essentials.controller;

import br.com.acme.springboot_essentials.domain.Anime;
import br.com.acme.springboot_essentials.service.AnimeService;
import br.com.acme.springboot_essentials.util.AnimeCreator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
class AnimeControllerTest {

    @InjectMocks
    private AnimeController animeController;

    @Mock
    private AnimeService animeServiceMock;

    @BeforeEach
    void setUp(){
        PageImpl<Anime> animePage = new PageImpl<>(List.of(AnimeCreator.createValidAnime()));

        BDDMockito.when(animeServiceMock.listAll(ArgumentMatchers.any()))
                .thenReturn(animePage);

        BDDMockito.when(animeServiceMock.listAllNonPageable())
                .thenReturn(List.of(AnimeCreator.createValidAnime()));

        BDDMockito.when(animeServiceMock.findByIdOrThrowBadRequestException(ArgumentMatchers.anyLong()))
                .thenReturn(AnimeCreator.createValidAnime());

        BDDMockito.when(animeServiceMock.findByName(ArgumentMatchers.anyString()))
                .thenReturn(List.of(AnimeCreator.createValidAnime()));
    }

    @Test
    @DisplayName("List returns list of anime inside page object when successful")
    void list_ReturnsListOfAnimesInsidePageObject_WhenSuccessful(){
        String expectedName = AnimeCreator.createValidAnime().getName();
        Page<Anime> animePage = animeController.list(null).getBody();

        Assertions.assertThat(animePage).isNotNull();
        Assertions.assertThat(animePage.toList())
                .isNotEmpty()
                .hasSize(1);
        Assertions.assertThat(animePage.toList().get(0).getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("ListAll returns list of anime when successful")
    void list_ReturnsListOfAnimes_WhenSuccessful(){
        String expectedName = AnimeCreator.createValidAnime().getName();
        List<Anime> animes = animeController.list().getBody();

        Assertions.assertThat(animes).isNotNull();
        Assertions.assertThat(animes)
                .isNotNull()
                .isNotEmpty()
                .hasSize(1);
        Assertions.assertThat(animes.get(0).getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("findById returns anime when successful")
    void findById_ReturnAnime_WhenSuccessful(){
        Long id = AnimeCreator.createValidAnime().getId();
        Anime animeSaved = animeController.findById(1L).getBody();

        Assertions.assertThat(animeSaved).isNotNull();

        Assertions.assertThat(animeSaved.getId())
                .isNotNull()
                .isEqualTo(id);
    }

    @Test
    @DisplayName("findByName returns anime when successful")
    void findByName_ReturnAnime_WhenSuccessful(){
        String expectedName = AnimeCreator.createValidAnime().getName();
        List<Anime> animes = animeController.findByName("Chuchu").getBody();

        Assertions.assertThat(animes).isNotNull();
        Assertions.assertThat(animes)
                .isNotEmpty()
                .hasSize(1);
        Assertions.assertThat(animes.get(0).getName()).isEqualTo(expectedName);
    }

}