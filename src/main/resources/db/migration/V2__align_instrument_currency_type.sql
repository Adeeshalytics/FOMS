-- JPA maps the ISO currency code as VARCHAR(3); retain that type in PostgreSQL.
ALTER TABLE instruments
    ALTER COLUMN currency TYPE VARCHAR(3)
    USING TRIM(currency);
