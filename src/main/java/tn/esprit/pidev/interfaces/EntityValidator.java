package tn.esprit.pidev.interfaces;

import java.util.Optional;

public interface EntityValidator<T> {
    Optional<String> validate(T entity);
    
    default boolean isValid(T entity) {
        return validate(entity).isEmpty();
    }
} 