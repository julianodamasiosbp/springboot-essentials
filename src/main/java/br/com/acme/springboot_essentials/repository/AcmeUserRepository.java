package br.com.acme.springboot_essentials.repository;

import br.com.acme.springboot_essentials.domain.AcmeUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AcmeUserRepository extends JpaRepository<AcmeUser, Long> {

    AcmeUser findByName(String username);
}
