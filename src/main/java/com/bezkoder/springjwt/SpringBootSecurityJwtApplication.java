package com.bezkoder.springjwt;

import com.bezkoder.springjwt.models.Company;
import com.bezkoder.springjwt.models.Employee;
import com.bezkoder.springjwt.models.Subdivision;
import com.bezkoder.springjwt.repository.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringBootSecurityJwtApplication {

	public static void main(String[] args) {

		var context=SpringApplication.run(SpringBootSecurityJwtApplication.class, args);


		var userrepositories=context.getBean(UserRepository.class);
		var companyRepositories=context.getBean(CompanyRepositories.class);
		var subdivisoinRepositories=context.getBean(SubdivisionRepositories.class);
		var employeeRepositories=context.getBean(EmployeeRepositories.class);
		var orderRepositories=context.getBean(OrderRepositories.class);
/*

        Company company=new Company("Neft","Adress",
                "Adress","Ruk");

        Subdivision subdivision=new Subdivision("IT","88002929","VVP",company);

        subdivisoinRepositories.save(subdivision);*/

/*        Employee employee=new Employee("Krol","Artem",
                "Pavlov","NoBa0","Engineer",subdivision);

        employeeRepositories.save(employee);*/
	}

}
