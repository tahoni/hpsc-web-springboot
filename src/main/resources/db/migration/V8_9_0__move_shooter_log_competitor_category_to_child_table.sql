-- =============================================================================================
-- V8.9.0 -- Replace shooter_log_competitor.competitor_category (a single category) with a
-- shooter_log_competitor_category child table, so a shooter log competitor can be ranked under
-- one or more categories (ShooterLogCompetitor.competitorCategory is now a
-- List<CompetitorCategory>).
--
-- Existing competitor_category values are carried over into shooter_log_competitor_category
-- before the column is dropped, so no data is lost. There is no @OrderColumn on the owning list
-- (see ShooterLogCompetitor.java), so Hibernate manages this element collection with
-- delete-all/reinsert semantics on change; the unique key on (shooter_log_competitor_id,
-- competitor_category) stops a category being listed twice for the same shooter log competitor.
-- =============================================================================================

CREATE TABLE shooter_log_competitor_category
(
    shooter_log_competitor_id BIGINT       NOT NULL,
    competitor_category       VARCHAR(255) NOT NULL,
    CONSTRAINT fk_slcc_shooter_log_competitor FOREIGN KEY (shooter_log_competitor_id) REFERENCES shooter_log_competitor (id),
    CONSTRAINT uk_slcc_competitor_category UNIQUE (shooter_log_competitor_id, competitor_category)
) ENGINE = InnoDB;

INSERT INTO shooter_log_competitor_category (shooter_log_competitor_id, competitor_category)
SELECT id, competitor_category
FROM shooter_log_competitor
WHERE competitor_category IS NOT NULL;

ALTER TABLE shooter_log_competitor
    DROP COLUMN competitor_category;
