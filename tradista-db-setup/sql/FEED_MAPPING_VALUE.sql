CREATE table FEED_MAPPING_VALUE(
FEED_CONFIG_ID BIGINT, 
QUOTE_ID BIGINT,
FEED_QUOTE_NAME VARCHAR(20),
FEED_BID_FIELD VARCHAR(20),
FEED_ASK_FIELD VARCHAR(20),
FEED_OPEN_FIELD VARCHAR(20),
FEED_CLOSE_FIELD VARCHAR(20),
FEED_HIGH_FIELD VARCHAR(20),
FEED_LOW_FIELD VARCHAR(20),
FEED_LAST_FIELD VARCHAR(20),
CONSTRAINT FK_FEED_CONFIG_ID FOREIGN KEY (FEED_CONFIG_ID) REFERENCES FEED_CONFIG(ID),
CONSTRAINT FK_FEED_MAPPING_VALUE_QUOTE_ID FOREIGN KEY (QUOTE_ID) REFERENCES QUOTE(ID));