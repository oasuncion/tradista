CREATE table PRICING_PARAMETER_INDEX_CURVE(
PRICING_PARAMETER_ID BIGINT NOT NULL, 
INDEX_ID BIGINT NOT NULL,
INTEREST_RATE_CURVE_ID BIGINT NOT NULL,
CONSTRAINT FK_PP_INDEX_CURVE_PRICING_PARAMETER_ID FOREIGN KEY (PRICING_PARAMETER_ID) REFERENCES PRICING_PARAMETER(ID),
CONSTRAINT FK_PP_INDEX_CURVE_INDEX_ID FOREIGN KEY (INDEX_ID) REFERENCES INDEX(ID),
CONSTRAINT FK_PP_INDEX_CURVE_INTEREST_RATE_CURVE_ID FOREIGN KEY (INTEREST_RATE_CURVE_ID) REFERENCES INTEREST_RATE_CURVE(ID));