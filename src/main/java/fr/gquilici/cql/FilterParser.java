package fr.gquilici.cql;

public interface FilterParser<N> {

	public Filter<N> parse(N criteria);

}
