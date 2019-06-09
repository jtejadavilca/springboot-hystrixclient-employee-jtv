package pe.com.pruebas.clienthystrixendpointjtv.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import pe.com.pruebas.clienthystrixendpointjtv.model.Employee;

@RestController
@RequestMapping("/rest")
@EnableDiscoveryClient
@CrossOrigin(origins = "http://localhost:4200")
public class EmployeeController {

	@Autowired
	private RestTemplate restTemplate;

	@GetMapping(value = "/healthcheck2", produces = "application/json; charset=utf-8")
	public String getHealthCheck() {
		return "{ \"todoOk\" : true }";
	}

	@HystrixCommand(fallbackMethod = "getEmployeeEmpty", commandKey = "employee", groupKey = "employee")
	@GetMapping("/employee/{id}")
	public Employee getEmployee(@PathVariable String id) {
		String urlMongo = "http://mongojtv-server/employee/" + id;
		ResponseEntity<Employee> respEmp = restTemplate.getForEntity(urlMongo, Employee.class);
		return respEmp != null && respEmp.hasBody() ? respEmp.getBody()  : getEmployeeEmpty(id);
	}

	private Employee getEmployeeEmpty(String s) {
		return new Employee();
	}

//	==================================================================================================================
	
	@HystrixCommand(fallbackMethod = "searchByFirstNameEmpty", commandKey = "/employee/firstName", groupKey = "/employee/firstName")
	@GetMapping("/employee/firstName/{firstName}")
	public List<Employee> searchByFirstName(@PathVariable String firstName) {
		String urlMongo = "http://mongojtv-server/employee/firstName/" + firstName;
		List<Employee> employeesList = (List<Employee>)restTemplate.getForObject(urlMongo, List.class);
		return employeesList == null ? searchByFirstNameEmpty(null) : employeesList;
	}

	private List<Employee> searchByFirstNameEmpty(String firstName) {
		return new ArrayList<Employee>();
	}
	
//	==================================================================================================================
	
	@HystrixCommand(fallbackMethod = "getEmployeeEmpty", commandKey = "employee", groupKey = "employee")
	@PutMapping("/employee/{id}")
	public Employee updateEmployee(@RequestBody Employee newEmployee, @PathVariable String id) {

		String urlMongo = "http://mongojtv-server/employee/" + id;
		ResponseEntity<Employee> respEmp = restTemplate.getForEntity(urlMongo, Employee.class);
		if (respEmp != null && respEmp.hasBody()) {
			Employee emp = respEmp.getBody();
			emp.setFirstName(newEmployee.getFirstName());
			emp.setLastName(newEmployee.getLastName());
			emp.setEmail(newEmployee.getEmail());
			
			restTemplate.put(urlMongo, emp);
			return respEmp.getBody();
		} else {
			return getEmployeeEmpty(newEmployee);
		}
	}
	
	private Employee getEmployeeEmpty(Employee newEmployee, String id) {
		return getEmployeeEmpty(newEmployee);
	}
	
//	==================================================================================================================

	@HystrixCommand(fallbackMethod = "deleteEmployeeError", commandKey = "employee", groupKey = "employee")
	@DeleteMapping(value = "/employee/{id}", produces = "application/json; charset=utf-8")
	public String deleteEmployee(@PathVariable String id) {
		String urlMongo = "http://mongojtv-server/employee/" + id;
//		Boolean result = employeeRepository.existsById(id);
		Boolean result = (Boolean)restTemplate.getForObject(urlMongo, Boolean.class);
		if( result ) {
			restTemplate.delete(urlMongo, id);
		}
		return "{ \"operacionExitosa\" : " + (result ? "true" : "false") + " }";
	}
	private String deleteEmployeeError(String id) {
		return "{ \"operacionExitosa\" : false }";
	}
//	==================================================================================================================
	@HystrixCommand(fallbackMethod = "deleteEmployeeError", commandKey = "/employee/delete", groupKey = "/employee/delete")
	@DeleteMapping(value = "/employee/delete", produces = "application/json; charset=utf-8")
	public String deleteAllEmployees() {
		String urlMongo = "http://mongojtv-server/employee/delete";
		restTemplate.delete(urlMongo);
		return "{ \"operacionExitosa\" : true }";
	}
	
//	==================================================================================================================

	@HystrixCommand(fallbackMethod = "getEmployeeEmpty", commandKey = "employee", groupKey = "employee")
	@PostMapping("/employee")
	public Employee addEmployee(@RequestBody Employee newEmployee) {
		Employee emp = null;
		String id = "";
		String urlMongo = "http://mongojtv-server/employee";
		try {
			emp = new Employee(id, newEmployee.getFirstName(), newEmployee.getLastName(), newEmployee.getEmail(), newEmployee.getActive() == null ? Boolean.FALSE : newEmployee.getActive());
			ResponseEntity<Employee> result = (ResponseEntity<Employee>)restTemplate.postForEntity(urlMongo, emp, Employee.class);
			return result.getBody();
		}catch(Exception ex) {
			ex.printStackTrace();
		}

		return null;
	}
	
	private Employee getEmployeeEmpty(Employee newEmployee) {
		return getEmployeeEmpty(newEmployee);
	}
}
