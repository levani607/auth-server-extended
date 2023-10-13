package me.levani.authserverextended;

import me.levani.authorizationserver.utils.AuthorizationServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.ControllerAdvice;

@SpringBootApplication
@ComponentScan({"me.levani.authorizationserver","me.levani.authserverextended"})
@EnableJpaRepositories({"me.levani.authserverextended.repository"})
@EntityScan({"me.levani.authorizationserver.model","me.levani.authserverextended.model.domain"})
public class AuthServerExtendedApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthServerExtendedApplication.class, args);
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

}
