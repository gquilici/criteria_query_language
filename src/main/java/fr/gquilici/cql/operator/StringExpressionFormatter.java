package fr.gquilici.cql.operator;

import java.util.List;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;

public abstract class StringExpressionFormatter<N> {

	private static final String TRANSLATE_ACCENTS   = "ÁÀÂÄÃÇÉÈÊËÍÌÎÏÑÓÒÔÖÕÚÙÛÜÝŸáàâäãçéèêëíìîïñóòôöõúùûüýÿ";
	private static final String TRANSLATE_NO_ACCENT = "AAAAACEEEEIIIINOOOOOUUUUYYaaaaaceeeeiiiinooooouuuuyy";

	public List<Expression<String>> format(CriteriaBuilder builder, List<?> operands,
			N options) {
		return operands.stream()
				.map(Object::toString)
				.map(builder::literal)
				.map(expression -> format(builder, expression, options))
				.toList();
	}

	public Expression<String> format(CriteriaBuilder builder, Expression<String> expression,
			N options) {
		StringFormatterOptions parsedOptions = parse(options);
		if (parsedOptions.ignoresCase()) {
			expression = builder.lower(expression);
		}
		if (parsedOptions.ignoresAccents()) {
			expression = builder.function("translate", String.class,
					expression,
					builder.literal(TRANSLATE_ACCENTS),
					builder.literal(TRANSLATE_NO_ACCENT));
		}
		return expression;
	}

	protected abstract StringFormatterOptions parse(N options);

}
