CREATE TABLE Orders (ID BIGINT NOT NULL, CREATIONTIME TIMESTAMP, ORDERSTATE INTEGER, VERSION INTEGER, CATEGORY VARCHAR(2), KEY VARCHAR(9), YEAR VARCHAR(4), CUSTOMER_ID BIGINT, PRIMARY KEY (ID))
CREATE TABLE OrderLines (ID BIGINT NOT NULL, QUANTITY INTEGER NOT NULL, ITEMKEY VARCHAR(10), ORDER_ID BIGINT, PRIMARY KEY (ID))
CREATE TABLE Customers (ID BIGINT NOT NULL, VERSION INTEGER, CUSTOMERKEY BIGINT, PRIMARY KEY (ID))
ALTER TABLE Orders ADD CONSTRAINT FK_Orders_CUSTOMER_ID FOREIGN KEY (CUSTOMER_ID) REFERENCES Customers (ID)
ALTER TABLE OrderLines ADD CONSTRAINT FK_OrderLines_ORDER_ID FOREIGN KEY (ORDER_ID) REFERENCES Orders (ID)
CREATE TABLE SEQUENCE (SEQ_NAME VARCHAR(50) NOT NULL, SEQ_COUNT DECIMAL(38), PRIMARY KEY (SEQ_NAME))
INSERT INTO SEQUENCE(SEQ_NAME, SEQ_COUNT) values ('SEQ_GEN', 0)
CREATE TABLE SEQUENCE (SEQ_NAME VARCHAR(50) NOT NULL, SEQ_COUNT DECIMAL(38), PRIMARY KEY (SEQ_NAME))
INSERT INTO SEQUENCE(SEQ_NAME, SEQ_COUNT) values ('SEQ_GEN', 0)
