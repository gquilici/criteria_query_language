package fr.gquilici.cql.path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import fr.gquilici.cql.operator.PathResolver;
import fr.gquilici.cql.poc.Company;
import fr.gquilici.cql.poc.Employee;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;

@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
public class DefaultPathResolverTests {

	@Autowired
	private EntityManager entityManager;

	private CriteriaBuilder builder;

	private PathResolver pathResolver;

	@BeforeAll
	void setup() {
		builder = entityManager.getCriteriaBuilder();
		pathResolver = new DefaultPathResolver();
	}

	@Test
	void resolveLocalProperty() {
		CriteriaQuery<Company> query = builder.createQuery(Company.class);
		Root<Company> root = query.from(Company.class);

		Path<?> property = pathResolver.resolve(root, "name");
		assertThat(property.getJavaType()).isEqualTo(String.class);
	}

	@Test
	void resolveSingularJoinProperty() {
		CriteriaQuery<Employee> query = builder.createQuery(Employee.class);
		Root<Employee> root = query.from(Employee.class);

		Path<?> property = pathResolver.resolve(root, "company");
		assertThat(property.getJavaType()).isEqualTo(Company.class);
	}

	@Test
	void resolveCollectionJoinProperty() {
		CriteriaQuery<Company> query = builder.createQuery(Company.class);
		Root<Company> root = query.from(Company.class);

		Path<?> property = pathResolver.resolve(root, "employees");
		assertThat(property.getJavaType()).isEqualTo(Employee.class);
	}

	@Test
	void resolveNonLocalProperty() {
		CriteriaQuery<Company> query = builder.createQuery(Company.class);
		Root<Company> root = query.from(Company.class);

		Path<?> property = pathResolver.resolve(root, "employees.lastName");
		assertThat(property.getJavaType()).isEqualTo(String.class);
	}

	@Test
	void resolveNullProperty() {
		CriteriaQuery<Company> query = builder.createQuery(Company.class);
		Root<Company> root = query.from(Company.class);

		Exception e = assertThrows(IllegalArgumentException.class, () -> pathResolver.resolve(root, null),
				"A null property should raise an exception");
		assertThat(e.getMessage()).isEqualTo("Argument <property> should not be null");
	}

	@Test
	void resolveUnknownProperty() {
		CriteriaQuery<Company> query = builder.createQuery(Company.class);
		Root<Company> root = query.from(Company.class);

		Exception e = assertThrows(IllegalArgumentException.class, () -> pathResolver.resolve(root, "foobar"),
				"An unknown property should raise an exception");
		assertThat(e.getMessage())
				.isEqualTo("Property <foobar> is not supported for target type <fr.gquilici.cql.poc.Company>");
	}

}
