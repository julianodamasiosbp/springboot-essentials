package br.com.acme.springboot_essentials.util;

import br.com.acme.springboot_essentials.domain.Anime;
import br.com.acme.springboot_essentials.requests.AnimePostRequestBody;

public class AnimePostRequestBodyCreator {

    public static AnimePostRequestBody createAnimePostRequestBody() {
        return AnimePostRequestBody.builder()
                .name(AnimeCreator.createAnimeToBeSaved().getName())
                .build();
    }
}
