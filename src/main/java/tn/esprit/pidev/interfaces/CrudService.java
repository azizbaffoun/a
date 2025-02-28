package tn.esprit.pidev.interfaces;

import tn.esprit.pidev.models.ApiResponse;
import java.util.List;

public interface CrudService<T, ID> {
    ApiResponse<T> create(T entity);
    ApiResponse<T> read(ID id);
    ApiResponse<List<T>> readAll();
    ApiResponse<T> update(ID id, T entity);
    ApiResponse<Boolean> delete(ID id);
} 