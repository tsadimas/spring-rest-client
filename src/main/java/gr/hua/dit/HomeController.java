package gr.hua.dit;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import gr.hua.dit.Employee;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {

	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

	public static final String SERVER_URI = "http://localhost:8080/lab2/";
	public static final String SERVER_ADD_URI = "http://localhost:8080/lab2/employee/addparam";

	/**
	 * Simply selects the home view to render by returning its name.
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 */

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String allEmployees(Locale locale, Model model) throws JsonParseException, JsonMappingException, IOException {
		RestTemplate restTemplate = new RestTemplate();

		Employee[] employees = restTemplate.getForObject(SERVER_URI + "employees", Employee[].class);

		
		model.addAttribute("employees", employees);

		System.out.println(Arrays.asList(employees).size());
		
		//and do I need this JSON media type for my use case?
	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);

	    //set my entity
	    HttpEntity<Object> entity = new HttpEntity<Object>(headers);

	    ResponseEntity<String> out = restTemplate.exchange(SERVER_URI + "employees", HttpMethod.GET, entity, String.class);

	    
	
	    ObjectMapper mapper = new ObjectMapper();
	    //JSON from URL to Object
	    Employee[] empss = mapper.readValue(out.getBody(), Employee[].class);

		System.out.println("------" + Arrays.asList(empss).size());

	   

	    System.out.println(out.getBody());
	    System.out.println(out.getStatusCode());

		return "home";
	}

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public String addEmployee(@ModelAttribute("newEmployee") Employee employee, Model model) {

		try {
			RestTemplate restTemplate = new RestTemplate();

			MultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
			parameters.add("name", employee.getName());
			parameters.add("role", employee.getRole());

			Employee response = restTemplate.postForObject(SERVER_URI + "employee/addparam", parameters,
					Employee.class);

			System.out.println("Response  " + response.getName());

			Employee[] employees = restTemplate.getForObject(SERVER_URI + "employees", Employee[].class);
			model.addAttribute("employees", employees);
			System.out.println(Arrays.asList(employees).size());
		} catch (HttpClientErrorException e) {

			System.out.println("error:  " + e.getResponseBodyAsString());

		} catch (Exception e) {
			System.out.println("error:  " + e.getMessage());
		}
		return "home";
	}

	@RequestMapping(value = "/employee", method = RequestMethod.GET)
	public String employee(Model model) {
		Employee newEmployee = new Employee();
		model.addAttribute("newEmployee", newEmployee);
		return "employee";
	}

}
