/**
 * 
 */
package net.uorbutembo.views;

import static net.uorbutembo.views.forms.FormUtil.COLORS;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.border.EmptyBorder;

import net.uorbutembo.swing.Card;
import net.uorbutembo.swing.DefaultCardModel;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.swing.charts.DefaultPieModel;
import net.uorbutembo.swing.charts.DefaultPiePart;
import net.uorbutembo.swing.charts.PiePanel;
import net.uorbutembo.views.components.DefaultScenePanel;
import net.uorbutembo.views.components.NavbarButtonModel;
import resources.net.uorbutembo.R;

/**
 * @author Esaie MUHASA
 *
 */
public class PanelDashboard extends DefaultScenePanel {
	private static final long serialVersionUID = 4525497607858984186L;
	
	private Box panelCards = Box.createHorizontalBox();
	private Panel panelPies = new Panel(new GridLayout(1, 3, 10, 10));
	private Panel panelCurrent = new Panel(new BorderLayout());
	
	public PanelDashboard(MainWindow mainWindow) {
		super("Tableau de board", new ImageIcon(R.getIcon("dashboard")), mainWindow, false);
		
		DefaultPieModel model = new DefaultPieModel(1000, "Repartition du fric");
		DefaultPieModel model2 = new DefaultPieModel(500, "Repartition des charges");
		
		model.addPart(new DefaultPiePart(COLORS[0], COLORS[6], 100, "Construction"));
		model.addPart(new DefaultPiePart(COLORS[8], COLORS[5], 150, "Honoraire"));
		model.addPart(new DefaultPiePart(COLORS[2], COLORS[4], 200, "Recapitulation"));
		model.addPart(new DefaultPiePart(COLORS[3], COLORS[3], 50, "Machine bureau"));
		model.addPart(new DefaultPiePart(COLORS[4], COLORS[2], 30, "Histoire de valeur"));
		model.addPart(new DefaultPiePart(COLORS[5], COLORS[8], 30, "Deplacement"));
		model.addPart(new DefaultPiePart(COLORS[6], COLORS[0], 90, "Commussion"));
		
		model2.bind(model);
		
		initCards();
		
		
		panelCurrent.add(panelCards, BorderLayout.NORTH);
		panelCurrent.add(panelPies, BorderLayout.CENTER);
		panelCurrent.setBorder(BODY_BORDER);
		
		PiePanel 
			panel1 = new PiePanel(model, COLORS[6]),
			panel2 = new PiePanel(model2, COLORS[11]),
			panel3 = new PiePanel(model, COLORS[10]);
		
		panel2.setHorizontalPlacement(false);
		
		panelPies.add(panel1);
		panelPies.add(panel2);
		panelPies.add(panel3);
		
		panelPies.setBorder(new EmptyBorder(10, 0, 0, 0));
		
		this
		.addItemMenu(new NavbarButtonModel("general", "Generale"), panelCurrent)
		.addItemMenu(new NavbarButtonModel("rubrique", "Rubiques budgetaire"), new Panel())
		.addItemMenu(new NavbarButtonModel("payments", "Evolution des payement"), new Panel());
	}
	
	private void initCards() {
		
		DefaultCardModel m = new DefaultCardModel(COLORS[6], Color.WHITE);
		m.setValue("756");
		m.setTitle("Etudiants inscrits");
		m.setInfo("Nombre des etudiants inscrits");
		m.setIcon(R.getIcon("toge"));			
		panelCards.add(new Card(m));
		panelCards.add(Box.createHorizontalStrut(10));
		
		DefaultCardModel m2 = new DefaultCardModel(COLORS[11], Color.WHITE);
		m2.setValue("50240 $");
		m2.setTitle("Payer par les etudiants");
		m2.setInfo("Montant deja payer par tout les etudiants");
		m2.setIcon(R.getIcon("caisse"));			
		panelCards.add(new Card(m2));
		panelCards.add(Box.createHorizontalStrut(10));
		
		DefaultCardModel m3 = new DefaultCardModel(COLORS[10], Color.WHITE);
		m3.setValue("40500 $");
		m3.setTitle("Budget general");
		m3.setInfo("Montant que doit payer tout les etudiants");
		m3.setIcon(R.getIcon("acounting"));			
		panelCards.add(new Card(m3));
		

	}
	
	@Override
	public String getNikeName() {
		return "dashboard";
	}
}
