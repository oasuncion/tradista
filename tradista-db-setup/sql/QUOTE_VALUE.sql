-- open, close and last are reserved DERBY names, so I added an underscore to these fields.
CREATE table QUOTE_VALUE(
QUOTE_ID BIGINT, 
DATE DATE,
BID DECIMAL(30,15),
ASK DECIMAL(30,15),
OPEN_ DECIMAL(30,15),
CLOSE_ DECIMAL(30,15),
HIGH DECIMAL(30,15),
LOW DECIMAL(30,15),
LAST_ DECIMAL(30,15),
SOURCE_NAME VARCHAR(20),
ENTERED_DATE DATE,
QUOTE_SET_ID BIGINT,
CONSTRAINT FK_QUOTE_ID FOREIGN KEY (QUOTE_ID) REFERENCES QUOTE(ID),
CONSTRAINT FK_QUOTE_SET_ID FOREIGN KEY (QUOTE_SET_ID) REFERENCES QUOTE_SET(ID));