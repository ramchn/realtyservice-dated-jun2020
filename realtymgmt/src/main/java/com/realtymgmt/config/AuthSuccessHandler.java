package com.realtymgmt.config;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
 
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
 
@Component
public class AuthSuccessHandler implements AuthenticationSuccessHandler {
 
  //private Logger logger = LoggerFactory.getLogger(this.getClass());
	
  
  @Override
  public void onAuthenticationSuccess(HttpServletRequest request,
      HttpServletResponse response, Authentication authentication)
      throws IOException, ServletException {
        //set our response to OK status
        response.setStatus(HttpServletResponse.SC_OK);
        
        boolean owner = false;
        boolean tenant = false;
        boolean contact = false;
        
        //logger.info("AT onAuthenticationSuccess(...) function!");
        
        String email = authentication.getName();
        
        for (GrantedAuthority auth : authentication.getAuthorities()) {
            if ("ROLE_TENANT".equals(auth.getAuthority())){
            	tenant = true;
            	break;
            } else if("ROLE_OWNER".equals(auth.getAuthority())) {
            	owner = true;
            	break;
            } else if("ROLE_CONTACT".equals(auth.getAuthority())) {
            	contact = true;
            	break;
            }
        }
        
        if(owner){
          response.sendRedirect("/owner/home?email="+email);          
        }else if (contact){
          response.sendRedirect("/contact/home?email="+email);
        } else if (tenant) {
          response.sendRedirect("/tenant/home?email="+email);
        } else {
          response.sendRedirect("/home");
        }
  }
}
