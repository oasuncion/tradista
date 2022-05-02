CREATE table LEGAL_ENTITY(
ID          BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY  (START WITH 1, INCREMENT BY 1),
SHORT_NAME    VARCHAR(20) UNIQUE NOT NULL,
LONG_NAME VARCHAR(100) UNIQUE,
ROLE VARCHAR(14) NOT NULL,
DESCRIPTION VARCHAR(1000));