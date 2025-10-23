package com.gongcaedan_auth;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.util.Objects;

@SpringBootApplication
@ComponentScan(basePackages = {
        "com.gongcaedan_auth",   // 기본 패키지
        "com.util",               // JwtUtil이 있는 패키지
        "com.config"
})
public class GongcaedanAuthApplication {

    public static void main(String[] args) {
        // .env 설정
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .load();

        // 환경변수를 시스템 프로퍼티로 설정
        System.setProperty("DB_URL", Objects.requireNonNull(dotenv.get("DB_URL")));
        System.setProperty("DB_USERNAME", Objects.requireNonNull(dotenv.get("DB_USERNAME")));
        System.setProperty("DB_PASSWORD", Objects.requireNonNull(dotenv.get("DB_PASSWORD")));
        System.setProperty("JWT_SECRET_BASE64", Objects.requireNonNull(dotenv.get("JWT_SECRET_BASE64")));
        System.setProperty("REDIS_URL", Objects.requireNonNull(dotenv.get("REDIS_URL")));

        System.out.println("==============================================");
        System.out.println("[App 시작] REDIS_URL = " + System.getProperty("REDIS_URL"));
        System.out.println("==============================================");

        SpringApplication.run(GongcaedanAuthApplication.class, args);
    }


}
