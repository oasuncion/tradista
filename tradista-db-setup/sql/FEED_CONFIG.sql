CREATE table FEED_CONFIG(
ID BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY  (START WITH 1, INCREMENT BY 1), 
NAME VARCHAR(50) NOT NULL,
FEED_TYPE VARCHAR(20) NOT NULL,
PROCESSING_ORG_ID BIGINT,
CONSTRAINT FK_FEED_CONFIG_PROCESSNG_ORG_ID FOREIGN KEY (PROCESSING_ORG_ID) REFERENCES LEGAL_ENTITY(ID),
CONSTRAINT UC_FEED_CONFIG_NAME_PROCESSING_ORG_ID UNIQUE (NAME, PROCESSING_ORG_ID));