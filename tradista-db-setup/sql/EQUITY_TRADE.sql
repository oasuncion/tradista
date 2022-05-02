CREATE table EQUITY_TRADE(
EQUITY_TRADE_ID BIGINT, 
QUANTITY DECIMAL(30,15) NOT NULL,
CONSTRAINT FK_EQUITY_TRADE_ID FOREIGN KEY (EQUITY_TRADE_ID) REFERENCES TRADE(ID));