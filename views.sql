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
    