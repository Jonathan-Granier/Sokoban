
import java.util.Comparator;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author jonathan
 */
public class CheminComparator implements Comparator<Chemin>{
    public int compare(Chemin c1, Chemin c2)
    {
        int prio1= c1.get(c1.size()-1).priority;
        int prio2= c2.get(c2.size()-1).priority;
        
        if (prio1<prio2)
            return -1;
        else if (prio1==prio2)
        {                
            return 0;
        }
        return 1;
    }
}
