CREATE TABLE SIMPLEACTION 
(ARRIVAL_STATUS_ID BIGINT,
ID BIGINT NOT NULL,
PROCESS_ID BIGINT UNIQUE,
PRIMARY KEY (ID));
ALTER TABLE SIMPLEACTION ADD CONSTRAINT FKkcq3tg2t1sptqaic9uatc1pbr FOREIGN KEY (ARRIVAL_STATUS_ID) REFERENCES STATUS;
ALTER TABLE SIMPLEACTION ADD CONSTRAINT FK7ssitsqp3dcq01iu9syiqtkid FOREIGN KEY (PROCESS_ID) REFERENCES PROCESS;
ALTER TABLE SIMPLEACTION ADD CONSTRAINT FK4wpxdxt8j1q2rvmg5qsjyhb6p FOREIGN KEY (ID) REFERENCES ACTION;