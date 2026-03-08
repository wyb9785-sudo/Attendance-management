package com.test.sshProject;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.oas.annotations.EnableOpenApi;

@SpringBootApplication
@EnableOpenApi
@MapperScan("com.test.sshProject.mapper")
public class sshProjectApplication {
    public static void main(String[] args) {
        SpringApplication.run(sshProjectApplication.class, args);
    }

}
