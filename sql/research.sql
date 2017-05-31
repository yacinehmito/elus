CREATE MATERIALIZED VIEW elus.research AS
  SELECT c.code_dpt, c.num, c.shape, d.name, d.emails
  FROM elus.circonscriptions c
    LEFT JOIN elus.deputes d ON (c.code_dpt, c.num) = (d.code_dpt, d.num_circo);
