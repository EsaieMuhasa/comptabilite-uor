package net.uorbutembo.views.models;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import net.uorbutembo.beans.FeePromotion;
import net.uorbutembo.beans.Inscription;
import net.uorbutembo.beans.PaymentFee;
import net.uorbutembo.beans.Promotion;
import net.uorbutembo.dao.DAOAdapter;
import net.uorbutembo.dao.DAOFactory;
import net.uorbutembo.dao.FeePromotionDao;
import net.uorbutembo.dao.InscriptionDao;
import net.uorbutembo.dao.PaymentFeeDao;

/**
 * 
 * @author Esaie MUHASA
 *
 */
public class PromotionPaymentTableModel extends AbstractTableModel {
	private static final long serialVersionUID = -2658546296487971536L;
	
	private Promotion promotion;
	private FeePromotion feePromotion;
	private List<InscriptionDataRow> data = new ArrayList<>();
	
	private PaymentFeeDao paymentFeeDao;
	private InscriptionDao inscriptionDao;
	private FeePromotionDao feePromotionDao;


	public PromotionPaymentTableModel(DAOFactory factory) {
		super();
		paymentFeeDao = factory.findDao(PaymentFeeDao.class);
		inscriptionDao = factory.findDao(InscriptionDao.class);
		feePromotionDao = factory.findDao(FeePromotionDao.class);
	}

	/**
	 * @return the promotion
	 */
	public Promotion getPromotion() {
		return promotion;
	}

	/**
	 * @param promotion the promotion to set
	 */
	public void setPromotion(Promotion promotion) {
		this.promotion = promotion;
		reload();
	}
	
	private void reload () {
		feePromotion = feePromotionDao.findByPromotion(promotion.getId());
		removeAll();
		if(inscriptionDao.checkByPromotion(promotion)) {
			List<Inscription> inscriptions = inscriptionDao.findByPromotion(promotion);
			for (int index = 0, count = inscriptions.size(); index < count ; index++) {
				InscriptionDataRow row = new InscriptionDataRow(inscriptions.get(index), index);
				data.add(row);
			}
		}
		fireTableDataChanged();
		
		for (InscriptionDataRow row : data) {
			row.reload();
		}
	}
	
	private void removeAll () {
		for (InscriptionDataRow row : data) {
			row.dispose();
		}
		data.clear();
	}

	@Override
	public int getRowCount() {
		return data.size();
	}

	@Override
	public int getColumnCount() {
		return 5;
	}

	@Override
	public String getColumnName(int column) {
		switch (column) {
			case 0:
				return "Matricule";
			case 1:
				return "Nom, Post-nom et prenom";
			case 2:
				return "Telephone";
			case 3:
				return "Solde";
			case 4:
				return "Reste";
		}
		return super.getColumnName(column);
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		switch (columnIndex) {
			case 0:
				return data.get(rowIndex).getInscription().getStudent().getMatricul();
			case 1:
				return data.get(rowIndex).getInscription().getStudent().getFullName();
			case 2:
				return data.get(rowIndex).getInscription().getStudent().getTelephone();
			case 3:
				return data.get(rowIndex).getSold()+" USD";
			case 4:
				return (feePromotion.getAcademicFee().getAmount() - data.get(rowIndex).getSold()) + " USD";
		}
		return null;
	}

	
	/**
	 * 
	 * @author Esaie MUHASA
	 *
	 */
	public class InscriptionDataRow {
		
		private Inscription inscription;
		private List<PaymentFee> payments;
		private int index;//l'index de la ligne dans le model du tableau
		private double sold;
		
		private final DAOAdapter<PaymentFee> paymentListener = new DAOAdapter<PaymentFee>() {
			@Override
			public void onCreate(PaymentFee p, int requestId) {
				if(p.getInscription().getId() == inscription.getId()) {
					payments.add(p);
					calcul();
				}
			}

			@Override
			public void onUpdate(PaymentFee p, int requestId) {
				if(p.getInscription().getId() == inscription.getId()) {
					for (int i=0, count = payments.size(); i<count; i++) {
						PaymentFee fee = payments.get(i);
						if(fee.getId() == p.getId()) {
							payments.remove(i);
							break;
						}
					}
					payments.add(p);
					calcul();
				}
			}

			@Override
			public void onDelete(PaymentFee p, int requestId) {
				if(p.getInscription().getId() == inscription.getId()) {
					for (int i=0, count = payments.size(); i<count; i++) {
						PaymentFee fee = payments.get(i);
						if(fee.getId() == p.getId()) {
							payments.remove(i);
							break;
						}
					}
					calcul();
				}
			}
		};
		
		/**
		 * @param inscription
		 * @param index
		 */
		public InscriptionDataRow(Inscription inscription, int index) {
			super();
			this.inscription = inscription;
			this.index = index;
			paymentFeeDao.addListener(paymentListener);
		}

		/**
		 * @return the inscription
		 */
		public Inscription getInscription() {
			return inscription;
		}
		
		/**
		 * @param inscription the inscription to set
		 */
		public void setInscription(Inscription inscription) {
			this.inscription = inscription;
		}
		
		/**
		 * @return the payments
		 */
		public List<PaymentFee> getPayments() {
			return payments;
		}
		
		/**
		 * @param payments the payments to set
		 */
		public void setPayments(List<PaymentFee> payments) {
			this.payments = payments;
			calcul();
		}
		
		/**
		 * 
		 */
		public void dispose () {
			paymentFeeDao.removeListener(paymentListener);
		}
		
		/**
		 * Rechargement des donnees de payement d'un etudiant
		 */
		public void reload () {
			if (paymentFeeDao.checkByInscription(inscription)) {
				setPayments(paymentFeeDao.findByInscription(inscription));
			}
			fireTableCellUpdated(index, 3);
		}
		
		protected void calcul () {
			calcul(false);
		}
		
		protected void calcul (boolean fireTable) {
			sold = 0;
			for (PaymentFee p : payments) {
				sold += p.getAmount();
			}
			if (fireTable)
				fireTableCellUpdated(index, 3);
		}
		
		public double getSold () {
			return sold;
		}

	}
}