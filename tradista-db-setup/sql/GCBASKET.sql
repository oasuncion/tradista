CREATE table GCBASKET(
ID BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY  (START WITH 1, INCREMENT BY 1), 
NAME VARCHAR(50) UNIQUE NOT NULL);