CREATE ROLE dev WITH LOGIN PASSWORD '';
CREATE DATABASE elus;
GRANT ALL PRIVILEGES ON DATABASE elus to dev;
