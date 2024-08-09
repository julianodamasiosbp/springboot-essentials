package br.com.acme.springboot_essentials.service;

import br.com.acme.springboot_essentials.repository.AcmeUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AcmeUserDetailsService implements UserDetailsService {

    private final AcmeUserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return Optional.ofNullable(repository.findByName(s))
                .orElseThrow(() -> new UsernameNotFoundException(("User not found.")));
    }
}
