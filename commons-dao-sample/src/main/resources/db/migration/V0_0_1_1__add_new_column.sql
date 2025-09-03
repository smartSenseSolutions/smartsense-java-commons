CREATE
EXTENSION IF NOT EXISTS "uuid-ossp";

ALTER TABLE author
    ADD uuid UUID DEFAULT NULL;

update author
set uuid=uuid_generate_v4();