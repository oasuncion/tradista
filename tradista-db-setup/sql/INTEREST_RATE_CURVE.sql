CREATE table INTEREST_RATE_CURVE(
ID BIGINT PRIMARY KEY, 
TYPE VARCHAR(20) NOT NULL,
CONSTRAINT FK_IR_CURVE_ID FOREIGN KEY (ID) REFERENCES CURVE(ID));