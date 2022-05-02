CREATE table IRSWAP_TRADE(
IRSWAP_TRADE_ID BIGINT PRIMARY KEY ,
MATURITY_DATE DATE,
MATURITY_TENOR VARCHAR(20),
PAYMENT_FREQUENCY VARCHAR(20) NOT NULL,
RECEPTION_FREQUENCY VARCHAR(20) NOT NULL,
PAYMENT_FIXED_INTEREST_RATE DECIMAL(30,15),
PAYMENT_REFERENCE_RATE_INDEX_ID BIGINT,
RECEPTION_REFERENCE_RATE_INDEX_ID BIGINT NOT NULL,
PAYMENT_REFERENCE_RATE_INDEX_TENOR VARCHAR(20),
RECEPTION_REFERENCE_RATE_INDEX_TENOR VARCHAR(20) NOT NULL,
PAYMENT_SPREAD DECIMAL(30,15),
RECEPTION_SPREAD DECIMAL(30,15),
PAYMENT_DAY_COUNT_CONVENTION_ID BIGINT NOT NULL,
RECEPTION_DAY_COUNT_CONVENTION_ID BIGINT NOT NULL,
PAYMENT_INTEREST_PAYMENT VARCHAR(19) NOT NULL,
RECEPTION_INTEREST_PAYMENT VARCHAR(19) NOT NULL,
PAYMENT_INTEREST_FIXING VARCHAR(19),
RECEPTION_INTEREST_FIXING VARCHAR(19) NOT NULL,
CONSTRAINT FK_IRSWAP_TRADE_ID FOREIGN KEY (IRSWAP_TRADE_ID) REFERENCES TRADE(ID),
CONSTRAINT FK_IRSWAP_PAYMENT_REFERENCE_RATE_INDEX_ID FOREIGN KEY (PAYMENT_REFERENCE_RATE_INDEX_ID) REFERENCES INDEX(ID),
CONSTRAINT FK_IRSWAP_RECEPTION_REFERENCE_RATE_INDEX_ID FOREIGN KEY (RECEPTION_REFERENCE_RATE_INDEX_ID) REFERENCES INDEX(ID),
CONSTRAINT FK_IRSWAP_PAYMENT_DAY_COUNT_CONVENTION_ID FOREIGN KEY (PAYMENT_DAY_COUNT_CONVENTION_ID) REFERENCES DAY_COUNT_CONVENTION(ID),
CONSTRAINT FK_IRSWAP_RECEPTION_DAY_COUNT_CONVENTION_ID FOREIGN KEY (RECEPTION_DAY_COUNT_CONVENTION_ID) REFERENCES DAY_COUNT_CONVENTION(ID));
