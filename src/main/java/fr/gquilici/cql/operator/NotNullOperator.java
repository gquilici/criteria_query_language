package fr.gquilici.cql.operator;

import org.springframework.data.jpa.domain.Specification;

import fr.gquilici.cql.Filter;
import fr.gquilici.cql.Operator;
import jakarta.persistence.criteria.Path;

public class NotNullOperator implements Operator<Object> {

	private final PathResolver pathResolver;

	public NotNullOperator(PathResolver pathResolver) {
		this.pathResolver = pathResolver;
	}

	@Override
	public <T> Specification<T> build(Filter<Object> filter) {
		return (root, query, builder) -> {
			Path<?> propertyPath = pathResolver.resolve(root, filter.property());
			return builder.isNotNull(propertyPath);
		};
	}

}
