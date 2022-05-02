CREATE table PARAMETER(
PARAMETER_ID BIGINT PRIMARY KEY,
POSITION SMALLINT NOT NULL,
TYPE VARCHAR(7) NOT NULL,
FUNCTION_ID BIGINT,
CONSTRAINT FK_PARAMETER_FUNCTION_ID FOREIGN KEY (FUNCTION_ID) REFERENCES FUNCTION(ID));
