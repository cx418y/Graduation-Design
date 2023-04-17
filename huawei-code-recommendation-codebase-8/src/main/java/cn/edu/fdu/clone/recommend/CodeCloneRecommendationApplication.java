package cn.edu.fdu.clone.recommend;

import cn.edu.fdu.clone.recommend.construct.FirstPipeline;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.Resource;

@SpringBootApplication
public class CodeCloneRecommendationApplication implements CommandLineRunner {

    @Resource
    private FirstPipeline firstPipeline;

    public static void main(String[] args) {
        SpringApplication.run(CodeCloneRecommendationApplication.class, args);
    }

    @Override
    public void run(String... args) {
        firstPipeline.run();
        System.exit(0);
    }
}
