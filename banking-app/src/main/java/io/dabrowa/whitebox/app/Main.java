package io.dabrowa.whitebox.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "io.dabrowa.whitebox.app")
public class Main {
    public static String test() {
        return "test";
    }

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
