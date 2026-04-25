package com.makurohashami.realtorconnect.email.dao;

import com.makurohashami.realtorconnect.email.model.Email;
import com.makurohashami.realtorconnect.email.model.EmailStatus;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailDAO {

    private static final String INSERT = """
            INSERT INTO email (to_email, subject, body, is_html, status, created_at)
            VALUES (:toEmail, :subject, :body, :isHtml, :status, :createdAt)
            """;

    private static final String FETCH_AND_LOCK_BATCH = """
            UPDATE email SET status = :processingStatus, updated_at = NOW()
            WHERE id IN (
                SELECT id FROM email WHERE status = :newStatus ORDER BY created_at LIMIT :batchSize
            )
            RETURNING id, to_email, subject, body, is_html, status, created_at, updated_at
            """;

    private static final String BATCH_UPDATE_STATUS = "UPDATE email SET status = :status, updated_at = NOW() WHERE id IN (:ids)";

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public void save(Email email) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(INSERT, buildInsertParams(email), keyHolder, new String[]{"id"});
        email.setId(keyHolder.getKeyAs(Long.class));
    }

    public List<Email> fetchAndSetStatusBatch(int batchSize) {
        return jdbcTemplate.query(
                FETCH_AND_LOCK_BATCH,
                buildFetchAndLockParams(batchSize),
                (rs, rowNum) -> mapRow(rs)
        );
    }

    public void batchUpdateStatus(Collection<Long> ids, EmailStatus status) {
        if (ids.isEmpty()) {
            return;
        }
        jdbcTemplate.update(BATCH_UPDATE_STATUS, buildBatchUpdateStatusParams(ids, status));
    }

    private MapSqlParameterSource buildInsertParams(Email email) {
        return new MapSqlParameterSource()
                .addValue("toEmail", email.getTo())
                .addValue("subject", email.getSubject())
                .addValue("body", email.getBody())
                .addValue("isHtml", email.isHtml())
                .addValue("status", email.getStatus().getId())
                .addValue("createdAt", Timestamp.from(email.getCreatedAt()));
    }

    private MapSqlParameterSource buildFetchAndLockParams(int batchSize) {
        return new MapSqlParameterSource()
                .addValue("newStatus", EmailStatus.NEW.getId())
                .addValue("processingStatus", EmailStatus.PROCESSING.getId())
                .addValue("batchSize", batchSize);
    }

    private MapSqlParameterSource buildBatchUpdateStatusParams(Collection<Long> ids, EmailStatus status) {
        return new MapSqlParameterSource()
                .addValue("ids", ids)
                .addValue("status", status.getId());
    }

    private Email mapRow(ResultSet rs) throws SQLException {
        return Email.builder()
                .id(rs.getLong("id"))
                .to(rs.getString("to_email"))
                .subject(rs.getString("subject"))
                .body(rs.getString("body"))
                .isHtml(rs.getBoolean("is_html"))
                .status(EmailStatus.fromId(rs.getInt("status")))
                .createdAt(rs.getTimestamp("created_at").toInstant())
                .updatedAt(rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toInstant() : null)
                .build();
    }

}
