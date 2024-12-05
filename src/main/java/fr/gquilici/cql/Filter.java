package fr.gquilici.cql;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;

public record Filter<N> (String property, Operator<N> operator, N options, List<N> operands) {

	public <T> Specification<T> build() {
		return operator.build(this);
	}

}
