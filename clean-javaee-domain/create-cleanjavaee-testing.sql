CREATE TABLE Orders (ID BIGINT NOT NULL, CREATIONTIME TIMESTAMP, ORDERSTATE INTEGER, VERSION INTEGER, CATEGORY VARCHAR(2), KEY VARCHAR(9), YEAR VARCHAR(4), CUSTOMER_ID BIGINT, PRIMARY KEY (ID))
CREATE TABLE OrderLines (ID BIGINT NOT NULL, QUANTITY INTEGER NOT NULL, ITEMKEY VARCHAR(100), ORDER_ID BIGINT, PRIMARY KEY (ID))
CREATE TABLE SequenceElements (ID BIGINT NOT NULL, OPERATOR VARCHAR(50), SEQUENCENUMBER INTEGER, CATEGORY VARCHAR(2), KEY VARCHAR(9), YEAR VARCHAR(4), PRIMARY KEY (ID))
CREATE TABLE Customers (ID BIGINT NOT NULL, VERSION INTEGER, CUSTOMERKEY BIGINT, PRIMARY KEY (ID))
ALTER TABLE Orders ADD CONSTRAINT FK_Orders_CUSTOMER_ID FOREIGN KEY (CUSTOMER_ID) REFERENCES Customers (ID)
ALTER TABLE OrderLines ADD CONSTRAINT FK_OrderLines_ORDER_ID FOREIGN KEY (ORDER_ID) REFERENCES Orders (ID)
CREATE SEQUENCE SEQ_GEN_SEQUENCE START WITH 50 INCREMENT BY 50
CREATE SEQUENCE SEQ_GEN_SEQUENCE START WITH 50 INCREMENT BY 50
