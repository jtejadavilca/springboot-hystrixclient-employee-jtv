package pe.com.pruebas.clienthystrixendpointjtv.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import pe.com.pruebas.clienthystrixendpointjtv.model.Employee;

@RestController
@RequestMapping("/rest")
@EnableDiscoveryClient
@CrossOrigin(origins = "http://localhost:4200")
public class ListEmployeesController {
	@Autowired
	private RestTemplate restTemplate;
	

	@GetMapping(value = "/healthcheck", produces = "application/json; charset=utf-8")
	public String getHealthCheck() {
		return "{ \"todoOk\" : true }";
	}

	@HystrixCommand(fallbackMethod = "getEmployeesMongoDB",
            commandKey = "employees", groupKey = "employees")
	@GetMapping("/employees")
	public List<Employee> getEmployeesCassandra() {
		String urlCassandra ="http://cassandrajtv-server/empoyees";
		List<Employee> employeesList = (List<Employee>)restTemplate.getForObject(urlCassandra, List.class);
		return employeesList;
	}
	
	@HystrixCommand(fallbackMethod = "getEmployeesRedis",
            commandKey = "employeesMongoDB", groupKey = "employeesMongoDB")
	@GetMapping("/employeesMongoDB")
	public List<Employee> getEmployeesMongoDB() {
		String urlMongo ="http://mongojtv-server/employees";
		List<Employee> employeesList = (List<Employee>)restTemplate.getForObject(urlMongo, List.class);
		return employeesList;
	}
//	@GetMapping("/employeesRedis")
	public List<Employee> getEmployeesRedis() {
		String urlRedis ="http://redisjtv-server/employees";
		List<Employee> employeesList = (List<Employee>)restTemplate.getForObject(urlRedis, List.class);
		return employeesList;
	}
}
