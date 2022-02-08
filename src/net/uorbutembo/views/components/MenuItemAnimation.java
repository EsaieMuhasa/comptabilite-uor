package net.uorbutembo.views.components;

import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.TimingTarget;
import org.jdesktop.animation.timing.TimingTargetAdapter;

import net.miginfocom.swing.MigLayout;

/**
 * 
 * @author Esaie MUHASA
 * Utilitaire d'animation d'un menu ayant des sous-menu
 */
class MenuItemAnimation {

    private final MigLayout layout;
    private final MenuItem item;
    private Animator animator;

    /**
     * construcrteur d'initialisation
     * @param layout le layout du conteneur de l'item du menu
     * @param item
     */
    public MenuItemAnimation(MigLayout layout, MenuItem item) {
        this.layout = layout;
        this.item = item;
        initAnimator(200);
    }

    /**
     * configuration de l'animation
     * @param duration
     */
    private void initAnimator(int duration) {
        int height = item.getPreferredSize().height;
        
        TimingTarget target = new TimingTargetAdapter() {
            @Override
            public void timingEvent(float fraction) {
                float h;
                if (item.isOpen()) {
                	//hauteur d'un sous-menu x le nombre de sous-menu
                    h = (35 * item .getModel().countItems()) + ((height - 40) * fraction);
                    item.setAlpha(fraction);
                } else {
                    h = 40 + ((height - 40) * (1f - fraction));
                    item.setAlpha(1f - fraction);
                }
                layout.setComponentConstraints(item, "h " + h + "!");
                item.revalidate();
                item.repaint();
            }
        };
        
        animator = new Animator(duration, target);
        animator.setResolution(0);
        animator.setDeceleration(0.5f);
    }

    /**
     * Demande d'annimer le menu
     */
    public void toggleMenu () {
    	animator.start();    	
    }
    
}
