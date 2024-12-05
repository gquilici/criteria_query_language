package fr.gquilici.cql.operator;

import org.springframework.util.Assert;

import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;

public interface PathResolver {

	public default <R, T> Path<R> resolve(Root<T> root, String property) {
		Assert.notNull(property, "Le chemin de la propriété cible ne doit pas être nul");
		try {
			String[] segments = property.split("\\.");
			if (segments.length == 1) {
				return root.get(segments[0]);
			}

			Path<?> path = root.get(segments[0]);
			for (int i = 1; i < segments.length - 1; i++) {
				path = path.get(segments[i]);
			}
			return path.get(segments[segments.length - 1]);
		} catch (Exception e) {
			throw new IllegalArgumentException("Le chemin de propriété <" + property + "> n'est pas supporté", e);
		}
	}

}
