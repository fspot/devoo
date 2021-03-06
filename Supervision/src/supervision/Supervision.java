/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package supervision;

import ihm.Drawing;
import ihm.Window;
import views.ViewMain;

/**
 *
 * @author Mignot
 */
public class Supervision extends javax.swing.JPanel {

	private static final long serialVersionUID = 1L;

	/**
     * @param args the command line arguments
     */
    public static void main(String[] args){
    	// controleur
        Controler ctrl = new Controler();
        
        // fenetre et vues
        Window fenetre = new Window();
        Drawing d = fenetre.getDessin();
        ViewMain vm = d.getViewMain();
        
        ctrl.setViewMain(vm);
        vm.setControler(ctrl);
        fenetre.setControleur(ctrl);
        ctrl.setWindow(fenetre);
        d.setControler(ctrl);
        
        fenetre.update();
        fenetre.setVisible(true);
    }
}
