
import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author granijon
 */
public class Chemin extends ArrayList<Etat_Resolution> {
    static Comparator compareTo;
    
   public int compareTo(List<Etat_Resolution> that)
    {
        int a= this.get(this.size()-1).priority;
        int b= that.get(that.size()-1).priority;
        if (a<b)
            return -1;
        else if (a==b)
            return 0;
        else 
            return 1;
    }
   
   public void afficher()
   {
       
       int i;
       System.out.println("------------Chemin-----------------");
       for(i=0;i<this.size();i++)
       {
           System.out.println("Pousseur : (" + this.get(i).iP + ";" + this.get(i).jP + ") - Sac :(" + this.get(i).iS + ";" + this.get(i).jS + ") "
                   + "Poids : "+ this.get(i).poids + " Priority : "+ this.get(i).priority + " Pas : " + this.get(i).nb_Pas);
       }
   }
   
   public Chemin copie()
   {
        Chemin retour = new Chemin();
        for(int i=0;i<this.size();i++)
        {  
            retour.add(this.get(i));
        }
        return retour;
   }
   
   public void ColorTerrain(TerrainGraphique tg)
   {
       int i;
       for(i=0;i<this.size();i++)
       {
           tg.setStatut(Color.yellow, this.get(i).iS, this.get(i).jS);
       }
   }
}
