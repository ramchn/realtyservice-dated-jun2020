package com.realtymgmt.config;


import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class AuthServerConfig extends GlobalAuthenticationConfigurerAdapter {

	@Autowired
	DataSource datasource;
	
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
	    return new BCryptPasswordEncoder();
	}
	
	public void init(AuthenticationManagerBuilder auth) throws Exception {
		auth
		   .jdbcAuthentication()
		   .dataSource(datasource)
		   .usersByUsernameQuery("select email_address,user_password,enabled "
			        + "from user "
			        + "where email_address = ?")
		   .authoritiesByUsernameQuery("select user_email_address,authority "
			        + "from access "
			        + "where user_email_address = ?");
	}
}
