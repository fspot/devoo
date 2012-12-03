package supervision;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

import views.ViewMain;
import views.ViewZoneGeo;

import model.Node;
import model.ZoneGeo;

public class Dessin extends JPanel implements MouseListener {
	
	protected ViewMain viewmain;
	protected Controleur controleur;
        protected Fenetre fenetre;
	
	public Dessin() {
		this.addMouseListener(this);
		viewmain = new ViewMain(this);
	}
	
	public ViewMain getViewMain() {
		return viewmain;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		int id = controleur.click(e.getX(), e.getY(), e.getButton());
		fenetre.nodeClicked(id);
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		viewmain.paint(g);
	}

	public void setControleur(Controleur ctrl) {
		controleur = ctrl;
	}

        public void setFenetre(Fenetre fenetre) {
            this.fenetre = fenetre;
        }

}
