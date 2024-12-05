package fr.gquilici.cql;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public interface OperandsParser<N> {

	public default List<?> parseAs(List<N> operands, Class<?> type) {
		if (String.class.equals(type)) {
			return parseAsString(operands);
		}
		if (Boolean.class.equals(type)) {
			return parseAsBoolean(operands);
		}
		if (Integer.class.equals(type)) {
			return parseAsInteger(operands);
		}
		if (Long.class.equals(type)) {
			return parseAsLong(operands);
		}
		if (Float.class.equals(type)) {
			return getAsFloat(operands);
		}
		if (Double.class.equals(type)) {
			return parseAsDouble(operands);
		}
		if (LocalDate.class.equals(type)) {
			return parseAsLocalDate(operands);
		}
		if (Instant.class.equals(type)) {
			return parseAsInstant(operands);
		}
		if (type.isEnum()) {
			return parseAsEnumValue(operands, type);
		}
		throw new UnsupportedOperationException(
				"Le type d'opérande <" + type.getSimpleName() + "> n'est pas pris en charge");
	}

	public List<String> parseAsString(List<N> operands);

	public List<Boolean> parseAsBoolean(List<N> operands);

	public List<Integer> parseAsInteger(List<N> operands);

	public List<Long> parseAsLong(List<N> operands);

	public List<Float> getAsFloat(List<N> operands);

	public List<Double> parseAsDouble(List<N> operands);

	public List<LocalDate> parseAsLocalDate(List<N> operands);

	public List<Instant> parseAsInstant(List<N> operands);

	@SuppressWarnings("unchecked")
	public default <T> List<T> parseAsEnumValue(List<N> operands, Class<T> type) {
		// Invoquer la méthode static valueOf de l'enum pour obtenir la constante
		Method valueOf = null;
		try {
			valueOf = type.getMethod("valueOf", String.class);
		} catch (NoSuchMethodException | SecurityException e) {
			throw new IllegalArgumentException(
					"Impossible d'accéder à la méthode <valueOf> de l'enum <" + type.getSimpleName() + ">", e);
		}

		List<T> list = new ArrayList<>();
		for (String operand : parseAsString(operands)) {
			try {
				list.add((T) valueOf.invoke(null, operand));
			} catch (IllegalAccessException | IllegalArgumentException e) {
				throw new IllegalArgumentException(
						"Impossible d'invoquer la méthode <valueOf> de l'enum <" + type.getSimpleName() + ">", e);
			} catch (InvocationTargetException e) {
				throw new IllegalArgumentException(
						"La valeur <" + operand + "> ne fait pas partie de l'enum <" + type.getSimpleName() + ">", e);
			}
		}
		return list;
	}

}
