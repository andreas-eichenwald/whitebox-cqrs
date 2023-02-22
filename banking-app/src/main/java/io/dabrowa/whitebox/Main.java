package io.dabrowa.whitebox;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {
    public static String test() {
        return "test";
    }

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
