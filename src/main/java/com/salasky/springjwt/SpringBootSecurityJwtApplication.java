package com.salasky.springjwt;

import com.salasky.springjwt.models.Role;
import com.salasky.springjwt.repository.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static com.salasky.springjwt.models.ERole.*;

@SpringBootApplication
public class SpringBootSecurityJwtApplication {

	public static void main(String[] args) {

		var context=SpringApplication.run(SpringBootSecurityJwtApplication.class, args);



	}

}
