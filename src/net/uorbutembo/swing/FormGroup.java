/**
 * 
 */
package net.uorbutembo.swing;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;

/**
 * @author Esaie MUHASA
 *
 */
public class FormGroup <T> extends Panel{
	private static final long serialVersionUID = 2657409418197954553L;
	
	private InputComponent<T> field;
	private JLabel help;
	
	/**
	 * @param label
	 */
	public FormGroup(InputComponent<T> field) {
		this.init(field, null);
	}
	
	/**
	 * @param field
	 * @param help
	 */
	public FormGroup(InputComponent<T> field, String help) {
		this.init(field, help);
	}
	
	/**
	 * utilitaire de creation d'un edit text
	 * @param <T>
	 * @param label
	 * @return
	 */
	public static <T> FormGroup <T> createEditText (String label) {
		EditText<T> edit = new EditText<>(label);
		FormGroup<T> form = new FormGroup<>(edit);
		return form;
	}
	
	/**
	 * @return the field
	 */
	public InputComponent<T> getField() {
		return field;
	}

	/**
	 * @return the help
	 */
	public JLabel getHelp() {
		return help;
	}
	
	public T getValue () {
		return this.field.getValue();
	}

	/**
	 * Utilitaire de creation d'u combo box
	 * @param <T>
	 * @param label
	 * @return
	 */
	public static <T> FormGroup <T> createComboBox (String label) {
		ComboBox<T> combo = new ComboBox<>(label);
		FormGroup<T> form = new FormGroup<>(combo);
		return form;
	}
	
	/**
	 * Initialisation des composents graphique
	 * @param label
	 * @param help
	 */
	private void init(InputComponent<T> field, String help) {
		this.field = field;
		this.help = new JLabel(help);
		this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		this.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.add(this.field.getComponent());
		this.add(this.help);
//		this.setBackground(FormUtil.BKG_END);
	}
	

}
