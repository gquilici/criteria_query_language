package fr.gquilici.cql;

import org.springframework.data.jpa.domain.Specification;

public interface Operator<N> {

	public <T> Specification<T> build(Filter<N> filter);

}
