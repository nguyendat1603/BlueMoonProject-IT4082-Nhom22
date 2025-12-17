package io.github.ktpm.bluemoonmanagement.repository.custom.impl;

import java.util.List;

import io.github.ktpm.bluemoonmanagement.model.entity.CuDan;
import io.github.ktpm.bluemoonmanagement.repository.custom.CuDanRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

public class CuDanRepositoryCustomImpl implements CuDanRepositoryCustom {
    
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<CuDan> findAllWithCanHo() {
        // JPQL query với LEFT JOIN FETCH để tải cả CuDan và CanHo trong 1 query
        String jpql = "SELECT c FROM CuDan c LEFT JOIN FETCH c.canHo WHERE 1=1";
        
        TypedQuery<CuDan> query = entityManager.createQuery(jpql, CuDan.class);
        
        List<CuDan> result = query.getResultList();
        return result;
    }
} 