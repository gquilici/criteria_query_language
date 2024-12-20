package fr.gquilici.cql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.gquilici.cql.poc.Company;
import fr.gquilici.cql.poc.CompanyRepository;
import fr.gquilici.cql.poc.Employee;
import fr.gquilici.cql.poc.EmployeeRepository;
import fr.gquilici.cql.poc.json.JsonCqlInterpreterFactory;

@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
class CriteriaQueryLanguageApplicationTests {

	@Autowired
	private CompanyRepository companyRepository;

	@Autowired
	private EmployeeRepository employeeRepository;

	@Autowired
	private ObjectMapper objectMapper;

	private CqlInterpreter<JsonNode, Company> companyCqlInterpreter;
	private Company company1;
	private Company company2;

	private CqlInterpreter<JsonNode, Employee> employeeCqlInterpreter;
	private Employee employee11;
	private Employee employee12;

	@BeforeAll
	void contextLoads() {
		company1 = new Company();
		company1.setCode("CPN-1");
		company1.setName("Company 1");
		company1 = companyRepository.save(company1);

		company2 = new Company();
		company2.setCode("CPN-2");
		company2.setName("Company 2");
		company2 = companyRepository.save(company2);

		employee11 = new Employee();
		employee11.setCode("EMP-11");
		employee11.setFirstName("John");
		employee11.setLastName("Doe");
		employee11.setCompany(company1);
		employee11 = employeeRepository.save(employee11);

		employee12 = new Employee();
		employee12.setCode("EMP-12");
		employee12.setFirstName("Elizabeth");
		employee12.setLastName("Doppler");
		employee12.setCompany(company1);
		employee12 = employeeRepository.save(employee12);

		JsonCqlInterpreterFactory cqlInterpreterFactory = new JsonCqlInterpreterFactory();
		cqlInterpreterFactory.restrictPaths(Company.class, "employees.firstName");

		companyCqlInterpreter = cqlInterpreterFactory.build();
		employeeCqlInterpreter = cqlInterpreterFactory.build();
	}

	@Test
	void requestLocalField() throws JsonMappingException, JsonProcessingException {
		String query = """
				{ "property": "lastName", "operator": "$eq", "operands": ["Doe"] }
				""";
		JsonNode json = objectMapper.readTree(query);

		Specification<Employee> specification = employeeCqlInterpreter.build(json);
		List<Employee> employees = employeeRepository.findAll(specification);

		assertThat(employees).hasSize(1);
		assertThat(employees).first().isEqualTo(employee11);
	}

	@Test
	void requestSingularJoinField() throws JsonMappingException, JsonProcessingException {
		String query = """
				{ "property": "company.code", "operator": "$eq", "operands": ["CPN-1"] }
				""";
		JsonNode json = objectMapper.readTree(query);

		Specification<Employee> specification = employeeCqlInterpreter.build(json);
		List<Employee> employees = employeeRepository.findAll(specification);

		assertThat(employees).hasSize(2);
		assertThat(employees).contains(employee11, employee12);
	}

	@Test
	@Transactional
	void requestCollectionJoinField() throws JsonMappingException, JsonProcessingException {
		String query = """
				{ "property": "employees.code", "operator": "$sw", "operands": ["EMP"] }
				""";
		JsonNode json = objectMapper.readTree(query);

		Specification<Company> specification = companyCqlInterpreter.build(json);
		List<Company> companies = companyRepository.findAll(specification);

		assertThat(companies).hasSize(2);
		assertThat(companies).contains(company1, company1);
	}

	@Test
	void requestWithIgnoreCase() throws JsonMappingException, JsonProcessingException {
		String query = """
				{ "property": "lastName", "operator": { "code": "$eq",  "ignoreCase": true }, "operands": ["dOE"] }
				""";
		JsonNode json = objectMapper.readTree(query);

		Specification<Employee> specification = employeeCqlInterpreter.build(json);
		List<Employee> employees = employeeRepository.findAll(specification);

		assertThat(employees).hasSize(1);
		assertThat(employees).first().isEqualTo(employee11);
	}

	@Test
	void requestWithIgnoreAccents() throws JsonMappingException, JsonProcessingException {
		String query = """
				{ "property": "lastName", "operator": { "code": "$eq", "ignoreAccents": true }, "operands": ["Dôé"] }
				""";
		JsonNode json = objectMapper.readTree(query);

		Specification<Employee> specification = employeeCqlInterpreter.build(json);
		List<Employee> employees = employeeRepository.findAll(specification);

		assertThat(employees).hasSize(1);
		assertThat(employees).first().isEqualTo(employee11);
	}

	@Test
	void requestWithExists() throws JsonMappingException, JsonProcessingException {
		String query = """
				{
				  "operator": {
				    "code": "$ex",
				    "joinProperty": "code",
				    "filter": {
				      "property": "employees.lastName",
				      "operator": "$in",
				      "operands": ["Doe", "Doppler"]
				    }
				  }
				}
				""";
		JsonNode json = objectMapper.readTree(query);

		Specification<Company> specification = companyCqlInterpreter.build(json);
		List<Company> companies = companyRepository.findAll(specification);

		assertThat(companies).hasSize(1);
		assertThat(companies).first().isEqualTo(company1);
	}

	@Test
	void missingProperty() throws JsonMappingException, JsonProcessingException {
		String query = """
				{ "operator": "$eq", "operands": ["Doe"] }
				""";
		JsonNode json = objectMapper.readTree(query);

		Specification<Employee> specification = employeeCqlInterpreter.build(json);

		InvalidDataAccessApiUsageException e = assertThrows(InvalidDataAccessApiUsageException.class,
				() -> employeeRepository.findAll(specification), "A missing property should raise an exception");
		assertThat(e.getCause().getClass()).isEqualTo(IllegalArgumentException.class);
		assertThat(e.getCause().getMessage()).isEqualTo("Argument <property> should not be null");
	}

	@Test
	void nullProperty() throws JsonMappingException, JsonProcessingException {
		String query = """
				{ "property": null, "operator": "$eq", "operands": ["Doe"] }
				""";
		JsonNode json = objectMapper.readTree(query);

		Specification<Employee> specification = employeeCqlInterpreter.build(json);

		InvalidDataAccessApiUsageException e = assertThrows(InvalidDataAccessApiUsageException.class,
				() -> employeeRepository.findAll(specification), "A null property should raise an exception");
		assertThat(e.getCause().getClass()).isEqualTo(IllegalArgumentException.class);
		assertThat(e.getCause().getMessage()).isEqualTo("Argument <property> should not be null");
	}

	@Test
	void unknownProperty() throws JsonMappingException, JsonProcessingException {
		String query = """
				{ "property": "name", "operator": "$eq", "operands": ["Doe"] }
				""";
		JsonNode json = objectMapper.readTree(query);

		Specification<Employee> specification = employeeCqlInterpreter.build(json);

		InvalidDataAccessApiUsageException e = assertThrows(InvalidDataAccessApiUsageException.class,
				() -> employeeRepository.findAll(specification), "An unknown property should raise an exception");
		assertThat(e.getCause().getClass()).isEqualTo(IllegalArgumentException.class);
		assertThat(e.getCause().getMessage())
				.isEqualTo("Property <name> is not supported for target type <fr.gquilici.cql.poc.Employee>");
	}

	@Test
	@Transactional
	void unreachableProperty() throws JsonMappingException, JsonProcessingException {
		String query = """
				{ "property": "employees.firstName", "operator": "$eq", "operands": ["John"] }
				""";
		JsonNode json = objectMapper.readTree(query);

		Specification<Company> specification = companyCqlInterpreter.build(json);
		InvalidDataAccessApiUsageException e = assertThrows(InvalidDataAccessApiUsageException.class,
				() -> companyRepository.findAll(specification), "An unreachable property should raise an exception");
		assertThat(e.getCause().getClass()).isEqualTo(IllegalArgumentException.class);
		assertThat(e.getCause().getMessage()).isEqualTo(
				"Property <employees.firstName> is not authorized for target type <fr.gquilici.cql.poc.Company>");
	}

	@Test
	void missingOperator() throws JsonMappingException, JsonProcessingException {
		String query = """
				{ "property": "lastName", "operands": ["Doe"] }
				""";
		JsonNode json = objectMapper.readTree(query);

		Specification<Employee> specification = employeeCqlInterpreter.build(json);

		InvalidDataAccessApiUsageException e = assertThrows(InvalidDataAccessApiUsageException.class,
				() -> employeeRepository.findAll(specification), "A missing operator should raise an exception");
		assertThat(e.getCause().getClass()).isEqualTo(IllegalArgumentException.class);
		assertThat(e.getCause().getMessage()).isEqualTo("No value for property 'operator' of `ObjectNode`");
	}

	@Test
	void nullOperator() throws JsonMappingException, JsonProcessingException {
		String query = """
				{ "property": "lastName", "operator": null, "operands": ["Doe"] }
				""";
		JsonNode json = objectMapper.readTree(query);

		Specification<Employee> specification = employeeCqlInterpreter.build(json);

		InvalidDataAccessApiUsageException e = assertThrows(InvalidDataAccessApiUsageException.class,
				() -> employeeRepository.findAll(specification), "A null operator should raise an exception");
		assertThat(e.getCause().getClass()).isEqualTo(IllegalArgumentException.class);
		assertThat(e.getCause().getMessage()).isEqualTo("Operator <null> is unknown in JsonCQL");
	}

	@Test
	void unknownOperator() throws JsonMappingException, JsonProcessingException {
		String query = """
				{ "property": "lastName", "operator": "$equals", "operands": ["Doe"] }
				""";
		JsonNode json = objectMapper.readTree(query);

		Specification<Employee> specification = employeeCqlInterpreter.build(json);

		InvalidDataAccessApiUsageException e = assertThrows(InvalidDataAccessApiUsageException.class,
				() -> employeeRepository.findAll(specification), "An unknown operator should raise an exception");
		assertThat(e.getCause().getClass()).isEqualTo(IllegalArgumentException.class);
		assertThat(e.getCause().getMessage()).isEqualTo("Operator <$equals> is unknown in JsonCQL");
	}

	@Test
	void illegalIgnoreCase() throws JsonMappingException, JsonProcessingException {
		String query = """
				{ "property": "lastName", "operator": { "code": "$eq", "ignoreCase": "123" }, "operands": ["dOE"] }
				""";
		JsonNode json = objectMapper.readTree(query);

		Specification<Employee> specification = employeeCqlInterpreter.build(json);
		List<Employee> employees = employeeRepository.findAll(specification);

		assertThat(employees).isEmpty();
	}

	@Test
	void illegalIgnoreAccents() throws JsonMappingException, JsonProcessingException {
		String query = """
				{ "property": "lastName", "operator": { "code": "$eq", "ignoreAccents": "123" }, "operands": ["Dôé"] }
				""";
		JsonNode json = objectMapper.readTree(query);

		Specification<Employee> specification = employeeCqlInterpreter.build(json);
		List<Employee> employees = employeeRepository.findAll(specification);

		assertThat(employees).isEmpty();
	}

	@Test
	void notEnoughOperands() throws JsonMappingException, JsonProcessingException {
		String query = """
				{ "property": "lastName", "operator": "$eq", "operands": [] }
				""";
		JsonNode json = objectMapper.readTree(query);

		Specification<Employee> specification = employeeCqlInterpreter.build(json);
		ArrayIndexOutOfBoundsException e = assertThrows(ArrayIndexOutOfBoundsException.class,
				() -> employeeRepository.findAll(specification), "A missing operand should raise an exception");
		assertThat(e.getMessage()).isEqualTo("Index 0 out of bounds for length 0");
	}

	@Test
	void tooManyOperands() throws JsonMappingException, JsonProcessingException {
		String query = """
				{ "property": "lastName", "operator": "$eq", "operands": ["Doe", "Jane"] }
				""";
		JsonNode json = objectMapper.readTree(query);

		Specification<Employee> specification = employeeCqlInterpreter.build(json);
		List<Employee> employees = employeeRepository.findAll(specification);

		assertThat(employees).hasSize(1);
		assertThat(employees).first().isEqualTo(employee11);
	}

	@Test
	void andOperator() throws JsonMappingException, JsonProcessingException {
		String query = """
				{ "operator": "$and", "operands": [
					{ "property": "lastName", "operator": "$eq", "operands": ["Doe"] },
					{ "property": "lastName", "operator": "$eq", "operands": ["Doppler"] }
				]}
				""";
		JsonNode json = objectMapper.readTree(query);

		Specification<Employee> specification = employeeCqlInterpreter.build(json);
		List<Employee> employees = employeeRepository.findAll(specification);

		assertThat(employees).isEmpty();
	}

	@Test
	void orOperator() throws JsonMappingException, JsonProcessingException {
		String query = """
				{ "operator": "$or", "operands": [
					{ "property": "lastName", "operator": "$eq", "operands": ["Doe"] },
					{ "property": "lastName", "operator": "$eq", "operands": ["Doppler"] }
				]}
				""";
		JsonNode json = objectMapper.readTree(query);

		Specification<Employee> specification = employeeCqlInterpreter.build(json);
		List<Employee> employees = employeeRepository.findAll(specification);

		assertThat(employees).hasSize(2);
		assertThat(employees).contains(employee11, employee12);
	}

	@Test
	void notOperator() throws JsonMappingException, JsonProcessingException {
		String query = """
				{ "operator": "$not", "operands": [
					{ "property": "lastName", "operator": "$eq", "operands": ["Doe"] }
				]}
				""";
		JsonNode json = objectMapper.readTree(query);

		Specification<Employee> specification = employeeCqlInterpreter.build(json);
		List<Employee> employees = employeeRepository.findAll(specification);

		assertThat(employees).hasSize(1);
		assertThat(employees).first().isEqualTo(employee12);
	}

	@Test
	void betweenOperator() throws JsonMappingException, JsonProcessingException {
		String query = """
				{ "property": "lastName", "operator": "$bw", "operands": ["Doa", "Dol"] }
				""";
		JsonNode json = objectMapper.readTree(query);

		Specification<Employee> specification = employeeCqlInterpreter.build(json);
		List<Employee> employees = employeeRepository.findAll(specification);

		assertThat(employees).hasSize(1);
		assertThat(employees).first().isEqualTo(employee11);
	}

	@Test
	void negateOperator() throws JsonMappingException, JsonProcessingException {
		String query = """
				{ "property": "lastName", "operator": "$nbw", "operands": ["Doa", "Dol"] }
				""";
		JsonNode json = objectMapper.readTree(query);

		Specification<Employee> specification = employeeCqlInterpreter.build(json);
		List<Employee> employees = employeeRepository.findAll(specification);

		assertThat(employees).hasSize(1);
		assertThat(employees).first().isEqualTo(employee12);
	}

	@Test
	void likeOperator() throws JsonMappingException, JsonProcessingException {
		String query = """
				{ "property": "lastName", "operator": "$lk", "operands": ["%pp%"] }
				""";
		JsonNode json = objectMapper.readTree(query);

		Specification<Employee> specification = employeeCqlInterpreter.build(json);
		List<Employee> employees = employeeRepository.findAll(specification);

		assertThat(employees).hasSize(1);
		assertThat(employees).first().isEqualTo(employee12);
	}

	@Test
	void startsWithOperator() throws JsonMappingException, JsonProcessingException {
		String query = """
				{ "property": "lastName", "operator": "$sw", "operands": ["Dop"] }
				""";
		JsonNode json = objectMapper.readTree(query);

		Specification<Employee> specification = employeeCqlInterpreter.build(json);
		List<Employee> employees = employeeRepository.findAll(specification);

		assertThat(employees).hasSize(1);
		assertThat(employees).first().isEqualTo(employee12);
	}

	@Test
	void containsOperator() throws JsonMappingException, JsonProcessingException {
		String query = """
				{ "property": "lastName", "operator": "$ct", "operands": ["pp"] }
				""";
		JsonNode json = objectMapper.readTree(query);

		Specification<Employee> specification = employeeCqlInterpreter.build(json);
		List<Employee> employees = employeeRepository.findAll(specification);

		assertThat(employees).hasSize(1);
		assertThat(employees).first().isEqualTo(employee12);
	}

	@Test
	void endsWithOperator() throws JsonMappingException, JsonProcessingException {
		String query = """
				{ "property": "lastName", "operator": "$ew", "operands": ["ler"] }
				""";
		JsonNode json = objectMapper.readTree(query);

		Specification<Employee> specification = employeeCqlInterpreter.build(json);
		List<Employee> employees = employeeRepository.findAll(specification);

		assertThat(employees).hasSize(1);
		assertThat(employees).first().isEqualTo(employee12);
	}

}
