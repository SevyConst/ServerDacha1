package com.company;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {

        if (!isCorrectArgs(args)) {
            logger.error("main method: wrong input arguments");
            return;
        }

        ForProperties forProperties = new ForProperties();
        if (!forProperties.load(args[0], logger)) {
            return;
        }


        Db db = new Db(forProperties.getUrlForDb(), logger);

        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("start!");
    }

    private static boolean isCorrectArgs(String[] args) {
        return 1 == args.length && args[0].startsWith(ForProperties.ARG_START);
    }
}