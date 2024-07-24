package br.com.acme.springboot_essentials.requests;

import lombok.Data;

@Data
public class AnimePutRequestBody {
    private Long id;
    private String name;
}
