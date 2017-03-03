/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author granijon
 */
public class Etat_Resolution {
    int iP,jP,iS,jS,poids,priority,nb_Pas;
    public Etat_Resolution( int iP, int jP, int iS, int jS, int poids, int priority,int nb_Pas)
    {
        this.iP= iP;
        this.jP = jP;
        this.iS = iS;
        this.jS = jS;
        this.poids=poids;
        this.priority = priority;
        this.nb_Pas = nb_Pas;
    }
    
    /**
     *
     * @param that
     */
    public int compareTo( Etat_Resolution that)
    {  
        return this.priority - that.priority;
    }
    
}
