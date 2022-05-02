CREATE table FX_CURVE(
ID BIGINT PRIMARY KEY, 
PRIMARY_CURRENCY_ID BIGINT,
QUOTE_CURRENCY_ID BIGINT,
PRIMARY_CURRENCY_CURVE_ID BIGINT,
QUOTE_CURRENCY_CURVE_ID BIGINT,
CONSTRAINT FK_FX_PRIMARY_CURRENCY_ID FOREIGN KEY (PRIMARY_CURRENCY_ID) REFERENCES CURRENCY(ID),
CONSTRAINT FK_FX_QUOTE_CURRENCY_ID FOREIGN KEY (QUOTE_CURRENCY_ID) REFERENCES CURRENCY(ID),
CONSTRAINT FK_FX_PRIMARY_CURRENCY_CURVE_ID FOREIGN KEY (PRIMARY_CURRENCY_CURVE_ID) REFERENCES INTEREST_RATE_CURVE(ID),
CONSTRAINT FK_FX_QUOTE_CURRENCY_CURVE_ID FOREIGN KEY (QUOTE_CURRENCY_CURVE_ID) REFERENCES INTEREST_RATE_CURVE(ID),
CONSTRAINT FK_FX_CURVE_ID FOREIGN KEY (ID) REFERENCES CURVE(ID));