-- =============================================================================================
-- V7.2.0 -- Replace competitor.email_address (a single, optional email) with a competitor_email
-- child table, so a competitor can have zero or more email addresses.
--
-- Existing non-blank email_address values are carried over into competitor_email before the
-- column is dropped, so no data is lost. There is no @OrderColumn on the owning
-- List<String> emailAddresses (see Competitor.java), so the pair of columns below
-- (competitor_id, email_address) is the table's implicit composite primary key -- Hibernate
-- manages this element collection with delete-all/reinsert semantics on change, which is fine
-- given the small, infrequently-changed size of this collection.
-- =============================================================================================

CREATE TABLE competitor_email
(
    competitor_id BIGINT       NOT NULL,
    email_address VARCHAR(255) NOT NULL,
    CONSTRAINT fk_competitor_email_competitor FOREIGN KEY (competitor_id) REFERENCES competitor (id)
) ENGINE = InnoDB;

INSERT INTO competitor_email (competitor_id, email_address)
SELECT id, email_address
FROM competitor
WHERE email_address IS NOT NULL
  AND email_address <> '';

ALTER TABLE competitor
    DROP COLUMN email_address;
