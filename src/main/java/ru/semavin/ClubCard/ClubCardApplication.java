package ru.semavin.ClubCard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class ClubCardApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClubCardApplication.class, args);
	}

}
