package fr.gquilici.cql;

import jakarta.persistence.criteria.Root;

public interface JoinsBuilder<T> {

	public void build(Root<T> root);

}
