package fr.gquilici.cql;

public interface FilterParser<N> {

	Filter<N> parse(N criteria);

}
