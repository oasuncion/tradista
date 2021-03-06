CREATE table IRFORWARD_TRADE(
IRFORWARD_TRADE_ID BIGINT PRIMARY KEY,
MATURITY_DATE DATE,
FREQUENCY VARCHAR(20),
REFERENCE_RATE_INDEX_ID BIGINT,
REFERENCE_RATE_INDEX_TENOR VARCHAR(20),
DAY_COUNT_CONVENTION_ID BIGINT,
INTEREST_PAYMENT VARCHAR(19),
INTEREST_FIXING VARCHAR(19),
CONSTRAINT FK_IRFORWARD_TRADE_ID FOREIGN KEY (IRFORWARD_TRADE_ID) REFERENCES TRADE(ID),
CONSTRAINT FK_IRFORWARD_REFERENCE_RATE_INDEX_ID FOREIGN KEY (REFERENCE_RATE_INDEX_ID) REFERENCES INDEX(ID),
CONSTRAINT FK_IRFORWARD_DAY_COUNT_CONVENTION_ID FOREIGN KEY (DAY_COUNT_CONVENTION_ID) REFERENCES DAY_COUNT_CONVENTION(ID));
