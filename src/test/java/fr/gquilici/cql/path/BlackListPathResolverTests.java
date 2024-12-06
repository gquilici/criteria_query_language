package fr.gquilici.cql.path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import fr.gquilici.cql.poc.Company;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;

@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
public class BlackListPathResolverTests {

	@Autowired
	private EntityManager entityManager;

	private CriteriaBuilder builder;

	@BeforeAll
	void setup() {
		builder = entityManager.getCriteriaBuilder();
	}

	@Test
	void noRegisteredRestriction() {
		BlackListPathResolver pathResolver = new BlackListPathResolver();

		CriteriaQuery<Company> query = builder.createQuery(Company.class);
		Root<Company> root = query.from(Company.class);

		List<String> patterns = pathResolver.getPatterns(root);
		assertThat(patterns).isNotNull();
		assertThat(patterns).isEmpty();
	}

	@Test
	void retrieveRegisteredRestrictions() {
		BlackListPathResolver pathResolver = new BlackListPathResolver();
		pathResolver.register(Company.class, "code", "employees.**");

		CriteriaQuery<Company> query = builder.createQuery(Company.class);
		Root<Company> root = query.from(Company.class);

		List<String> patterns = pathResolver.getPatterns(root);
		assertThat(patterns).hasSize(2);
		assertThat(patterns).contains("code", "employees.**");
	}

	@Test
	void restrictLocalProperty() {
		BlackListPathResolver pathResolver = new BlackListPathResolver();
		pathResolver.register(Company.class, "code");

		CriteriaQuery<Company> query = builder.createQuery(Company.class);
		Root<Company> root = query.from(Company.class);

		boolean authorized = pathResolver.isAuthorized(root, "code");
		assertFalse(authorized, "Property <code> should be unreachable");

		authorized = pathResolver.isAuthorized(root, "name");
		assertTrue(authorized, "Property <name> should be reachable");
	}

	@Test
	void restrictWithOneWildcard() {
		BlackListPathResolver pathResolver = new BlackListPathResolver();
		pathResolver.register(Company.class, "employees.*");

		CriteriaQuery<Company> query = builder.createQuery(Company.class);
		Root<Company> root = query.from(Company.class);

		boolean authorized = pathResolver.isAuthorized(root, "employees.lastName");
		assertFalse(authorized, "Property <employees.lastName> should be unreachable");

		authorized = pathResolver.isAuthorized(root, "employees.company.code");
		assertTrue(authorized, "Property <employees.company.code> should be reachable");
	}

	@Test
	void restrictWithTwoWildcards() {
		BlackListPathResolver pathResolver = new BlackListPathResolver();
		pathResolver.register(Company.class, "employees.**");

		CriteriaQuery<Company> query = builder.createQuery(Company.class);
		Root<Company> root = query.from(Company.class);

		boolean authorized = pathResolver.isAuthorized(root, "employees.lastName");
		assertFalse(authorized, "Property <employees.lastName> should be unreachable");

		authorized = pathResolver.isAuthorized(root, "employees.company.code");
		assertFalse(authorized, "Property <employees.company.code> should be unreachable");
	}

	@Test
	void resolveReachableProperty() {
		BlackListPathResolver pathResolver = new BlackListPathResolver();
		pathResolver.register(Company.class, "employees.**");

		CriteriaQuery<Company> query = builder.createQuery(Company.class);
		Root<Company> root = query.from(Company.class);

		Path<?> property = pathResolver.resolve(root, "code");
		assertThat(property.getJavaType()).isEqualTo(String.class);
	}

	@Test
	void resolveUnreachableProperty() {
		BlackListPathResolver pathResolver = new BlackListPathResolver();
		pathResolver.register(Company.class, "employees.**");

		CriteriaQuery<Company> query = builder.createQuery(Company.class);
		Root<Company> root = query.from(Company.class);

		Exception e = assertThrows(IllegalArgumentException.class,
				() -> pathResolver.resolve(root, "employees.lastName"),
				"An unreachable property should raise an exception");
		assertThat(e.getMessage()).isEqualTo(
				"Property <employees.lastName> is not authorized for target type <fr.gquilici.cql.poc.Company>");
	}

}
