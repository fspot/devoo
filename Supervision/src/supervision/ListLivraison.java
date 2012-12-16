package supervision;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import model.Schedule;

/**
 * <b>ListLivraison est la classe représentant le panel contenant le JTree.</b>
 * <p>
 * La ListLivraison est caractérisée par les informations suivantes :
 * <ul>
 * <li>Un arbre permettant la représentation des données.</li>
 * <li>Un modèle de l'arbre contenant les données de l'arbre.</li>
 * <li>Une liste de plages horaires traitées et placées dans l'arbre.</li>
 * </ul>
 * </p>
 * 
 * @see javax.swing.JPanel
 * 
 * @author H4404
 */
public class ListLivraison extends JPanel {
	
	/**
    * valeur du JTree
    */
    private JTree tree;
	/**
    * valeur du Modèle du JTree
    */
    private DefaultTreeModel treeModel;
	/**
    * valeur des plages horaires
    */
    private ArrayList<Schedule> schedules;
	/**
    * valeur du bouton Supprimer
    */
	private JButton jButtonSupprimer;


   /**
    * Constructeur ListLivraison.
    */
    public ListLivraison(){
        super(new BorderLayout());
    }
    
	/**
	 * Met à jour le bouton de supprimer de la fenetre
	 * @param jb bouton supprimer de la fenetre
	 */
    public void setjButtonSupprimer(JButton jb){
    	this.jButtonSupprimer=jb;
    }
    
	/**
	 * Teste la presence d'une addresse de livraison dans l'arbre
	 * @param addr addresse de la livraison à chercher
	 * @return retourne s'il a trouve la livraison ou non
	 */
    public boolean livExists(String addr){
		//recherche de la racine
		DefaultMutableTreeNode root = (DefaultMutableTreeNode)treeModel.getRoot();
		
		//Scan de l'arbre par plage horaire puis par children de ces plages horaires.
		DefaultMutableTreeNode nodeBuffer;
    	for(int i = 0;i<treeModel.getChildCount(root);i++){
			nodeBuffer = (DefaultMutableTreeNode)treeModel.getChild(root, i);
			for(int j = 0;j<treeModel.getChildCount(nodeBuffer);j++){
				if(addr.equals(((DefaultMutableTreeNode)treeModel.getChild(nodeBuffer, j)).toString())){
					//noeud trouve
					return true;
				}
			}
    	}
    	return false;
    }
    
	/**
	 * Recupere l'id d'une plage horaire
	 * @param schedule plage de livraison
	 * @return l'id de la plage horaire (-1 s'il ne troue rien)
	 */
    private Integer getIdSchedule(Schedule schedule){
		
    	for(int i = 0;i<schedules.size();i++){
    		if(schedule.getEndTime()==schedules.get(i).getEndTime()){
    			return i;
    		}
    	}
    	return -1;
    }	
	
	/**
	 * Vide l'arbre de son contenu
	 */
	public void clearTree()
	{
		((DefaultMutableTreeNode)treeModel.getRoot()).removeAllChildren();
		treeModel.reload();
		this.repaint();
	}	
	
	/**
	 * Met à jour la plage horaire de l'arbre qui est passée en paramètres d'entrée
	 * 
	 * @param schedule plages horaires
	 */
	public void updateOneSchedule(Schedule schedule)
	{
		this.schedules = schedules;
		//recuperation de l'indice du noeud de plage horaire voulue
    	Integer idSchedule=getIdSchedule(schedule);
		//recupere le noeud relatif a une plage horaire
		DefaultMutableTreeNode scheduleNode=(DefaultMutableTreeNode)((DefaultMutableTreeNode)treeModel.getRoot()).getChildAt(idSchedule);
		
		scheduleNode.removeAllChildren();
		for(int i = 0; i<schedule.getDeliveries().size();i++)
		{
			String addr = schedule.getDeliveries().get(i).getDest().getID().toString();
			treeModel.insertNodeInto(new DefaultMutableTreeNode(addr),scheduleNode, scheduleNode.getChildCount());
		}
	}	
	
	/**
	 * Met à jour l'arbre selon la liste de plages horaires passées en paramètre
	 * 
	 * @param schedules ArrayList de plages horaires
	 */
	public void updateAllSchedules(ArrayList<Schedule> schedules)
	{
		this.schedules = schedules;
		for(int s=0;s<schedules.size()-1;s++)
		{
			Schedule schedule = schedules.get(s);
			
			//recupere le noeud relatif a une plage horaire
			DefaultMutableTreeNode scheduleNode=(DefaultMutableTreeNode)((DefaultMutableTreeNode)treeModel.getRoot()).getChildAt(s);

			scheduleNode.removeAllChildren();
			this.repaint();
			for(int i = 0; i<schedule.getDeliveries().size();i++)
			{
				String addr = schedule.getDeliveries().get(i).getDest().getID().toString();
				treeModel.insertNodeInto(new DefaultMutableTreeNode(addr),scheduleNode, scheduleNode.getChildCount());
			}
		}
		treeModel.reload();
		this.repaint();
		expandAll();
	}	
	
	/**
	 * Développe toutes les sections de l'arbre
	 */
	public void expandAll ()
	{
		for(int i = 0;i<((DefaultMutableTreeNode)treeModel.getRoot()).getChildCount();i++)
		{
			DefaultMutableTreeNode scheduleNode=(DefaultMutableTreeNode)((DefaultMutableTreeNode)treeModel.getRoot()).getChildAt(i);
			
			for(int j = 0;j<scheduleNode.getChildCount();j++)
			{
				//récupération du chemin du noeud schedule afin de le déployer par défaut
				TreeNode[] nodes = treeModel.getPathToRoot(scheduleNode);
				TreePath path = new TreePath(nodes);
				tree.expandPath(path);
			}
		}
	}	
	
	/**
	 * Ajoute une livraison à la plage horaire selectionnee
	 * @param schedule plage horaire selectionnee
	 * @param addr addresse de la livraison à ajouter
	 */
    public void addLiv (Schedule schedule, String addr){
		//recuperation de l'indice du noeud de plage horaire voulue
    	Integer idSchedule=getIdSchedule(schedule);
		//recupere le noeud relatif a une plage horaire
		DefaultMutableTreeNode scheduleNode=(DefaultMutableTreeNode)((DefaultMutableTreeNode)treeModel.getRoot()).getChildAt(idSchedule);
		//insere un noeud dans la plage horaire trouvee au dessus
    	treeModel.insertNodeInto(new DefaultMutableTreeNode(addr),scheduleNode, scheduleNode.getChildCount());
		expandAll();
    }
	
    /**
     * Supprime la livraison selectionnée de la liste
     */
    public void remLiv (){
		//recupere le noeud selectionne
    	DefaultMutableTreeNode node = ((DefaultMutableTreeNode)tree.getLastSelectedPathComponent());
		
		//supprime le noeud en question
    	treeModel.removeNodeFromParent(node);
    }
    
    /**
     * Initialise la ListLivraison selon  la liste de Schedules passée en paramètres d'entrée
	 * 
     * @param aschedules Liste de Schedules
     */
    public void initListLivraison(ArrayList<Schedule> aschedules) {
        schedules = aschedules;
		//definition de la racine de l'arbre et declaration du model de l'arbre (contient la totalite des donnees et conditionne le comportement de l'arbre)
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Plages horaires");
        treeModel = new DefaultTreeModel(root);
		
		//creee les noeuds relatifs aux plages horaires
        for(int i=0;i<schedules.size();i++){
			DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(schedules.get(i).getSliceString());
			//treeNode.setAllowsChildren(true);
            treeModel.insertNodeInto(treeNode, root, i);
        }
        
        //creee l'arbre, edite son mode de fonctionnement, son etat initial et le met dans le scrollpane
        tree = new JTree(treeModel);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setSelectionPath(new TreePath(root));
        JScrollPane listScrollPane = new JScrollPane(tree);
        add(listScrollPane, BorderLayout.CENTER);

        /*listener qui detecte une selection sur la liste
        et enable le bouton supprimer s'il s'agit d'une livraison*/
    	tree.addTreeSelectionListener(new TreeSelectionListener() {
			
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				if(!tree.isSelectionEmpty())
				{
					//la ligne selectionnee est une livraison?
					if(((DefaultMutableTreeNode)tree.getLastSelectedPathComponent()).getLevel()>1){
						//System.out.println("liv");
						jButtonSupprimer.setEnabled(true);
					}
					else{
						jButtonSupprimer.setEnabled(false);
					}
				}
				else
				{
						jButtonSupprimer.setEnabled(false);					
				}
			}
		});
		treeModel.reload();
		this.repaint();
    }
	
	/**
	 * Met à jour le noeud sélectionné dans l'arbre.
	 * Si l'adresse passée en paramètre n'existe pas dans l'arbre, aucune action n'est effectuée
	 * 
	 * @param addr indice du noeud selectionné en format de String
	 */
	public void setSelected(String addr)
	{
		//parcours de l'arbre afin de trouver un noeud corrfespondant à l'id passé en paramètre d'entrée
		for(int i = 0;i<((DefaultMutableTreeNode)treeModel.getRoot()).getChildCount();i++)
		{
			DefaultMutableTreeNode scheduleNode=(DefaultMutableTreeNode)((DefaultMutableTreeNode)treeModel.getRoot()).getChildAt(i);
			
			for(int j = 0;j<scheduleNode.getChildCount();j++)
			{
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)scheduleNode.getChildAt(j);
				if(addr.equals(node.toString()))
				{
					//récupération du chemin du noeud schedule afin de le selectionner
					TreeNode[] nodes = treeModel.getPathToRoot(node);
					TreePath path = new TreePath(nodes);
					tree.setSelectionPath(path);
				}
			}
		}
	}
	
	/**
	 * Retourne l'arbre
	 * @return l'arbre
	 */
    public JTree getList() {
        return tree;
    }

	/**
	 * Retourne le model de l'arbre
	 * @return le model de l'arbre
	 */
    public DefaultTreeModel getListModel() {
        return treeModel;
    }
    
}