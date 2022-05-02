CREATE table DATE_RULE_CALENDAR(
DATE_RULE_ID BIGINT, 
CALENDAR_ID BIGINT,
CONSTRAINT FK_DRCAL_DATE_RULE_ID FOREIGN KEY (DATE_RULE_ID) REFERENCES DATE_RULE(ID),
CONSTRAINT FK_DRCAL_CALENDAR_ID FOREIGN KEY (CALENDAR_ID) REFERENCES CALENDAR(ID)
);
