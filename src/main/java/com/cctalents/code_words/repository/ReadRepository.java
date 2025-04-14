package com.cctalents.code_words.repository;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

import java.util.List;

@NoRepositoryBean
public interface ReadRepository<T, ID> extends Repository<T, ID> {
    T findById(ID id);
    List<T> findAll();
}
