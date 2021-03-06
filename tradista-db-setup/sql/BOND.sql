CREATE table BOND(
PRODUCT_ID BIGINT, 
COUPON    DECIMAL(30,15), 
MATURITY_DATE  DATE,
PRINCIPAL  DECIMAL(30,15),
DATED_DATE DATE,
COUPON_TYPE VARCHAR(20),
COUPON_FREQUENCY VARCHAR(20),
REDEMPTION_PRICE DECIMAL(30,15),
REDEMPTION_CURRENCY_ID BIGINT,
REFERENCE_RATE_INDEX_ID BIGINT,
CAP DECIMAL(30,15),
FLOOR DECIMAL(30,15),
SPREAD DECIMAL(30,15),
LEVERAGE_FACTOR DECIMAL(30,15),
CONSTRAINT FK_BOND_SECURITY_ID FOREIGN KEY (PRODUCT_ID) REFERENCES SECURITY(PRODUCT_ID),
CONSTRAINT FK_BOND_REFERENCE_RATE_INDEX_ID FOREIGN KEY (REFERENCE_RATE_INDEX_ID) REFERENCES INDEX(ID),
CONSTRAINT FK_BOND_CURRENCY_ID FOREIGN KEY (REDEMPTION_CURRENCY_ID) REFERENCES CURRENCY(ID));