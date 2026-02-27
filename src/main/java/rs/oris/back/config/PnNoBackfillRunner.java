package rs.oris.back.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * One-time/idempotent backfill: assigns PN.no_seq for existing rows (per firm, per trailer flag).
 * Runs on startup and only updates rows where no_seq is NULL.
 */
@Component
public class PnNoBackfillRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(PnNoBackfillRunner.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        // Assign sequential numbers for existing PN rows (stable per firm & trailer).
        // Ordering: pn.date (NULLS LAST), then pn_id.
        String sql =
                "WITH base AS ( " +
                "  SELECT v.firm_id AS firm_id, p.trailer AS trailer, COALESCE(MAX(p.no_seq), 0) AS base_no " +
                "  FROM pn p " +
                "  JOIN vehicle v ON v.vehicle_id = p.vehicle_id " +
                "  GROUP BY v.firm_id, p.trailer " +
                "), upd AS ( " +
                "  SELECT p.pn_id AS pn_id, " +
                "         b.base_no + ROW_NUMBER() OVER (PARTITION BY v.firm_id, p.trailer ORDER BY p.date NULLS LAST, p.pn_id) AS new_no " +
                "  FROM pn p " +
                "  JOIN vehicle v ON v.vehicle_id = p.vehicle_id " +
                "  JOIN base b ON b.firm_id = v.firm_id AND b.trailer = p.trailer " +
                "  WHERE p.no_seq IS NULL " +
                ") " +
                "UPDATE pn p SET no_seq = upd.new_no " +
                "FROM upd " +
                "WHERE p.pn_id = upd.pn_id";

        int updated = entityManager.createNativeQuery(sql).executeUpdate();
        if (updated > 0) {
            log.info("PN no_seq backfill updated {} row(s).", updated);
        }
    }
}

