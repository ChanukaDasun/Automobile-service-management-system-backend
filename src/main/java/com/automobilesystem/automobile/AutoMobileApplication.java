package com.automobilesystem.automobile;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AutoMobileApplication {

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();
        System.setProperty("MONGO_URI", dotenv.get("MONGO_URI"));
        System.setProperty("PORT", dotenv.get("PORT"));
        SpringApplication.run(AutoMobileApplication.class, args);
    }

}
