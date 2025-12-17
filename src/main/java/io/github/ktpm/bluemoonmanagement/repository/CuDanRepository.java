package io.github.ktpm.bluemoonmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.ktpm.bluemoonmanagement.model.entity.CuDan;
import io.github.ktpm.bluemoonmanagement.repository.custom.CuDanRepositoryCustom;

public interface CuDanRepository extends JpaRepository<CuDan, String>, CuDanRepositoryCustom {
}
