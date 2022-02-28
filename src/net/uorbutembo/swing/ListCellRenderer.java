/**
 * 
 */
package net.uorbutembo.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.SwingUtilities;


/**
 * @author Esaie MUHASA
 *
 */
public class ListCellRenderer extends DefaultListCellRenderer {
	private static final long serialVersionUID = 219699801372727008L;
	
	private CustomLabel renderer;

	/**
	 * 
	 */
	public ListCellRenderer(final JList<?> list ) {
		super ();
        renderer = new CustomLabel ();

        list.addMouseListener ( new MouseAdapter ()
        {
            @Override
            public void mouseReleased ( MouseEvent e )
            {
                if ( SwingUtilities.isLeftMouseButton ( e ) )
                {
                    int index = list.locationToIndex ( e.getPoint () );
                    if ( index != -1 && list.isSelectedIndex ( index ) )
                    {
                        Rectangle rect = list.getCellBounds ( index, index );
                        Point pointWithinCell = new Point ( e.getX () - rect.x, e.getY () - rect.y );
//                        Rectangle crossRect = new Rectangle ( rect.width - 9 - 5 - crossIcon.getIconWidth () / 2,
//                                rect.height / 2 - crossIcon.getIconHeight () / 2, crossIcon.getIconWidth (), crossIcon.getIconHeight () );
//                        if ( crossRect.contains ( pointWithinCell ) )
//                        {
//                            DefaultListModel<?> model = ( DefaultListModel<?> ) list.getModel ();
//                            model.remove ( index );
//                        }
                    }
                }
            }
        } );
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends Object> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		renderer.setSelected ( isSelected );
        renderer.setData (  value.toString() );
        return renderer;
	}
	
	private static class CustomLabel extends JLabel
    {
        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private static final Color selectionColor = new Color ( 82, 158, 202 );

        private boolean selected;
        private String data;

        public CustomLabel ()
        {
            super ();
            setOpaque ( false );
            setBorder ( BorderFactory.createEmptyBorder ( 0, 36 + 5, 0, 40 ) );
        }

        private void setSelected ( boolean selected )
        {
            this.selected = selected;
            setForeground ( selected ? Color.WHITE : Color.BLACK );
        }

        private void setData ( String data )
        {
            this.data = data;
            setText ( data );
        }

        @Override
        protected void paintComponent ( Graphics g )
        {
            Graphics2D g2d = ( Graphics2D ) g;
            g2d.setRenderingHint ( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );

            if ( selected )
            {
                Area area = new Area ( new Ellipse2D.Double ( 0, 0, 36, 36 ) );
                area.add ( new Area ( new RoundRectangle2D.Double ( 18, 3, getWidth () - 18, 29, 6, 6 ) ) );
                g2d.setPaint ( selectionColor );
                g2d.fill ( area );

                g2d.setPaint ( Color.WHITE );
                g2d.fill ( new Ellipse2D.Double ( 2, 2, 32, 32 ) );
            }

            g2d.setColor(Color.WHITE);
            g2d.fill ( new Ellipse2D.Double ( 5, 5, 26, 26 ) );
            if ( selected )
            {
                
            }
            else 
            {
                g2d.setPaint ( selectionColor );
                g2d.fill ( new Ellipse2D.Double ( getWidth () - 18 - 5, getHeight () / 2 - 9, 18, 18 ) );

                final String text = "" + data;
                final Font oldFont = g2d.getFont ();
                g2d.setFont ( oldFont.deriveFont ( oldFont.getSize () - 1f ) );
                final FontMetrics fm = g2d.getFontMetrics ();
                g2d.setPaint ( Color.WHITE );
                g2d.drawString ( text, getWidth () - 9 - 5 - fm.stringWidth ( text ) / 2,
                        getHeight () / 2 + ( fm.getAscent () - fm.getLeading () - fm.getDescent () ) / 2 );
                g2d.setFont ( oldFont );
            }

            super.paintComponent ( g );
        }

        @Override
        public Dimension getPreferredSize ()
        {
            final Dimension ps = super.getPreferredSize ();
            ps.height = 36;
            return ps;
        }
    }


}
