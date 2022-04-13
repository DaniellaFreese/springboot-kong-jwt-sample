package com.example.sample;

import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;



@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
     @Override
     protected void configure(HttpSecurity http) throws Exception {
         http
             .authorizeRequests(authorizeRequests ->
                 authorizeRequests.antMatchers("/**").permitAll()
                 .anyRequest().authenticated())
             .csrf().disable();
     }

//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http
//            .authorizeRequests(authorizeRequests -> authorizeRequests.anyRequest().authenticated())
//            .oauth2ResourceServer().jwt();
//    }
    
}
