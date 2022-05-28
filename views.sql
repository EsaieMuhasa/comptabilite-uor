DROP VIEW IF EXISTS V_AnnualRecipe;
CREATE VIEW V_AnnualRecipe AS 
   SELECT 
        AnnualRecipe.id AS id,
        AnnualRecipe.recordDate AS recordDate,
        AnnualRecipe.lastUpdate AS lastUpdate,
        AnnualRecipe.academicYear AS academicYear,
        AnnualRecipe.universityRecipe AS universityRecipe,
        (SELECT SUM(OtherRecipe.amount) FROM OtherRecipe WHERE OtherRecipe.account = AnnualRecipe.id) AS collected 
    FROM AnnualRecipe;
  
-- la vue des frais universtitaire annuel
-- + facilite le calcul du montant total attandue
-- + le montant total deja collecter (payer par les etudiants inscrit) 
DROP VIEW IF EXISTS V_AcademicFee;
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
   
DROP VIEW IF EXISTS V_AllocationRecipe;
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

DROP VIEW IF EXISTS V_AllocationCost;
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
  
DROP VIEW IF EXISTS V_AnnualSpend;
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

-- Vue qui calcule la repartition des frais academique (tranche payer par un etudiant)
-- selon la configuration des repartition
DROP VIEW IF EXISTS V_PaymentFeePart;
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
		PaymentFee.location AS "location",
		(SELECT (Student.name || ' '|| Student.postName || ' ' || Student.lastName) FROM Student WHERE Student.id = Inscription.student) AS label,
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

DROP VIEW IF EXISTS V_OtherRecipePart;
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
        OtherRecipe.location AS "location",
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
	