package fr.gquilici.cql;

public interface CqlInterpreterFactory<N> {

	public FilterParser<N> getFilterParser();

	public default <T> CqlInterpreter<N, T> build() {
		return new CqlInterpreter<>(getFilterParser(), (root) -> {
		});
	}

	public default <T> CqlInterpreter<N, T> build(JoinsBuilder<T> joinsBuilder) {
		return new CqlInterpreter<>(getFilterParser(), joinsBuilder);
	}

}
