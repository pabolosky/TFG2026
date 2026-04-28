package com.casino.security;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class DatabaseWebSecurity {

	@Bean
	public UserDetailsManager usersCustom(DataSource dataSource) {
		JdbcUserDetailsManager users = new JdbcUserDetailsManager(dataSource);

	    users.setUsersByUsernameQuery(
	        "SELECT username, user_password, user_status FROM users WHERE username = ?"
	    );

	    users.setAuthoritiesByUsernameQuery(
	    	"SELECT u.username, s.status " +
	        "FROM user_status us " +
	        "INNER JOIN users u ON u.user_id = us.id_user " +
	        "INNER JOIN statuses s ON s.id = us.id_status " +
	        "WHERE u.username = ?"
	    ); 

	    return users;
	    } 
	
	 @Bean
	    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

	        http.authorizeHttpRequests(auth -> auth
	            .requestMatchers("/bootstrap/**", "/scripts/**", "/img/**", "/estilos/**").permitAll() 
	            
	            .requestMatchers("/authentication/**", "/").permitAll()

	            .anyRequest().authenticated()
	        ); 
 
	        http.formLogin(form -> form 
	            .loginPage("/authentication/login")
	            .defaultSuccessUrl("/", true) 
	            .permitAll() 
	        );
  
	        http.logout(logout -> logout
	        		.logoutUrl("/logout") 
	        		.logoutSuccessUrl("/")
	        		.permitAll());
  
	        return http.build();
	    } 
 
	    @Bean
	    public PasswordEncoder passwordEncoder() {
	        return new BCryptPasswordEncoder();
	    }
}
