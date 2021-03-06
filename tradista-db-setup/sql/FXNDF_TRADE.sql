CREATE table FXNDF_TRADE(
FXNDF_TRADE_ID BIGINT,
NON_DELIVERABLE_CURRENCY_ID BIGINT,
NDF_RATE DECIMAL(30,15),
CONSTRAINT FK_FXNDF_TRADE_ID FOREIGN KEY (FXNDF_TRADE_ID) REFERENCES TRADE(ID),
CONSTRAINT FK_NON_DELIVERABLE_CURRENCY_ID FOREIGN KEY (NON_DELIVERABLE_CURRENCY_ID) REFERENCES CURRENCY(ID));