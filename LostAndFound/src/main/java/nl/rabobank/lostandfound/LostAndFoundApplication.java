package nl.rabobank.lostandfound;

import nl.rabobank.lostandfound.model.User;
import nl.rabobank.lostandfound.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.retry.annotation.EnableRetry;


@EnableRetry
@SpringBootApplication
public class LostAndFoundApplication {

	public static void main(String[] args) {
		SpringApplication.run(LostAndFoundApplication.class, args);
	}


  //Add test role in database when application starts
  @Bean
  CommandLineRunner init(UserRepository userRepository){
    return args -> {
      if(userRepository.count() == 0) {
        User admin = new User();
          admin.setUsername("admin");
          admin.setPassword("password");
          admin.setRole("ADMIN");
        userRepository.save(admin);
      }
    };
  }

}
