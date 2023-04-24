package fudan.design.clone;

import fudan.design.clone.utils.sagaUtils.FirstPipeline;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import jakarta.annotation.Resource;

@SpringBootApplication
public class CloneApplication implements CommandLineRunner {
	@Resource
	private FirstPipeline firstPipeline;

	public static void main(String[] args) {

		SpringApplication.run(CloneApplication.class, args);

	}

	@Override
	public void run(String... args) {
		firstPipeline.run();
		//System.exit(0);
	}

}
