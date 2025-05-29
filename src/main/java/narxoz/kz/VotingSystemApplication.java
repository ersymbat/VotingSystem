package narxoz.kz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@SpringBootApplication(scanBasePackages = {
		"narxoz.kz.auth",
		"narxoz.kz.config",
		"narxoz.kz.controller",
		"narxoz.kz.repository"
})
public class VotingSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(VotingSystemApplication.class, args);
	}

}
