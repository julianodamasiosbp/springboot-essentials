package br.com.acme.springboot_essentials.controller;

import br.com.acme.springboot_essentials.domain.Anime;
import br.com.acme.springboot_essentials.utils.DateUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("anime")
@Log4j2
public class AnimeController {

    private final DateUtil dateUtil;

    public AnimeController(DateUtil dateUtil) {
        this.dateUtil = dateUtil;
    }

    @GetMapping("list")
    public List<Anime> list(){
       log.info(dateUtil.formatLocalDateTimetoDatabaseStyle(LocalDateTime.now()));
        return List.of(new Anime("DBZ"), new Anime("Turma da Monica"),
                new Anime("Pokemon"));
    }
}
