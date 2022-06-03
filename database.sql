BEGIN TRANSACTION;
DROP TABLE IF EXISTS "Department";
CREATE TABLE IF NOT EXISTS "Department" (
	"id"	INTEGER NOT NULL UNIQUE,
	"acronym"	TEXT NOT NULL,
	"name"	TEXT NOT NULL,
	"faculty"	INTEGER NOT NULL,
	"recordDate"	INTEGER NOT NULL,
	"lastUpdate"	INTEGER,
	CONSTRAINT "fk_Department_faculty" FOREIGN KEY("faculty") REFERENCES "Faculty"("id"),
	CONSTRAINT "PRIMARY_KEY" PRIMARY KEY("id" AUTOINCREMENT),
	CONSTRAINT "uni_Department_acronym" UNIQUE("acronym"),
	CONSTRAINT "uni_Department_name" UNIQUE("name")
);
DROP TABLE IF EXISTS "StudyClass";
CREATE TABLE IF NOT EXISTS "StudyClass" (
	"id"	INTEGER NOT NULL UNIQUE,
	"acronym"	TEXT NOT NULL,
	"name"	TEXT NOT NULL,
	"recordDate"	INTEGER NOT NULL,
	"lastUpdate"	INTEGER,
	PRIMARY KEY("id" AUTOINCREMENT),
	CONSTRAINT "uni_StudyClass_acronym" UNIQUE("acronym"),
	CONSTRAINT "uni_StudyClass_name" UNIQUE("name")
);
DROP TABLE IF EXISTS "UniversitySpend";
CREATE TABLE IF NOT EXISTS "UniversitySpend" (
	"id"	INTEGER NOT NULL UNIQUE,
	"title"	TEXT NOT NULL,
	"description"	TEXT NOT NULL,
	"recordDate"	INTEGER NOT NULL,
	"lastUpdate"	INTEGER,
	CONSTRAINT "PRIMARY_KEY" PRIMARY KEY("id" AUTOINCREMENT),
	CONSTRAINT "uni_UniversitySpend_title" UNIQUE("title")
);
DROP TABLE IF EXISTS "AnnualSpend";
CREATE TABLE IF NOT EXISTS "AnnualSpend" (
	"id"	INTEGER NOT NULL UNIQUE,
	"academicYear"	INTEGER NOT NULL,
	"universitySpend"	INTEGER NOT NULL,
	"recordDate"	INTEGER NOT NULL,
	"lastUpdate"	INTEGER,
	CONSTRAINT "fk_AnnualSpend_academicYear" FOREIGN KEY("academicYear") REFERENCES "AcademicYear"("id"),
	CONSTRAINT "fk_AnnualSpend_universitySpend" FOREIGN KEY("universitySpend") REFERENCES "UniversitySpend"("id"),
	PRIMARY KEY("id" AUTOINCREMENT),
	CONSTRAINT "uni_AnnualSpend_academicYear_univeritySpend" UNIQUE("universitySpend","academicYear")
);
DROP TABLE IF EXISTS "AcademicYear";
CREATE TABLE IF NOT EXISTS "AcademicYear" (
	"id"	INTEGER NOT NULL UNIQUE,
	"label"	TEXT NOT NULL UNIQUE,
	"startDate"	INTEGER NOT NULL,
	"closeDate"	INTEGER,
	"previous"	INTEGER,
	"recordDate"	INTEGER NOT NULL,
	"lastUpdate"	INTEGER,
	CONSTRAINT "fk_AcademicYear_previous" FOREIGN KEY("previous") REFERENCES "AcademicYear"("id"),
	CONSTRAINT "PRIMARY_KEY" PRIMARY KEY("id" AUTOINCREMENT)
);
DROP TABLE IF EXISTS "Faculty";
CREATE TABLE IF NOT EXISTS "Faculty" (
	"id"	INTEGER NOT NULL UNIQUE,
	"acronym"	TEXT NOT NULL,
	"name"	TEXT NOT NULL,
	"recordDate"	INTEGER NOT NULL,
	"lastUpdate"	INTEGER,
	CONSTRAINT "PRIMARY_KEY" PRIMARY KEY("id" AUTOINCREMENT),
	CONSTRAINT "uni_Faculty_name" UNIQUE("name"),
	CONSTRAINT "uni_Faculty_acronym" UNIQUE("acronym")
);
DROP TABLE IF EXISTS "AcademicFee";
CREATE TABLE IF NOT EXISTS "AcademicFee" (
	"id"	INTEGER NOT NULL UNIQUE,
	"academicYear"	INTEGER NOT NULL,
	"amount"	NUMERIC NOT NULL,
	"description"	TEXT,
	"recordDate"	INTEGER NOT NULL,
	"lastUpdate"	INTEGER,
	CONSTRAINT "fk_AcademicFee" FOREIGN KEY("academicYear") REFERENCES "AcademicYear"("id"),
	CONSTRAINT "PRIMARY_KEY" PRIMARY KEY("id" AUTOINCREMENT),
	CONSTRAINT "uni_AcademicFee_academicYear_amount" UNIQUE("academicYear","amount")
);
DROP TABLE IF EXISTS "AllocationCost";
CREATE TABLE IF NOT EXISTS "AllocationCost" (
	"id"	INTEGER NOT NULL UNIQUE,
	"amount"	NUMERIC NOT NULL,
	"annualSpend"	INTEGER NOT NULL,
	"academicFee"	INTEGER NOT NULL,
	"recordDate"	INTEGER NOT NULL,
	"lastUpdate"	INTEGER,
	CONSTRAINT "fk_AllocationCost_academicFee" FOREIGN KEY("academicFee") REFERENCES "AcademicFee"("id"),
	CONSTRAINT "fk_AllocationCost_annualSpend" FOREIGN KEY("annualSpend") REFERENCES "AnnualSpend"("id"),
	CONSTRAINT "pk_AllocationCost" PRIMARY KEY("id" AUTOINCREMENT),
	CONSTRAINT "uni_AllocationCost_academicFee_annualSpend" UNIQUE("annualSpend","academicFee")
);
DROP TABLE IF EXISTS "PaymentFee";
CREATE TABLE IF NOT EXISTS "PaymentFee" (
	"id"	INTEGER NOT NULL UNIQUE,
	"inscription"	INTEGER NOT NULL,
	"amount"	NUMERIC NOT NULL,
	"receivedDate"	INTEGER DEFAULT NULL,
	"receiptNumber"	INTEGER DEFAULT NULL,
	"slipDate"	INTEGER NOT NULL,
	"slipNumber"	INTEGER NOT NULL,
	"wording"	INTEGER,
	"recordDate"	INTEGER NOT NULL,
	"lastUpdate"	INTEGER,
	"location"	INTEGER NOT NULL DEFAULT 1,
	CONSTRAINT "fk_PaymentFee_inscription" FOREIGN KEY("inscription") REFERENCES "Inscription"("id"),
	CONSTRAINT "fk_PaymentFee_location" FOREIGN KEY("location") REFERENCES "PaymentLocation"("id"),
	CONSTRAINT "pk_PaymentFee" PRIMARY KEY("id" AUTOINCREMENT),
	CONSTRAINT "uni_PaymentFee_slepNumber" UNIQUE("slipNumber"),
	CONSTRAINT "uni_PaymentFee_receiptNumber" UNIQUE("receiptNumber")
);
DROP TABLE IF EXISTS "Promotion";
CREATE TABLE IF NOT EXISTS "Promotion" (
	"id"	INTEGER NOT NULL UNIQUE,
	"department"	INTEGER NOT NULL,
	"studyClass"	INTEGER NOT NULL,
	"academicYear"	INTEGER NOT NULL,
	"academicFee"	INTEGER,
	"recordDate"	INTEGER NOT NULL,
	"lastUpdate"	INTEGER,
	CONSTRAINT "fk_Promotion_acaemicYear" FOREIGN KEY("academicYear") REFERENCES "AcademicYear"("id"),
	CONSTRAINT "fk_Promotion_department" FOREIGN KEY("department") REFERENCES "Department"("id"),
	FOREIGN KEY("academicFee") REFERENCES "AcademicFee"("id"),
	CONSTRAINT "fk_Promotion_studyClass" FOREIGN KEY("studyClass") REFERENCES "StudyClass"("id"),
	CONSTRAINT "PRIMARY_KEY" PRIMARY KEY("id" AUTOINCREMENT),
	CONSTRAINT "uni_Promotion_department_academicYear_studyClas" UNIQUE("department","studyClass","academicYear")
);
DROP TABLE IF EXISTS "Student";
CREATE TABLE IF NOT EXISTS "Student" (
	"id"	INTEGER NOT NULL UNIQUE,
	"name"	TEXT NOT NULL,
	"postName"	TEXT NOT NULL,
	"lastName"	TEXT NOT NULL,
	"picture"	TEXT,
	"email"	TEXT,
	"telephone"	TEXT NOT NULL,
	"birthDate"	INTEGER NOT NULL,
	"birthPlace"	INTEGER NOT NULL,
	"matricul"	TEXT NOT NULL,
	"recordDate"	INTEGER NOT NULL,
	"lastUpdate"	INTEGER,
	"firstPromotion"	INTEGER,
	"kind"	TEXT NOT NULL DEFAULT 'M',
	"originalSchool"	TEXT,
	CONSTRAINT "fk_Student_fistPromotion" FOREIGN KEY("firstPromotion") REFERENCES "Promotion"("id"),
	CONSTRAINT "PRIMARY_KEY" PRIMARY KEY("id" AUTOINCREMENT),
	CONSTRAINT "uni_Student_email" UNIQUE("email"),
	CONSTRAINT "uni_Student_telephone" UNIQUE("telephone")
);
DROP TABLE IF EXISTS "Inscription";
CREATE TABLE IF NOT EXISTS "Inscription" (
	"id"	INTEGER NOT NULL UNIQUE,
	"student"	INTEGER NOT NULL,
	"promotion"	INTEGER NOT NULL,
	"recordDate"	INTEGER NOT NULL,
	"lastUpdate"	INTEGER,
	"picture"	TEXT,
	"adress"	TEXT,
	CONSTRAINT "fk_Inscription_student" FOREIGN KEY("student") REFERENCES "Student"("id"),
	CONSTRAINT "fk_Inscription_promotion" FOREIGN KEY("promotion") REFERENCES "Promotion"("id"),
	CONSTRAINT "PRIMARY_KEY" PRIMARY KEY("id" AUTOINCREMENT),
	CONSTRAINT "uni_Iscription_picture" UNIQUE("picture"),
	CONSTRAINT "uni_Inscription_student_promotion" UNIQUE("student","promotion")
);
DROP TABLE IF EXISTS "Outlay";
CREATE TABLE IF NOT EXISTS "Outlay" (
	"id"	INTEGER NOT NULL UNIQUE,
	"recordDate"	INTEGER NOT NULL,
	"lastUpdate"	INTEGER,
	"deliveryDate"	INTEGER NOT NULL,
	"amount"	REAL NOT NULL,
	"wording"	TEXT NOT NULL,
	"reference"	TEXT,
	"account"	INTEGER NOT NULL,
	"academicYear"	INTEGER NOT NULL,
	"deliveryYear"	INTEGER NOT NULL,
	FOREIGN KEY("academicYear") REFERENCES "AcademicYear"("id"),
	CONSTRAINT "fk_Outlay_deliveryYear" FOREIGN KEY("deliveryYear") REFERENCES "AcademicYear"("id"),
	FOREIGN KEY("account") REFERENCES "AnnualSpend"("id"),
	PRIMARY KEY("id" AUTOINCREMENT)
);
DROP TABLE IF EXISTS "UniversityRecipe";
CREATE TABLE IF NOT EXISTS "UniversityRecipe" (
	"id"	INTEGER NOT NULL UNIQUE,
	"recordDate"	INTEGER,
	"lastUpdate"	INTEGER,
	"title"	TEXT NOT NULL,
	"description"	TEXT NOT NULL,
	CONSTRAINT "pk_UniversityRecipe" PRIMARY KEY("id"),
	CONSTRAINT "uni_UniversityRecipe_title" UNIQUE("title")
);
DROP TABLE IF EXISTS "AnnualRecipe";
CREATE TABLE IF NOT EXISTS "AnnualRecipe" (
	"id"	INTEGER NOT NULL UNIQUE,
	"recordDate"	INTEGER NOT NULL,
	"lastUpdate"	INTEGER,
	"academicYear"	INTEGER NOT NULL,
	"universityRecipe"	INTEGER NOT NULL,
	"forecasting"	REAL DEFAULT 0,
	CONSTRAINT "fk_AnnualRecipe_academicYear" FOREIGN KEY("academicYear") REFERENCES "AcademicYear"("id"),
	CONSTRAINT "fk_AnnualRecipe_universityRecipe" FOREIGN KEY("universityRecipe") REFERENCES "UniversityRecipe"("id"),
	CONSTRAINT "pk_AnnualRecipe" PRIMARY KEY("id" AUTOINCREMENT),
	CONSTRAINT "uni_AnnualRecipe_academicYear_universitySpend" UNIQUE("academicYear","universityRecipe")
);
DROP TABLE IF EXISTS "AllocationRecipe";
CREATE TABLE IF NOT EXISTS "AllocationRecipe" (
	"id"	INTEGER NOT NULL UNIQUE,
	"recordDate"	INTEGER NOT NULL,
	"lastUpdate"	INTEGER,
	"percent"	REAL NOT NULL,
	"recipe"	INTEGER NOT NULL,
	"spend"	INTEGER NOT NULL,
	CONSTRAINT "fk_AllocationRecipe_spend" FOREIGN KEY("spend") REFERENCES "AnnualSpend"("id"),
	CONSTRAINT "fk_AllocationRecipe_recipe" FOREIGN KEY("recipe") REFERENCES "AnnualRecipe"("id"),
	CONSTRAINT "pk_AllocationRecipe" PRIMARY KEY("id" AUTOINCREMENT),
	CONSTRAINT "uni_AllocationRecipe_recipe_spend" UNIQUE("recipe","spend")
);
DROP TABLE IF EXISTS "OtherRecipe";
CREATE TABLE IF NOT EXISTS "OtherRecipe" (
	"id"	INTEGER NOT NULL UNIQUE,
	"recordDate"	INTEGER NOT NULL,
	"lastUpdate"	INTEGER,
	"collectionYear"	INTEGER NOT NULL,
	"collectionDate"	INTEGER NOT NULL,
	"amount"	INTEGER NOT NULL,
	"label"	TEXT NOT NULL,
	"account"	INTEGER NOT NULL,
	"location"	INTEGER NOT NULL,
	CONSTRAINT "fk_OtherRecipe_account" FOREIGN KEY("account") REFERENCES "AnnualRecipe"("id"),
	CONSTRAINT "fk_OtherRecipe_location" FOREIGN KEY("location") REFERENCES "PaymentLocation"("id"),
	CONSTRAINT "fk_OtherRecipe_collectionYear" FOREIGN KEY("collectionYear") REFERENCES "AcademicYear"("id"),
	CONSTRAINT "pk_OtherRecipe" PRIMARY KEY("id" AUTOINCREMENT)
);
DROP TABLE IF EXISTS "PaymentLocation";
CREATE TABLE IF NOT EXISTS "PaymentLocation" (
	"id"	INTEGER NOT NULL UNIQUE,
	"recordDaete"	INTEGER NOT NULL,
	"lastUpdate"	INTEGER,
	"name"	TEXT,
	PRIMARY KEY("id" AUTOINCREMENT)
);
DROP VIEW IF EXISTS "V_AnnualRecipe";
CREATE VIEW V_AnnualRecipe AS 
   SELECT 
        AnnualRecipe.id AS id,
        AnnualRecipe.recordDate AS recordDate,
        AnnualRecipe.lastUpdate AS lastUpdate,
        AnnualRecipe.academicYear AS academicYear,
        AnnualRecipe.universityRecipe AS universityRecipe,
        (SELECT SUM(OtherRecipe.amount) FROM OtherRecipe WHERE OtherRecipe.account = AnnualRecipe.id) AS collected 
    FROM AnnualRecipe;
DROP VIEW IF EXISTS "V_AcademicFee";
CREATE VIEW V_AcademicFee AS 
   SELECT 
        AcademicFee.id AS id,
        AcademicFee.recordDate AS recordDate,
        AcademicFee.lastUpdate AS lastUpdate,
        AcademicFee.academicYear AS academicYear,
        AcademicFee.amount AS amount,
        AcademicFee.description AS description,
        (SELECT SUM(PaymentFee.amount) FROM PaymentFee WHERE PaymentFee.inscription IN
        	(SELECT Inscription.id FROM Inscription WHERE Inscription.promotion IN 
        		(SELECT Promotion.id FROM Promotion WHERE  Promotion.academicFee = AcademicFee.id)
        	)
    	) AS collected,
    	(SELECT (COUNT(Inscription.id) * AcademicFee.amount) FROM Inscription WHERE Inscription.promotion IN
        	(SELECT Promotion.id FROM Promotion WHERE  Promotion.academicFee = AcademicFee.id)
    	) AS totalExpected   	
    FROM AcademicFee;
DROP VIEW IF EXISTS "V_AllocationRecipe";
CREATE VIEW V_AllocationRecipe AS 
   SELECT 
        AllocationRecipe.id AS id,
        AllocationRecipe.recordDate AS recordDate,
        AllocationRecipe.lastUpdate AS lastUpdate,
        AllocationRecipe.percent AS percent,
        AllocationRecipe.recipe AS recipe,
        AllocationRecipe.spend AS spend,
        (SELECT (SUM(V_AnnualRecipe.collected) / 100.0) * AllocationRecipe.percent FROM V_AnnualRecipe WHERE AllocationRecipe.recipe = V_AnnualRecipe.id) AS collected
    FROM AllocationRecipe;
DROP VIEW IF EXISTS "V_AllocationCost";
CREATE VIEW V_AllocationCost AS 
   SELECT 
        AllocationCost.id AS id,
        AllocationCost.recordDate AS recordDate,
        AllocationCost.lastUpdate AS lastUpdate,
        AllocationCost.amount AS amount,
        AllocationCost.academicFee AS academicFee,
        AllocationCost.annualSpend AS annualSpend,
        (SELECT 
        	((100.0 / AcademicFee.amount) * AllocationCost.amount) FROM AcademicFee WHERE AllocationCost.academicFee = AcademicFee.id
    	) AS percent,
        (SELECT 
        	((V_AcademicFee.collected / 100.0) * ((100.0 / AcademicFee.amount) * AllocationCost.amount)) 
        	FROM V_AcademicFee WHERE AllocationCost.academicFee = V_AcademicFee.id
        ) AS collected,
        (SELECT 
        	((V_AcademicFee.totalExpected / 100.0) * ((100.0 / AcademicFee.amount) * AllocationCost.amount)) 
        	FROM V_AcademicFee WHERE AllocationCost.academicFee = V_AcademicFee.id
        ) AS totalExpected
    FROM AllocationCost INNER JOIN AcademicFee ON AllocationCost.academicFee = AcademicFee.id;
DROP VIEW IF EXISTS "V_AnnualSpend";
CREATE VIEW V_AnnualSpend AS 
   SELECT 
        AnnualSpend.id AS id,
        AnnualSpend.recordDate AS recordDate,
        AnnualSpend.lastUpdate AS lastUpdate,
        AnnualSpend.academicYear AS academicYear,
        AnnualSpend.universitySpend AS universitySpend,
        (SELECT SUM(Outlay.amount) FROM Outlay WHERE Outlay.account = AnnualSpend.id) AS used,
        (SELECT (SUM(V_AllocationRecipe.collected)) FROM V_AllocationRecipe WHERE V_AllocationRecipe.spend = AnnualSpend.id) AS collectedRecipe,
       	(SELECT (SUM(V_AllocationCost.collected)) FROM V_AllocationCost WHERE V_AllocationCost.annualSpend = AnnualSpend.id) AS collectedCost
    FROM AnnualSpend;
DROP VIEW IF EXISTS "V_OtherRecipePart";
CREATE VIEW V_OtherRecipePart AS 
  SELECT 
        OtherRecipe.id AS id,
        OtherRecipe.recordDate AS recordDate,
        OtherRecipe.lastUpdate AS lastUpdate,
        OtherRecipe.collectionDate AS collectionDate,
        OtherRecipe.collectionYear AS collectionYear,
        OtherRecipe.account AS account,
        OtherRecipe.label AS label,
        OtherRecipe.amount AS amount,
        AllocationRecipe.spend  AS spend,
        UniversityRecipe.title AS title,
		(
			SELECT AllocationRecipe.percent FROM AllocationRecipe WHERE OtherRecipe.account = AllocationRecipe.recipe
		) AS percent,
		(OtherRecipe.amount / 100.0) * AllocationRecipe.percent AS part
    FROM OtherRecipe INNER JOIN AnnualRecipe ON AnnualRecipe.id = OtherRecipe.account 
    INNER JOIN AllocationRecipe ON AnnualRecipe.id = AllocationRecipe.recipe 
	INNER JOIN AnnualSpend ON AnnualSpend.id = AllocationRecipe.spend
	INNER JOIN UniversityRecipe ON UniversityRecipe.id = AnnualRecipe.universityRecipe ORDER BY OtherRecipe.id;
DROP VIEW IF EXISTS "V_PaymentFeePart";
CREATE VIEW V_PaymentFeePart AS 
	SELECT 
		PaymentFee.id AS id,
		PaymentFee.recordDate AS recordDate,
		PaymentFee.lastUpdate AS lastUpdate,
		PaymentFee.inscription AS inscription,
		PaymentFee.receivedDate AS receivedDate,
		PaymentFee.slipDate AS slipDate,
		PaymentFee.slipNumber AS slipNumber,
		PaymentFee.wording AS wording,
		PaymentFee.receiptNumber AS receiptNumber,
		PaymentFee.amount AS amount,
		(SELECT ( Student.name || ' '|| Student.postName || ' ' || Student.lastName) FROM Student WHERE Student.id = Inscription.student) AS label,
		AnnualSpend.id  AS spend,
		UniversitySpend.title AS title,
		((PaymentFee.amount / 100.0) * ((100.0 / AcademicFee.amount) * AllocationCost.amount))  AS part
	FROM PaymentFee 
		INNER JOIN Inscription ON inscription.id = PaymentFee.inscription 
		INNER JOIN Promotion ON Promotion.id = Inscription.promotion 
		INNER JOIN AcademicFee ON AcademicFee.id = Promotion.academicFee
		INNER JOIN AllocationCost ON AllocationCost.academicFee = academicFee.id 
		INNER JOIN AnnualSpend ON AnnualSpend.id = AllocationCost.annualSpend 
		INNER JOIN UniversitySpend ON UniversitySpend.id = AnnualSpend.universitySpend
		INNER JOIN Student ON Student.id = Inscription.student;
COMMIT;
