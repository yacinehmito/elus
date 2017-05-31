CREATE TABLE elus.circonscriptions (
  code_dpt VARCHAR(4) NOT NULL,
  num INTEGER NOT NULL,
  shape GEOMETRY,
  CONSTRAINT circonscriptions_code_dpt_num_pk PRIMARY KEY (code_dpt, num)
);
CREATE UNIQUE INDEX circonscriptions_code_dpt_num_pk ON elus.circonscriptions (code_dpt, num)
