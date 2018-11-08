
Insert into ACCOUNT_STATUS (ID,NAME_TEXT) values (1,'Açık');
Insert into ACCOUNT_STATUS (ID,NAME_TEXT) values (2,'Kapalı');
commit;

Insert into ACCOUNT_TYPE (ID,NAME_TEXT) values (1,'Vadeli Mevduat');
Insert into ACCOUNT_TYPE (ID,NAME_TEXT) values (2,'Vadesiz Mevduat');
Insert into ACCOUNT_TYPE (ID,NAME_TEXT) values (3,'Kredi Hesabı');
commit;

Insert into AUTO_TRANSFER (ID,NAME_TEXT) values (1,'Dahil');
Insert into AUTO_TRANSFER (ID,NAME_TEXT) values (2,'Hariç');
commit;


Insert into BANK (ID,CODE_TEXT,NAME_TEXT) values (1,'GB','Garanti Bankası');
Insert into BANK (ID,CODE_TEXT,NAME_TEXT) values (2,'ING','ING Bank');
commit;


Insert into CURRENCY (ID,CODE_TEXT,NAME_TEXT) values (1,'TL','Türk Lirası');
Insert into CURRENCY (ID,CODE_TEXT,NAME_TEXT) values (2,'USD','Amerikan Doları');
Insert into CURRENCY (ID,CODE_TEXT,NAME_TEXT) values (3,'EUR','Euro');
Insert into CURRENCY (ID,CODE_TEXT,NAME_TEXT) values (4,'GBP','İngiliz Sterlini');
commit;


Insert into SUB_TRXN_TYPE (ID,NAME_TEXT,FK_TRXN_TYPE_ID) values (1,'KDV','1');
Insert into SUB_TRXN_TYPE (ID,NAME_TEXT,FK_TRXN_TYPE_ID) values (2,'Muhtasar','1');
Insert into SUB_TRXN_TYPE (ID,NAME_TEXT,FK_TRXN_TYPE_ID) values (3,'Kurumlar Vergisi','1');
Insert into SUB_TRXN_TYPE (ID,NAME_TEXT,FK_TRXN_TYPE_ID) values (4,'ÖTV','1');
Insert into SUB_TRXN_TYPE (ID,NAME_TEXT,FK_TRXN_TYPE_ID) values (5,'Ceza','1');
Insert into SUB_TRXN_TYPE (ID,NAME_TEXT,FK_TRXN_TYPE_ID) values (6,'Stopaj','1');
Insert into SUB_TRXN_TYPE (ID,NAME_TEXT,FK_TRXN_TYPE_ID) values (7,'KKDF','1');
Insert into SUB_TRXN_TYPE (ID,NAME_TEXT,FK_TRXN_TYPE_ID) values (8,'BSMV','1');
Insert into SUB_TRXN_TYPE (ID,NAME_TEXT,FK_TRXN_TYPE_ID) values (9,'SGK','1');
Insert into SUB_TRXN_TYPE (ID,NAME_TEXT,FK_TRXN_TYPE_ID) values (10,'Gümrük','1');
Insert into SUB_TRXN_TYPE (ID,NAME_TEXT,FK_TRXN_TYPE_ID) values (11,'Spot','2');
Insert into SUB_TRXN_TYPE (ID,NAME_TEXT,FK_TRXN_TYPE_ID) values (12,'Forward','2');
Insert into SUB_TRXN_TYPE (ID,NAME_TEXT,FK_TRXN_TYPE_ID) values (13,'Havale','3');
Insert into SUB_TRXN_TYPE (ID,NAME_TEXT,FK_TRXN_TYPE_ID) values (14,'Eft','3');
commit;


Insert into TRXN_TYPE (ID,NAME_TEXT) values (1,'Vergi');
Insert into TRXN_TYPE (ID,NAME_TEXT) values (2,'Döviz Alış Satış');
Insert into TRXN_TYPE (ID,NAME_TEXT) values (3,'Transferler');
commit;


Insert into WEEKDAY (ID,NAME_TEXT) values (1,'Pazartesi');
Insert into WEEKDAY (ID,NAME_TEXT) values (2,'Salı');
Insert into WEEKDAY (ID,NAME_TEXT) values (3,'Çarşamba');
Insert into WEEKDAY (ID,NAME_TEXT) values (4,'Perşembe');
Insert into WEEKDAY (ID,NAME_TEXT) values (5,'Cuma');
commit;
