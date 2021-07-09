In this project i separate all the file to multiple part for more organizing and reuse purpose 
You will require to add gson-2.8.6.jar and mysql-connector-java-8.0.23.jar to your library
and replace the contend in .classpath.
This is using eclipse DE to compile and run thus you may need to use it , 
You also need to change the DB cridential in DB_Connector to connect DB 

Read the attached Sample_Report.csv file and output two SQL INSERT statements for the following
normalized tables. You can assume storage size is sufficient for the purpose. You can also assume
the tables are empty.
1. The ‘chargeable’ table will have columns
1) id: int auto-increment
2) partnerID: int
3) product: varchar
4) partnerPurchasedPlanID: varchar
5) plan: varchar
6) usage: int
The implementation of the code for the ‘chargeable’ table should:
i. Log an error and skip entries:
 without ‘PartNumber’
 with non-positive ‘itemCount’
ii. Skip any entries where the value of PartnerID matches a configurable list of ‘PartnerID’
[Note: for the purpose of this exercise the list of PartnerIDs to skip contains just 26392]
iii. Map ‘PartNumber’ in the csv to the ‘product’ column in the ‘chargeable’ table based on the
map in the attached typemap.json file. For example the PartNumber ADS000010U0R will
be mapped to product value ‘core.chargeable.adsync’ for the insert.
iv. Map ‘accountGuid’ to ‘partnerPurchasedPlanID’ as alphanumeric string of length 32 and
should strip any non-alphanumeric characters before insert.

v. Map ‘itemCount’ in csv as ‘usage’ in the table subject to a unit reduction rule which for the
purpose of this exercise is as follows,
 EA000001GB0O: 1000
 PMQ00005GB0R: 5000
 SSX006NR: 1000
 SPQ00001MB0R: 2000
For example, an itemCount of 5000 for the PartNumber ‘PMQ00005GB0R’ would be
inserted as usage = 5000/5000 = 1
Insert the quantity as is if there is no unit reduction rule.
vi. Output stats of running totals over ‘itemCount’ for each of the products in a success log.
2. The ‘domains’ table will have columns,
1) id: int auto-increment
2) partnerPurchasedPlanID: varchar
3) domain: varchar
The implementation of the code for the ‘domains’ table should:
i. Record the Domain associated with the partnerPurchasedPlanID in the table
ii. Ensure only distinct domain names are recorded in the ‘domains’ table
