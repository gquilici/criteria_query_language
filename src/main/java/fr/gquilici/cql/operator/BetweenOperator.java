package fr.gquilici.cql.operator;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import fr.gquilici.cql.Filter;
import fr.gquilici.cql.OperandsParser;
import fr.gquilici.cql.Operator;
import jakarta.persistence.criteria.Path;

public class BetweenOperator<N> implements Operator<N> {

	private final PathResolver pathResolver;
	private final OperandsParser<N> operandsParser;
	private final StringExpressionFormatter<N> expressionFormatter;

	public BetweenOperator(PathResolver pathResolver, OperandsParser<N> operandsParser,
			StringExpressionFormatter<N> expressionFormatter) {
		this.pathResolver = pathResolver;
		this.operandsParser = operandsParser;
		this.expressionFormatter = expressionFormatter;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> Specification<T> build(Filter<N> filter) {
		return (root, query, builder) -> {
			Path<?> path = pathResolver.resolve(root, filter.property());
			Class<?> type = path.getJavaType();

			if (type.equals(String.class)) {
				List<String> operands = operandsParser.parseAsString(filter.operands());
				var formatPath = expressionFormatter.format(builder, (Path<String>) path, filter.options());
				var formatOperands = expressionFormatter.format(builder, operands, filter.options());
				return builder.between(formatPath, formatOperands.get(0), formatOperands.get(1));
			}
			if (type.equals(Integer.class)) {
				List<Integer> operands = operandsParser.parseAsInteger(filter.operands());
				return builder.between((Path<Integer>) path, operands.get(0), operands.get(1));
			}
			if (type.equals(Long.class)) {
				List<Long> operands = operandsParser.parseAsLong(filter.operands());
				return builder.between((Path<Long>) path, operands.get(0), operands.get(1));
			}
			if (type.equals(Float.class)) {
				List<Float> operands = operandsParser.getAsFloat(filter.operands());
				return builder.between((Path<Float>) path, operands.get(0), operands.get(1));
			}
			if (type.equals(Double.class)) {
				List<Double> operands = operandsParser.parseAsDouble(filter.operands());
				return builder.between((Path<Double>) path, operands.get(0), operands.get(1));
			}
			if (type.equals(LocalDate.class)) {
				List<LocalDate> operands = operandsParser.parseAsLocalDate(filter.operands());
				return builder.between((Path<LocalDate>) path, operands.get(0), operands.get(1));
			}
			if (type.equals(Instant.class)) {
				List<Instant> operands = operandsParser.parseAsInstant(filter.operands());
				return builder.between((Path<Instant>) path, operands.get(0), operands.get(1));
			}
			throw new UnsupportedOperationException("L'opérateur <" + getClass().getSimpleName()
					+ "> ne prend pas en charge le type d'opérande <" + type.getSimpleName() + ">");
		};
	}

}
