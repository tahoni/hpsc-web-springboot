-- =============================================================================================
-- V8.8.0 -- Replace match_competitor.competitor_category (a single category) with a
-- match_competitor_category child table, so a match competitor can have one or more
-- categories (MatchCompetitor.competitorCategory is now a List<CompetitorCategory>).
--
-- Existing non-null competitor_category values are carried over into match_competitor_category
-- before the column is dropped, so no data is lost. There is no @OrderColumn on the owning
-- list (see MatchCompetitor.java), so Hibernate manages this element collection with
-- delete-all/reinsert semantics on change; the unique key on (match_competitor_id,
-- competitor_category) stops a category being listed twice for the same match competitor.
-- =============================================================================================

CREATE TABLE match_competitor_category
(
    match_competitor_id BIGINT       NOT NULL,
    competitor_category VARCHAR(255) NOT NULL,
    CONSTRAINT fk_match_competitor_category_match_competitor FOREIGN KEY (match_competitor_id) REFERENCES match_competitor (id),
    CONSTRAINT uk_match_competitor_category UNIQUE (match_competitor_id, competitor_category)
) ENGINE = InnoDB;

INSERT INTO match_competitor_category (match_competitor_id, competitor_category)
SELECT id, competitor_category
FROM match_competitor
WHERE competitor_category IS NOT NULL;

ALTER TABLE match_competitor
    DROP COLUMN competitor_category;
