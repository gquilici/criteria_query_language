package fr.gquilici.cql.poc;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Employee {

	@Id
	private String code;

	@Column
	private String firstName;

	@Column
	private String lastName;

	@ManyToOne
	private Company company;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
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
		Employee other = (Employee) obj;
		return Objects.equals(code, other.code);
	}

	@Override
	public String toString() {
		return "Employee {code=" + code + ", firstName=" + firstName + ", lastName=" + lastName + ", company=" + company.getCode() + "}";
	}

}
