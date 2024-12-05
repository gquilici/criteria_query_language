package fr.gquilici.cql.operator;

import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;

public interface PathResolver {

	public <R, T> Path<R> resolve(Root<T> root, String property);

}
