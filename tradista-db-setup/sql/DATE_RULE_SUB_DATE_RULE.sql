CREATE table DATE_RULE_SUB_DATE_RULE(
DATE_RULE_ID BIGINT, 
SUB_DATE_RULE_ID BIGINT,
POSITION INT,
DURATION_DAY INT,
DURATION_MONTH INT,
DURATION_YEAR INT,
CONSTRAINT FK_DATE_RULE_ID FOREIGN KEY (DATE_RULE_ID) REFERENCES DATE_RULE(ID),
CONSTRAINT FK_SUB_DATE_RULE_ID FOREIGN KEY (SUB_DATE_RULE_ID) REFERENCES DATE_RULE(ID)
);
