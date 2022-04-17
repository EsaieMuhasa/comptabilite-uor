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
	
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		help.setEnabled(enabled);
		field.getComponent().setEnabled(enabled);
	}
	
	/**
	 * utilitaire de creation d'un edit text
	 * @param <T>
	 * @param label
	 * @return
	 */
	public static <T> FormGroup <T> createTextField (String label) {
		TextField<T> edit = new TextField<>(label);
		FormGroup<T> form = new FormGroup<>(edit);
		return form;
	}
	
	/**
	 * utilitaire de creation d'un edit text
	 * @param <T>
	 * @param field
	 * @return
	 */
	public static <T> FormGroup <T> createTextField (TextField<T> field) {
		FormGroup<T> form = new FormGroup<>(field);
		return form;
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
	 * utilitaire de referencement d'un combobox dans un form-group
	 * @param <T>
	 * @param combo
	 * @return
	 */
	public static <T> FormGroup <T> createComboBox (ComboBox<T> combo) {
		FormGroup<T> form = new FormGroup<>(combo);
		return form;
	}
	
	public static FormGroup<String> createTextArea (String label) {
		TextArea area = new TextArea(label);
		FormGroup<String> form = new FormGroup<>(area);
		return form;
	}
	
	public static FormGroup<String> createTextArea (String label, int row, int cols) {
		TextArea area = new TextArea(label, row, cols);
		FormGroup<String> form = new FormGroup<>(area);
		return form;
	}
	
	public static FormGroup<String> createTextArea (TextArea area) {
		FormGroup<String> form = new FormGroup<>(area);
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
		//Dimension max = new Dimension((int) field.getComponent().getMaximumSize().getWidth(), (int)field.getComponent().getMaximumSize().getHeight()); 
		//this.setMaximumSize(max);
	}
	

}
