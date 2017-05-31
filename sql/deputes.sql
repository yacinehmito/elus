CREATE TABLE elus.deputes (
  id_an INTEGER PRIMARY KEY NOT NULL,
  code_dpt VARCHAR(4),
  num_circo INTEGER,
  name TEXT,
  firstname TEXT,
  lastname TEXT,
  slug TEXT,
  emails VARCHAR(256),
  CONSTRAINT deputes_circonscriptions_code_dpt_num_fk FOREIGN KEY (code_dpt, num_circo) REFERENCES circonscriptions (code_dpt, num)
);
CREATE UNIQUE INDEX deputes_pkey ON elus.deputes (id_an);
CREATE INDEX deputes_code_dpt_num_circo_index ON elus.deputes (code_dpt, num_circo)
