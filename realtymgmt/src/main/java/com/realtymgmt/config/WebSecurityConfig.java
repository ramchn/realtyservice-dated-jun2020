package com.realtymgmt.config;


import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	DataSource datasource;

	@Autowired
	AuthSuccessHandler authSuccessHandler;

	@Bean
	public PasswordEncoder encoder() {
      return new BCryptPasswordEncoder();
	} 


	
   @Override
   protected void configure(HttpSecurity http) throws Exception {
	   
	   http
	   .authorizeRequests()
	   .antMatchers("/signup", "/index").permitAll()
	   .antMatchers("/home").hasAnyRole("TENANT", "OWNER", "CONTACT")
	   .antMatchers("/tenant/**").hasAnyRole("TENANT")
	   .antMatchers("/contact/**").hasAnyRole("CONTACT")
	   .antMatchers("/owner/**").hasAnyRole("OWNER")
	   .anyRequest().authenticated()
	   .and()
	   .formLogin()
	   .loginPage("/signin")
	   //.defaultSuccessUrl("/home", true)
	   .successHandler(authSuccessHandler)
	   .permitAll()
	   .and()
	   .logout().permitAll();
	   
	   
	   /* in memory 
	   http
       .authorizeRequests()
        .antMatchers("/signup").permitAll()
        .antMatchers("/tenant/createtask").access("hasRole('TENANT')")
		.antMatchers("/contact/taketask").access("hasRole('CONTACT')")
		.antMatchers("/owner/assigncontact").access("hasRole('OWNER')")
        .and()
        .formLogin()
          .loginPage("/signin")
          .usernameParameter("username").passwordParameter("password")
          .and()
          .logout();
         */
   }
   
   //@Override
   @Autowired
   protected void configure(AuthenticationManagerBuilder auth) throws Exception {
	  
	   // db authority prefixed with ROLE_ whereas in memory doesn't require
	   auth
	   .jdbcAuthentication()
	   .dataSource(datasource)
	   .usersByUsernameQuery("select email_address,user_password,enabled "
		        + "from user "
		        + "where email_address = ?")
	   .authoritiesByUsernameQuery("select user_email_address,authority "
		        + "from access "
		        + "where user_email_address = ?");

       
       /* in memory doesn't require encode method
	   auth
       .inMemoryAuthentication()
       .withUser("ramamoorthyp@gmail.com").password("{noop}jeffpass").roles("CONTACT")
       .and()
       .withUser("ramamoorthy_p@yahoo.com").password("{noop}jamunpass").roles("OWNER")
       .and()
       .withUser("ramamoorthy_p@rediffmail.com").password("{noop}ravipass").roles("TENANT");
		*/
   }
}
