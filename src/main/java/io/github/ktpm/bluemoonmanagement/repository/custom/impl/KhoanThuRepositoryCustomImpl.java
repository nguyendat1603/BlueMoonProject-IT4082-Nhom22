package io.github.ktpm.bluemoonmanagement.repository.custom.impl;

import io.github.ktpm.bluemoonmanagement.repository.custom.KhoanThuRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

@Repository
public class KhoanThuRepositoryCustomImpl implements KhoanThuRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public long countByBatBuocAndMonthAndYear(boolean batBuoc, int month, int year) {
        String sql = "SELECT COUNT(*) " +
                "FROM khoan_thu k " +
                "WHERE k.bat_buoc = CAST(:batBuoc AS boolean) " +
                "AND EXTRACT(MONTH FROM k.ngay_tao) = CAST(:month AS int) " +
                "AND EXTRACT(YEAR FROM k.ngay_tao) = CAST(:year AS int)";

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("batBuoc", batBuoc);
        query.setParameter("month", month);
        query.setParameter("year", year);

        Number result = (Number) query.getSingleResult();
        return result.longValue();
    }
}
