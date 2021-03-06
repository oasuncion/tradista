CREATE table PROBABILITY_LAW(
ID BIGINT PRIMARY KEY,
FUNCTION_CALL_ID BIGINT,
PROBABILITY_DISTRIBUTION_ID BIGINT,
CONSTRAINT FK_PROBABILITY_LAW_FUNCTION_CALL_ID FOREIGN KEY (FUNCTION_CALL_ID) REFERENCES FUNCTION_CALL(ID),
CONSTRAINT FK_PROBABILITY_LAW_PROBABILITY_DISTRIBUTION_ID FOREIGN KEY (PROBABILITY_DISTRIBUTION_ID) REFERENCES PROBABILITY_DISTRIBUTION(ID));
