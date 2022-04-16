//package com.company;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.DependsOn;
//
//@Configuration
//public class Config {
//
//    @Bean
//    @DependsOn({"forBot"})
//    public CheckingLastDate checkingLastDate(){
//        return new CheckingLastDate();
//    }
//
//    @Bean("forBot")
//    public ForBot forBot() {
//        return new ForBot();
//    }
//
//}
