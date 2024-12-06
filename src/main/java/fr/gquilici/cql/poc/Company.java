package fr.gquilici.cql.poc;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class Company {

	@Id
	private String code;

	@Column
	private String name;

	@OneToMany(mappedBy = "company")
	private List<Employee> employees;

	public Company() {
		employees = new ArrayList<>();
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Employee> getEmployees() {
		return employees;
	}

	public void setEmployees(List<Employee> employees) {
		this.employees = employees;
	}

	@Override
	public int hashCode() {
		return Objects.hash(code);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Company other = (Company) obj;
		return Objects.equals(code, other.code);
	}

	@Override
	public String toString() {
		return "Company {code=" + code + ", name=" + name + ", employees=" + employees + "}";
	}

}
