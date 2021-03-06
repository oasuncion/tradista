CREATE table VOLATILITY_SURFACE(
ID BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY  (START WITH 1, INCREMENT BY 1), 
NAME VARCHAR(20) NOT NULL,
ALGORITHM VARCHAR(20),
INTERPOLATOR VARCHAR(50),
INSTANCE VARCHAR(20),
TYPE VARCHAR(20) NOT NULL,
QUOTE_DATE DATE,
QUOTE_SET_ID BIGINT,
PROCESSING_ORG_ID BIGINT,
CONSTRAINT FK_VS_PROCESSING_ORG_ID FOREIGN KEY (PROCESSING_ORG_ID) REFERENCES LEGAL_ENTITY(ID),
CONSTRAINT UC_VS_NAME_TYPE_PROCESSING_ORG_ID UNIQUE (NAME, TYPE, PROCESSING_ORG_ID),
CONSTRAINT FK_VS_QUOTE_SET_ID FOREIGN KEY (QUOTE_SET_ID) REFERENCES QUOTE_SET(ID));