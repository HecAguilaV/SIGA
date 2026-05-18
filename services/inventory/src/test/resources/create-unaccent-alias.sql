-- H2 compatibility alias for PostgreSQL's unaccent function
-- This allows the f_unaccent() function to work in H2 during tests
-- Note: Java body must NOT contain semicolons to avoid SQL parsing issues
CREATE ALIAS IF NOT EXISTS f_unaccent AS 'String fUnaccent(String s) { return s; }';
