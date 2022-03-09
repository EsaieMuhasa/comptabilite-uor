/**
 * 
 */
package net.uorbutembo.views.forms;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.Box;

import net.uorbutembo.beans.AcademicFee;
import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.beans.AllocationCost;
import net.uorbutembo.beans.AnnualSpend;
import net.uorbutembo.dao.AcademicFeeDao;
import net.uorbutembo.dao.AcademicYearDao;
import net.uorbutembo.dao.AllocationCostDao;
import net.uorbutembo.dao.AnnualSpendDao;
import net.uorbutembo.dao.FeePromotionDao;
import net.uorbutembo.views.MainWindow;
import net.uorbutembo.views.components.DefaultFormPanel;
import net.uorbutembo.views.forms.FormGroupAllocationCost.AllocationCostField;

/**
 * @author Esaie MUHASA
 *
 */
public class FormAllocationCost extends DefaultFormPanel {
	private static final long serialVersionUID = -6677387371088875030L;
	
	private AcademicYear currentYear;
	private AllocationCostDao allocationCostDao;
	private AnnualSpendDao annualSpendDao;
	private AcademicFeeDao academicFeeDao;
	private FeePromotionDao feePromotionDao;
	private List<FormGroupAllocationCost> groups = new ArrayList<>();
	private Box content = Box.createVerticalBox();

	/**
	 * @param allocationCostDao
	 */
	public FormAllocationCost(MainWindow mainWindow, AllocationCostDao allocationCostDao) {
		super(mainWindow);
		this.allocationCostDao = allocationCostDao;
		this.currentYear = allocationCostDao.getFactory().findDao(AcademicYearDao.class).findCurrent();
		this.setTitle("Formulire de répartiton des fais univeristaire");
		this.academicFeeDao = allocationCostDao.getFactory().findDao(AcademicFeeDao.class);
		this.annualSpendDao = allocationCostDao.getFactory().findDao(AnnualSpendDao.class);
		this.feePromotionDao = allocationCostDao.getFactory().findDao(FeePromotionDao.class);
		
		this.getBody().add(content, BorderLayout.CENTER);
	}
	
	private void loadData() {

		List<AnnualSpend> spends = this.annualSpendDao.checkByAcademicYear(this.currentYear.getId())? this.annualSpendDao.findkByAcademicYear(currentYear) : new ArrayList<>();
		List<AcademicFee> fees = this.academicFeeDao.checkByAcademicYear(this.currentYear.getId())? this.academicFeeDao.findByAcademicYear(currentYear) : new ArrayList<>();
		content.removeAll();
		groups.clear();
		
//		for (AcademicFee fee : fees) {
//			FormGroupAllocationCost group = new FormGroupAllocationCost(this.feePromotionDao, fee, spends);
//			content.add(group);
//			groups.add(group);
//		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		int lenght = this.groups.size() * this.groups.get(0).getFields().size();
		AllocationCost [] costs = new AllocationCost [lenght];
		Date now = new Date();
		
		int index = 0;
		for (FormGroupAllocationCost group : groups) {//pour chaque groupe
			for (AllocationCostField field : group.getFields()) {//pour chaque champs du group	
				AllocationCost cost = field.getCost()!=null? field.getCost() : new AllocationCost();
				cost.setAcademicFee(group.getAcademicFee());
				cost.setAnnualSpend(field.getSpend());
				cost.setAmount(Float.parseFloat(field.getAmount().getValue()));
				cost.setRecordDate(now);
				costs[index++] = cost;
			}
		}
		
		this.allocationCostDao.create(costs);
		
	}

}
