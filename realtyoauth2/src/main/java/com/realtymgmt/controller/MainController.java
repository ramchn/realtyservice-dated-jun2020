package com.realtymgmt.controller;

import java.security.Principal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {

	@GetMapping("/user")
	Principal getUser(Principal user) {
		return user;
	}
}
