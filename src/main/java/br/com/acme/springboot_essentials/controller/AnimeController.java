package br.com.acme.springboot_essentials.controller;

import br.com.acme.springboot_essentials.domain.Anime;
import br.com.acme.springboot_essentials.requests.AnimePostRequestBody;
import br.com.acme.springboot_essentials.requests.AnimePutRequestBody;
import br.com.acme.springboot_essentials.service.AnimeService;
import br.com.acme.springboot_essentials.utils.DateUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("animes")
@Log4j2
public class AnimeController {

    private final DateUtil dateUtil;
    private final AnimeService animeService;

    public AnimeController(DateUtil dateUtil, AnimeService animeService) {
        this.dateUtil = dateUtil;
        this.animeService = animeService;
    }

    @GetMapping
    public ResponseEntity<List<Anime>> list(){
       log.info(dateUtil.formatLocalDateTimetoDatabaseStyle(LocalDateTime.now()));
        return ResponseEntity.ok(animeService.listAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Anime> findById(@PathVariable Long id){
        log.info(dateUtil.formatLocalDateTimetoDatabaseStyle(LocalDateTime.now()));
        return ResponseEntity.ok(animeService.findByIdOrThrowBadRequestException(id));
    }

    @PostMapping
    public ResponseEntity<Anime> save(@RequestBody AnimePostRequestBody animePostRequestBody){
        Anime animeSalvo = animeService.save(animePostRequestBody);
        return new ResponseEntity<>(animeSalvo, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        animeService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> replace(@RequestBody AnimePutRequestBody animePutRequestBody){
        animeService.replace(animePutRequestBody);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
