package net.uorbutembo.views.models;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import net.uorbutembo.beans.Inscription;
import net.uorbutembo.beans.PaymentFee;
import net.uorbutembo.beans.Promotion;
import net.uorbutembo.beans.Student;
import net.uorbutembo.dao.DAOAdapter;
import net.uorbutembo.dao.DAOFactory;
import net.uorbutembo.dao.InscriptionDao;
import net.uorbutembo.dao.PaymentFeeDao;
import net.uorbutembo.dao.StudentDao;
import net.uorbutembo.swing.TableModel;

/**
 * 
 * @author Esaie MUHASA
 *
 */
public class PromotionPaymentTableModel extends AbstractTableModel {
	private static final long serialVersionUID = -2658546296487971536L;
	
	private Promotion promotion;
	private List<InscriptionDataRow> data = new ArrayList<>();
	
	private PaymentFeeDao paymentFeeDao;
	private InscriptionDao inscriptionDao;
	private StudentDao studentDao;

	public static final String [] COLUMN_NAMES = new String[] {"Matricule", "Nom, post-nom et prénom", "Téléphone", "Solde", "Dêtte"};
	public static final String [] ALL_COLUMN_NAMES = new String[] {"Matricule", "Nom", "Post-nom", "prénom", "Téléphone", "E-mail", "Date de naissance", "Liex de naissance", "Photo", "Solde", "Dêtte"};
	
	private final DAOAdapter<Inscription> inscriptionListener = new DAOAdapter<Inscription>() {
		@Override
		public synchronized void onCreate(Inscription e, int requestId) {
			if( promotion!= null && e.getPromotion().getId() == promotion.getId())
				addRow(new InscriptionDataRow(e, data.size()));
		}
		
		@Override
		public synchronized void onUpdate(Inscription e, int requestId) {
			if(promotion == null)
				return;
			
			boolean exist = false;
			for (int i = 0, count = data.size(); i < count; i++) {
				InscriptionDataRow row = data.get(i);
				if(row.getInscription().getId() == e.getId()) {
					exist= true;
					if(e.getPromotion().getId() != promotion.getId()) {//modification de la promotion
						removeRow(i);
					} else {//meme promotion
						
					}
				}
			}
			
			if(!exist && e.getPromotion().getId() == promotion.getId())
				addRow(new InscriptionDataRow(e, data.size()));
		}
		
		@Override
		public synchronized void onDelete(Inscription e, int requestId) {
			if( promotion!= null && e.getPromotion().getId() == promotion.getId()) {
				for (InscriptionDataRow row : data) {
					if(row.getInscription().getId() == e.getId()) {
						removeRow(row.index);
						break;
					}
				}
			}
		}
	};
	
	private final DAOAdapter<Student> studentListener = new DAOAdapter<Student>() {
		@Override
		public synchronized void onUpdate(Student e, int requestId) {
			for (InscriptionDataRow row : data) {
				if(row.getInscription().getStudent().getId() == e.getId()) {
					row.getInscription().setStudent(e);
					fireTableRowsUpdated(row.index, row.index);
					break;
				}
			}
		}
	};
	private InscriptionDataRowListener dataRowListener;

	public PromotionPaymentTableModel(DAOFactory factory) {
		super();
		paymentFeeDao = factory.findDao(PaymentFeeDao.class);
		inscriptionDao = factory.findDao(InscriptionDao.class);
		studentDao = factory.findDao(StudentDao.class);
		
		inscriptionDao.addListener(inscriptionListener);
		studentDao.addListener(studentListener);
	}
	
	/**
	 * construction et initalisation du listener des rows
	 * @param factory
	 * @param dataRowListener
	 */
	public PromotionPaymentTableModel(DAOFactory factory, InscriptionDataRowListener dataRowListener) {
		this(factory);
		this.dataRowListener = dataRowListener;
	}


	/**
	 * @return the promotion
	 */
	public Promotion getPromotion() {
		return promotion;
	}
	
	/**
	 * Renvoie la ligne a l'index en parametre
	 * @param index
	 * @return
	 */
	public InscriptionDataRow  getRow (int index) {
		return data.get(index);
	}
	
	/**
	 * Ajout d'une line
	 * @param row
	 */
	public void addRow (InscriptionDataRow row) {
		data.add(row);
		if(row.getPayments() == null)
			row.setPayments(new ArrayList<>());
		fireTableRowsInserted(data.size()-1, data.size()-1);
	}
	
	/**
	 * Suppression d'une ligne
	 * @param index
	 */
	public void removeRow (int index) {
		int count = getRowCount();
		
		data.get(index).dispose();
		data.remove(index);
		
		if(count == 1) 
			reload();
		else
			fireTableRowsDeleted(index, index);
	}
	
	/**
	 * insersion de plusieur ligne
	 * @param rows
	 */
	public void addRows (InscriptionDataRow...rows) {
		for (InscriptionDataRow row : rows) {
			data.add(row);
			if(row.getPayments() == null)
				row.setPayments(new ArrayList<>());
		}
		fireTableRowsInserted(data.size()- rows.length, data.size()-1);
	}

	/**
	 * @param promotion the promotion to set
	 */
	public void setPromotion(Promotion promotion) {
		this.promotion = promotion;
		reload();
	}
	
	/**
	 * Rechargement des comptes des inscrits de la dite promotion
	 */
	private synchronized void reload () {
		removeAll();
		if(inscriptionDao.checkByPromotion(promotion)) {
			List<Inscription> inscriptions = inscriptionDao.findByPromotion(promotion);
			for (int index = 0, count = inscriptions.size(); index < count ; index++) {
				InscriptionDataRow row = new InscriptionDataRow(inscriptions.get(index), index);
				if (dataRowListener != null)
					row.addDataRowListener(dataRowListener);
				data.add(row);
			}
		}
		fireTableDataChanged();
		
		for (InscriptionDataRow row : data) {
			row.load();
		}
	}
	
	private synchronized void removeAll () {
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
		return promotion.getAcademicFee() == null? 4 : 5;
	}

	@Override
	public String getColumnName(int column) {
		if(column < COLUMN_NAMES.length)
			return COLUMN_NAMES[column];
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
				return (promotion.getAcademicFee().getAmount() - data.get(rowIndex).getSold()) + " USD";
		}
		return null;
	}
	
	/**
	 * liberation des resources
	 * deconnexion aux DAO
	 */
	public void dispose () {
		for (InscriptionDataRow row : data) {
			row.dispose();
		}
		
		inscriptionDao.removeListener(inscriptionListener);
		studentDao.removeListener(studentListener);
	}

	
	/**
	 * 
	 * @author Esaie MUHASA
	 *
	 */
	public class InscriptionDataRow {
		
		private final List<InscriptionDataRowListener> listeners = new ArrayList<>();
		
		private Inscription inscription;
		private List<PaymentFee> payments;
		private int index;//l'index de la ligne dans le model du tableau
		private double sold;
		private double debs;//la dette
		
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
		 * Ecoute des evenements epecifique a un compte charger en memoire
		 * @param ls
		 */
		public void addDataRowListener (InscriptionDataRowListener ls) {
			if(!listeners.contains(ls))
				listeners.add(ls);
		}
		
		/**
		 * Desabonnement aux evenements specifique a un compte charger en memoire
		 * @param ls
		 * @return
		 */
		public boolean removeDataRowListener (InscriptionDataRowListener ls) {
			return listeners.remove(ls);
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
		 * renvoie la valeur a l'index en parametre, sous forme d'une chaine de caractere
		 * @param index
		 * @return
		 */
		public String getStringValueAt (int index) throws IndexOutOfBoundsException {
			switch (index) {
				case 0: return inscription.getStudent().getName();
				case 1: return inscription.getStudent().getPostName();
				case 2: return inscription.getStudent().getFirstName();
				case 3: return inscription.getStudent().getMatricul();
				case 4: return inscription.getStudent().getTelephone();
				case 5: return inscription.getStudent().getEmail();
				case 6: {					
					try {
						return TableModel.DEFAULT_DATE_FORMAT.format(inscription.getStudent().getBirthDate());
					} catch (Exception e) {
						return null;
					}
				}
				case 7: return inscription.getStudent().getBirthPlace();
				case 8: return inscription.getStudent().getPicture();
				case 9: return getSold()+"";
				case 10: return getDebs()+"";
			}
			throw new IndexOutOfBoundsException ("Index out of range: "+index);
		}
		
		/**
		 * Renvoie le titre de a l'index en parametre
		 * @param index
		 * @return
		 */
		public String getTitleAt (int index) {
			return ALL_COLUMN_NAMES[index];
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
		 * liberation des resources
		 * -onDispose
		 * -deconnection au DAO
		 */
		public void dispose () {
			for (InscriptionDataRowListener l : listeners) 
				l.onDispose(this);
			
			paymentFeeDao.removeListener(paymentListener);
			listeners.clear();
		}
		
		/**
		 * Rechargement des donnees de payement d'un etudiant
		 */
		public void reload () {
			if (paymentFeeDao.checkByInscription(inscription)) {
				setPayments(paymentFeeDao.findByInscription(inscription));
			} else if(payments == null) 
				payments = new ArrayList<>();
			
			for (InscriptionDataRowListener l : listeners) 
				l.onReload(this);
			
			fireTableCellUpdated(index, 3);
		}
		
		/**
		 * premier chargement du compte d'un etudiant
		 */
		private void load () {
			if (paymentFeeDao.checkByInscription(inscription)) {
				setPayments(paymentFeeDao.findByInscription(inscription));
			} else if(payments == null) 
				payments = new ArrayList<>();
			
			for (InscriptionDataRowListener l : listeners) 
				l.onLoad(this);
			
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
			debs = promotion.getAcademicFee().getAmount() - sold;
			if (fireTable)
				fireTableCellUpdated(index, 3);
		}
		
		public double getSold () {
			return sold;
		}
		
		public double getDebs () {
			return debs;
		}

	}
	
	/**
	 * Evenement particulier d'un compte d'un etudiant deja charger en memoire 
	 * @author Esaie MUHASA
	 */
	public interface InscriptionDataRowListener {
		
		/**
		 * Lors de la creation/premier chargement du compte d'un etudiant
		 * @param row
		 */
		void onLoad (InscriptionDataRow row);
		
		/**
		 * Lors de la mis en jour du compte d'un etudiant.
		 * cela est souvent due au payment
		 * @param row
		 */
		void onReload (InscriptionDataRow row);
		
		/**
		 * Lors de  la destruction du compte d'un etudiant
		 * @param row
		 */
		void onDispose (InscriptionDataRow row);
	}
}
