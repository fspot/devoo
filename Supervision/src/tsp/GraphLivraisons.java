package tsp;

import java.util.ArrayList;
import dijkstra.*;
import model.*;

public class GraphLivraisons implements Graph {

	private final int timeLimit=10000;//milliseconds
	private Chemin[][] listeChemins;
	private int[][] listeCosts;
	private FeuilleDeRoute feuilleDeRoute;
	private int maxArcCost=-1;
	private int minArcCost=-1;
	private int nbVertices=0;
	
	public GraphLivraisons(FeuilleDeRoute f){
		feuilleDeRoute=f;
	}
	
	public void createGraph(){
		
		//on ajoute l'entrepot au livraisons pour connaitre le nombre de sommets
		nbVertices=feuilleDeRoute.getFullPath().size()+1;
		
		//si aucune livraison n'a été saisie
		if(nbVertices==1)
		{
			//envoyer une exception
		}
		
		listeChemins=new Chemin[nbVertices][nbVertices];
		listeCosts=new int[nbVertices][nbVertices];
		
		//index de la liste des schedules de feuilleDeRoute
		int indexSchedule=0;
		
		//solveDijkstra pour le point de départ à l'entrepot
		Node depart=feuilleDeRoute.getEntrepot().getAdresse();
		ArrayList<Node> listeArrivees=new ArrayList<Node>();
		
		//on ne prend pas un schedule qui ne possède aucune livraison
		while(feuilleDeRoute.getTimeZones().get(indexSchedule).getDeliveries().size()==0)
		{
			indexSchedule++;
		}
		
		for(Delivery d : feuilleDeRoute.getTimeZones().get(indexSchedule).getDeliveries())
		{
			listeArrivees.add(d.getDest());
		}
		
		//on fait appel à la classe static Dijkstra pour calculer les plus courts chemins
		ArrayList<Chemin> lC=Dijkstra.solve(feuilleDeRoute.getZoneGeo(), depart, listeArrivees);
		
		for(int i=0;i<lC.size();i++)
		{
			//on met à jour maxArcCost et minArcCost
			if(maxArcCost<lC.get(i).getDuration()){
				maxArcCost=(int) lC.get(i).getDuration();
				if(minArcCost==-1){
					minArcCost=maxArcCost;
				}
			}
			else if(minArcCost>lC.get(i).getDuration()){
				minArcCost=(int) lC.get(i).getDuration();
			}
			
			//on ajoute aux tableaux les chemins et couts trouvés
			listeChemins[0][i+1]=lC.get(i);
			listeCosts[0][i+1]=(int) lC.get(i).getDuration();
		}
		
		//solveDijkstra pour les livraions
		//inc permet de connaitre à quel index la première livraison d'un schedule appartient pour listeChemins et listeCosts
		int inc=1;
		
		//indexSchedule2 est l'index du schedule suivant à indexSchedule ayant des livraisons
		int indexSchedule2=indexSchedule+1;
		
		//tant qu'on est pas à la fin de la liste des schedules
		while(indexSchedule<feuilleDeRoute.getTimeZones().size())
		{
			Schedule s1=feuilleDeRoute.getTimeZones().get(indexSchedule);
			
			//on prend toutes les livraions d'un schedule
			for(int j=0;j<s1.getDeliveries().size();j++)
			{
				depart=s1.getDeliveries().get(0).getDest();
				listeArrivees.clear();
				
				//on ajoute comme point d'arrivée les livraions du même schedule que la livraison de départ
				for(int k=0;k<s1.getDeliveries().size();k++)
				{
					if(k!=j)
					{
						listeArrivees.add(s1.getDeliveries().get(k).getDest());
					}
				}
				
				//on cherche le schedule suivant indexSchedule possédant des livraisons 
				while(indexSchedule2<feuilleDeRoute.getTimeZones().size() && feuilleDeRoute.getTimeZones().get(indexSchedule2).getDeliveries().size()==0)
				{
					indexSchedule2++;
				}
				
				//si les livraisons ne font pas partie de la dernière plage horaire
				if(indexSchedule2<feuilleDeRoute.getTimeZones().size()-1)
				{
					Schedule s2=feuilleDeRoute.getTimeZones().get(indexSchedule2);
					for(Delivery d : s2.getDeliveries())
					{
						listeArrivees.add(d.getDest());
					}
				}
				else
				{
					listeArrivees.add(feuilleDeRoute.getEntrepot().getAdresse());
				}
				
				lC=Dijkstra.solve(feuilleDeRoute.getZoneGeo(), depart, listeArrivees);
				
				//l'offset permet de remplir correctement listeChemins et listeCosts
				int offset=0;
				
				//on remplit listeChemins et listeCosts
				for(int m=0;m<lC.size();m++)
				{
					if(maxArcCost<lC.get(m).getDuration())
					{
						maxArcCost=(int) lC.get(m).getDuration();
						if(minArcCost==-1)
						{
							minArcCost=maxArcCost;
						}
					}
					else if(minArcCost>lC.get(m).getDuration())
					{
						minArcCost=(int) lC.get(m).getDuration();
					}
					
					if(inc+j==inc+m)
						offset=1;
					
					//si la destination du chemin n'est pas l'entrepôt
					if((inc+m+offset)<nbVertices){
						listeChemins[inc+j][inc+m+offset]=lC.get(m);
						listeCosts[inc+j][inc+m+offset]=(int) lC.get(m).getDuration();
					}
					else
					{
						listeChemins[inc+j][0]=lC.get(m);
						listeCosts[inc+j][0]=(int) lC.get(m).getDuration();
					}
				}
			}
			inc+=s1.getDeliveries().size();
			indexSchedule=indexSchedule2;
			indexSchedule2++;
		}
	}
	
	
	public ArrayList<Chemin> calcItineraire(){
		TSP tsp=new TSP();
		int bound=nbVertices*maxArcCost;
		SolutionState retour;
		while((retour=tsp.solve(timeLimit, bound, this))==SolutionState.SOLUTION_FOUND)
		{
			bound=tsp.getTotalCost();
		}
		int[] tabPos=tsp.getPos();
		int[] tabNext=tsp.getNext();
		ArrayList<Chemin> itineraire=new ArrayList<Chemin>();
		for(int i=0;i<tabPos.length;i++)
		{
			itineraire.add(listeChemins[tabPos[i]][tabNext[i]]);
		}
		return itineraire;
	}
	
	@Override
	public void display() {
		// TODO Auto-generated method stub

	}

	@Override
	public int getMaxArcCost() {
		return maxArcCost;
	}

	@Override
	public int getMinArcCost() {
		return minArcCost;
	}

	@Override
	public int getNbVertices() {
		return nbVertices;
	}

	@Override
	public int getCost(int i, int j) throws ArrayIndexOutOfBoundsException {
		return listeCosts[i][j];
	}

	@Override
	public int[][] getCost() {
		return listeCosts;
	}

	@Override
	public ArrayList<Integer> getSucc(int i)
			throws ArrayIndexOutOfBoundsException {
		ArrayList<Integer> listeInteger=new ArrayList<Integer>();
		for(int j=0;j<nbVertices;j++)
		{
			if(listeCosts[i][j]!=0)
				listeInteger.add(j);
		}
		return listeInteger;
	}

}