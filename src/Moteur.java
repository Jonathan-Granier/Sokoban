
import Dessin.Fenetre;
import Dessin.Point;
import java.awt.Color;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;

/*
Sokoban - implementation manuelle et automatique du celebre jeu
Copyright (C) 2009 Guillaume Huard
Ce programme est libre, vous pouvez le redistribuer et/ou le modifier selon les
termes de la Licence Publique Generale GNU publiee par la Free Software
Foundation (version 2 ou bien toute autre version ulterieure choisie par vous).

Ce programme est distribue car potentiellement utile, mais SANS AUCUNE
GARANTIE, ni explicite ni implicite, y compris les garanties de
commercialisation ou d'adaptation dans un but specifique. Reportez-vous a la
Licence Publique Generale GNU pour plus de details.

Vous devez avoir recu une copie de la Licence Publique Generale GNU en meme
temps que ce programme ; si ce n'est pas le cas, ecrivez a la Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307,
Etats-Unis.

Contact: Guillaume.Huard@imag.fr
         ENSIMAG - Laboratoire LIG
         51 avenue Jean Kuntzmann
         38330 Montbonnot Saint-Martin
*/
class Moteur {
    Terrain t;
    TerrainGraphique tg;
    Fenetre f;
    
    int lignePousseur, colonnePousseur;
    boolean victoire;
    int score;
    Point Objectif;  
    Point Sac;
    int CaseAccessible[][];
    int CasePossibleSac[][];
    int ConfigurationVisite[][][][];
    // Variable pour le tableau CaseAccessible
    static int accessible = -1;
    static int non_accessible = -2;
    int poids_sac = 1;
    int poids_pousseur = 1;
    int nb_Test = 0;
    
    
    public Stack<Point> chemin;

    // Constructeur Moteur
    Moteur(Terrain t, TerrainGraphique tg, Fenetre f) {
        this.t = t;
        this.tg = tg;
        this.f = f;
        for (int i=0; i<t.hauteur(); i++)
        {
            for (int j=0; j<t.largeur(); j++)
            {
                if (t.consulter(i,j).contient(Case.POUSSEUR)) {
                    lignePousseur = i;
                    colonnePousseur = j;
                }
                if(t.consulter(i,j).contient(Case.BUT))
                {
                    Objectif = new Point(i,j);
                }
                if(t.consulter(i,j).contient(Case.SAC))
                {
                    Sac = new Point(i,j);
                }
            }
        }   
                
        victoire = false;
        score = 0;
        
        RefreshCaseAccessible();
    }
    
    
    /*
    actionSouris
    Prends un position i,j qui est la position du clique de la souris et agit en consequence.
    Retour de actionSouris : 
    0: Rien n'a été fait
    1: Le pousseur doit être bouger
    2: un sac a été bougé
    */
    public int actionSouris(int i, int j) {
        
        if(EstCaseAccessible(i,j)){
            CheminLePlusCours(i,j);
            return 1;
        }else if (t.consulter(i,j).contient(Case.SAC))
        {
            boolean PousserFait = false;
            switch (JoueurACoter(i, j))
            {
            case 1:
                PousserFait = PousserFait || Pousser(i,j,i-1,j);     
                break;
            case 2:
                PousserFait = PousserFait || Pousser(i,j,i,j+1);
                break;
            case 3:
                PousserFait = PousserFait || Pousser(i,j,i+1,j);
                break;
            case 4:
                PousserFait = PousserFait || Pousser(i,j,i,j-1);
                   break;
            default: return 0;
            }
            if(PousserFait)
            {
                return 2;
            }
        }
        return 0;
    }
    
    
    public boolean actionClavier(int i, int j) {
        if(0<=i && i<t.hauteur() && 0<=j && j<t.largeur())
        {
            if (t.consulter(i,j).estLibre()) 
            {
                Case courante = t.consulter(lignePousseur, colonnePousseur);
                courante = courante.retrait(Case.POUSSEUR);
                t.assigner(courante, lignePousseur, colonnePousseur);

                courante = t.consulter(i, j);
                courante = courante.ajout(Case.POUSSEUR);
                t.assigner(courante, i, j);

                lignePousseur = i;
                colonnePousseur = j;

                return true;
            }else if (t.consulter(i,j).contient(Case.SAC))
            {
               return Pousser(i,j,i+(i-lignePousseur),j+(j-colonnePousseur));
            }
        }
        return false;
    }
    
    /*
    Met à jour la position du pousseur en le positionnant au point suivant de la pile chemin.
    */
    
    public void MajPositionPousseur()
    {
        int i,j;
        Point p_courant = chemin.pop();
        i = p_courant.x;
        j = p_courant.y;
        
        Case courante = t.consulter(lignePousseur, colonnePousseur);
        courante = courante.retrait(Case.POUSSEUR);
        t.assigner(courante, lignePousseur, colonnePousseur);

        courante = t.consulter(i, j);
        courante = courante.ajout(Case.POUSSEUR);
        t.assigner(courante, i, j);

        lignePousseur = i;
        colonnePousseur = j;
    }
    
  
    /*
    Prends en parametre une case 
    */
    public boolean Pousser(int src_i ,int src_j, int dest_i, int dest_j)
    {
        //System.out.println("Pousser ?" + src_i + "-" + src_j + "/" + dest_i + "-"+ dest_j);
        if(EstDansTerrain(dest_i,dest_j)&& t.consulter(dest_i,dest_j).estLibre())
        { 
            if (t.consulter(dest_i, dest_j).contient(Case.BUT))
            {
                t.assigner(Case.SAC_SUR_BUT, dest_i, dest_j);
                victoire =gagne();
            }
            else
            {
                t.assigner(Case.SAC, dest_i, dest_j);
            }
            
            if(t.consulter(src_i, src_j).contient(Case.SAC_SUR_BUT))
            {
                t.assigner(Case.POUSSEUR_SUR_BUT, src_i, src_j);
            }
            else
            {
                t.assigner(Case.POUSSEUR, src_i, src_j);
            }
            
            if( t.consulter(src_i-(dest_i-src_i), src_j-(dest_j-src_j)).contient(Case.POUSSEUR_SUR_BUT))
            {
                t.assigner(Case.BUT, src_i-(dest_i-src_i), src_j-(dest_j-src_j));
            }else
            {
                t.assigner(Case.LIBRE, src_i-(dest_i-src_i), src_j-(dest_j-src_j));
            }
            lignePousseur = src_i;
            colonnePousseur = src_j;
            RefreshCaseAccessible();
            return true;
        }
         //System.out.println("ON POUSSE PAS!");
        return false;
        
        
    }
    
      
    /*Fonction qui renvoie si le joueur est a coté du sac et dans quel position il se trouve 
    -1 : Non
    1: N
    2: E
    3: S
    4: O
  
    */
    
    public int JoueurACoter(int Sac_i, int Sac_j)
    {
        
        if( EstDansTerrain(Sac_i+1,Sac_j)&& CasePousseur(Sac_i+1,Sac_j))
        {
            return 1;
        }
        else if( EstDansTerrain(Sac_i,Sac_j-1)&& CasePousseur(Sac_i,Sac_j-1))
        {
            return 2;
        }
        else if( EstDansTerrain(Sac_i-1,Sac_j)&& CasePousseur(Sac_i-1,Sac_j))
        {
            return 3;
        }
        else if( EstDansTerrain(Sac_i,Sac_j+1)&& CasePousseur(Sac_i,Sac_j+1))
        {
            return 4;
        }
        return -1;
    }
    
    
    public boolean CasePousseur(int i, int j)
    {
        return (t.consulter(i, j).contient(Case.POUSSEUR) || t.consulter(i, j).contient(Case.POUSSEUR_SUR_BUT));
    }
    
    public boolean EstDansTerrain (int i, int j)
    {
        return ( (0 <= i) && (i<t.hauteur()) && (0 <= j && j < t.largeur())) ;
    }
    
    public boolean gagne(){
        boolean victoire=true;
        int i=0, j=0;
        while(victoire && i < t.hauteur())
        {
            while(victoire && j< t.largeur())
            {
                
                // Soit j'ai pas une case But , soit j'ai une case but et il y a un sac dessus
                victoire = (! t.consulter(i,j).contient(Case.BUT)) || (t.consulter(i, j).contient(Case.SAC_SUR_BUT)); 
                j++;
            }
            i++;
            j=0;
        }
        if(victoire)
        {
            System.out.println("GAGNE"); 
            System.out.println("Score : "+score);
        }
        return victoire;
        
    }
    
    public boolean EstCaseAccessible(int i,int j)
    {
        return CaseAccessible[i][j] ==  accessible;
    }
    
    
    // Dijkstra
    /* 
    * Prends un point d'arrivé , applique Dijkstra depuis le Pousseur jusqu'au point d'arrivé.
    * Ensuite , par du point d'arrive dans le tableau de poids , et remonte 
    * jusqu'au pousseur en construissant une pile qui formera le chemin
    */
    public int CheminLePlusCours(int dest_i, int dest_j)
    {

        Point p_courant,p_next, p_dest;
        
        int[][] Poids = CopieCaseAccessible();
       
        Queue<Point> file = new LinkedList<Point>();
        Stack<Point> succ = new Stack();
        chemin = new Stack();
        
        p_dest = new Point(dest_i,dest_j);
        
        p_courant = new Point(lignePousseur,colonnePousseur);
        file.add(p_courant);
        Poids[lignePousseur][colonnePousseur] = 0;
        
        while(!file.isEmpty() && (p_courant.x != p_dest.x || p_courant.y != p_dest.y))
        {
            p_courant = file.poll();
            if (p_courant.x != p_dest.x || p_courant.y != p_dest.y)
            {
                succ = TrouverSucc(p_courant);
                while(!succ.isEmpty())
                {
                    p_next = succ.pop();
                    // Si la case p_next n'est pas marqué ou que son poids est supp au poids de p_courant +1 
                    if(Poids[p_next.x][p_next.y] == accessible || Poids[p_next.x][p_next.y] > Poids[p_courant.x][p_courant.y]+1)
                    {
                        Poids[p_next.x][p_next.y] = Poids[p_courant.x][p_courant.y]+1;
                        file.add(p_next);
                    } 
                }   
            }
                  
        }
        // Faire UNE pile de point pour le chemin
/*
        for(int i=0;i<t.hauteur();i++)
        {
            System.out.print("|");
            for(int j=0;j<t.largeur();j++)
            {
               System.out.print(Poids[i][j]+"|");
               
            }
            System.out.println();
        }
*/       
        p_courant = p_dest;
        Point p_pousseur = new Point(lignePousseur,colonnePousseur);
        while(p_courant.x != p_pousseur.x || p_courant.y != p_pousseur.y )
        {
            chemin.add(p_courant);
            p_courant = NextPoint(Poids,p_courant);
        }
        
        return Poids[p_dest.x][p_dest.y];
        
    }
    
    // Copie le contenu de CaseAccessible dans un nouveau Tableau
    private int[][] CopieCaseAccessible()
    {
        int[][] Copie = new int[t.hauteur()][t.largeur()];
        for(int i= 0; i<t.hauteur();i++)
        {
            for(int j=0; j<t.largeur();j++)
            {
                Copie[i][j]=CaseAccessible[i][j];
            }
        }
        
        return Copie;
    }
    
    // Regarde les 4 cases autour de p_courant et les ajoute à la pile si ils sont accessible.
    // Calcule les successeurs de p_courant
    private Stack TrouverSucc(Point p_courant)
    {
        Stack<Point> succ = new Stack();
        int i,j;
        i = p_courant.x;
        j = p_courant.y-1;
        if(j >=0 && CaseAccessible[i][j] ==  accessible)
        {
            succ.add(new Point(i,j));
        }
        
        i = p_courant.x;
        j = p_courant.y+1;
        if(j < t.largeur() && CaseAccessible[i][j] ==  accessible)
        {
            succ.add(new Point(i,j));
        }
        
        i = p_courant.x-1;
        j = p_courant.y;
        if(i >= 0 && CaseAccessible[i][j] ==  accessible)
        {
            succ.add(new Point(i,j));
        }
        
        i = p_courant.x+1;
        j = p_courant.y;
        if(i < t.hauteur() && CaseAccessible[i][j] ==  accessible)
        {
            succ.add(new Point(i,j));
        }   
        return succ;
    }
    
    
    // Prend un Point p_courant et un tableau Poids et retourne le Point avec le poids le plus faible adjacent à p_courant
    private Point NextPoint(int[][] Poids, Point p_courant)
    {
        Point p_next = p_courant;
        int i,j;
        
        i = p_courant.x;
        j = p_courant.y-1;
        if(j >=0 && Poids[i][j] >= 0 && Poids[i][j] < Poids[p_next.x][p_next.y])
        {
            p_next = new Point(i,j);
        }
        
        i = p_courant.x;
        j = p_courant.y+1;
        if(j < t.largeur()  && Poids[i][j] >= 0 && Poids[i][j] < Poids[p_next.x][p_next.y])
        {
             p_next = new Point(i,j);
        }
        
        i = p_courant.x-1;
        j = p_courant.y;
        if(i >= 0  && Poids[i][j] >= 0 && Poids[i][j] < Poids[p_next.x][p_next.y])
        {
            
            p_next = new Point(i,j);
        }
        
        i = p_courant.x+1;
        j = p_courant.y;
        if(i < t.hauteur() && Poids[i][j] >= 0 && Poids[i][j] < Poids[p_next.x][p_next.y])
        {
             p_next = new Point(i,j);
        }
        
        
        return p_next;
        
    }
    
    // Met à jour les caseAccessible par le pousseur
    public void RefreshCaseAccessible()
    {
        int i,j;
        CaseAccessible= new int[t.hauteur()][t.largeur()];
        
        
        Queue<Point> file;
        file = new LinkedList<Point>();
        Point p_courant,p_new;
        for(i=0;i<t.hauteur();i++)
        {
            for(j=0;j<t.largeur();j++)
            {
               CaseAccessible[i][j] =  non_accessible;
               tg.setStatut(Color.red, i, j);
            }
        }
        
        i = lignePousseur;
        j = colonnePousseur;
        CaseAccessible[i][j] =  accessible;
        tg.setStatut(Color.green, i, j);
        
        file.add(new Point(i,j));
        while (! file.isEmpty())
        {
            p_courant = file.poll();
          
            i = p_courant.x;
            j = p_courant.y-1;
            MajQueue(i,j,file,CaseAccessible);
            
            i = p_courant.x;
            j = p_courant.y+1;
            MajQueue(i,j,file,CaseAccessible);
             
            i = p_courant.x-1;
            j = p_courant.y;
            MajQueue(i,j,file,CaseAccessible);
            i = p_courant.x+1;
            j = p_courant.y;
            MajQueue(i,j,file,CaseAccessible);
        }
       
    }
    // Met à jour la file 
    private void MajQueue(int i, int j, Queue<Point> file, int CaseAccessible[][])
    {
        Point p_new;
        if(EstDansTerrain(i,j))
        {
            if(t.consulter(i,j).estLibre() && CaseAccessible[i][j] !=  accessible)
            {
                p_new = new Point(i,j);
                file.add(p_new);
                CaseAccessible[i][j] =  accessible;
                tg.setStatut(Color.green, i, j);
            }
        }
    }
    
    
    public Chemin FaireResolution(int PoidsPousseur,int PoidsSac)
    {
        poids_pousseur = PoidsPousseur;
        poids_sac = PoidsSac;
        nb_Test = 0;
        Chemin c = Resolution();
        if(c.isEmpty())
        {
            System.out.println("Aucune Solution");
        }
        else
        {
            
            Stat(c);
            c.afficher();
        }
        return c;
    }
    
    
    
    
    
    
    public Chemin Resolution()
    {
        Comparator<Chemin> comparator = new CheminComparator();
        PriorityQueue<Chemin> file = new PriorityQueue(comparator);
        Etat_Resolution etat_courant;
        Stack<Etat_Resolution> succ = new Stack();
        
        
        InitTerrainResolution();

        //structure (X/Y sac/ X/Y pousseur/ poids)
        //enfiler ( X/Y base, X/Y pousseur, Poids)
        // récupérer les coord Sac/pousseur
        etat_courant= new Etat_Resolution (lignePousseur,colonnePousseur,Sac.x,Sac.y,0,0,0);
        ConfigurationVisite[lignePousseur][colonnePousseur][Sac.x][Sac.y] = 1;
        Chemin chemin_courant = new Chemin();
        Chemin clone;
        
        chemin_courant.add(etat_courant);
        file.add(chemin_courant);
        //tant que la file est pas vide,
        while(!file.isEmpty())
        {
          //  System.out.println("Début de boucle algo");
        //    défiler le points de H min
            chemin_courant = file.poll();
            nb_Test++;
            etat_courant = chemin_courant.get(chemin_courant.size()-1);
           
            if(t.consulter(etat_courant.iS, etat_courant.jS).contient(Case.BUT))
            {
                MajTerrain(chemin_courant);
                return chemin_courant;
            }
             MajTerrain(chemin_courant);
        //    générer les succ
            succ = generer_succ(etat_courant);  
        //    mettre à jour graph chemin plus court (avec pred)
        //    les enfiler
            
            while(!succ.isEmpty())
            {
                clone = (Chemin)chemin_courant.clone();
                clone.add(succ.pop());
                file.add(clone);
            }
            
            
            
            
        }
        
        
       return new Chemin(); 
    }
    
    public Stack<Etat_Resolution> generer_succ(Etat_Resolution pred)
    {
        Stack<Etat_Resolution>  succ = new Stack();
        int i,j;
        i = pred.iS;
        j = pred.jS;
        
        
        if(j-1 >=0 && j+1 < t.largeur() && (t.consulter(i,j-1).estLibre() || t.consulter(i, j-1).contient(Case.POUSSEUR)) 
                && CaseAccessible[i][j+1] ==  accessible && CasePossibleSac[i][j-1]==2)
        {
            
            int nb_Pas = CheminLePlusCours(i,j+1)+1;
            int poids = MajPoids(pred,nb_Pas);
            
            if(ConfigurationVisite[i][j][i][j-1] == 0 || ConfigurationVisite[i][j][i][j-1] > poids+1)
            {
                succ.add(new Etat_Resolution(i,j,i,j-1,poids,MajPrio(poids,i,j-1),pred.nb_Pas+nb_Pas));
                ConfigurationVisite[i][j][i][j-1] = poids + 1;
            }
          
        }
        if(i-1 >=0 && i+1 < t.hauteur() && (t.consulter(i-1,j).estLibre() ||  t.consulter(i-1,j).contient(Case.POUSSEUR)) 
                &&  CaseAccessible[i+1][j] ==  accessible && CasePossibleSac[i-1][j]==2)
        {
            int nb_Pas = CheminLePlusCours(i+1,j)+1;
            int poids = MajPoids(pred,nb_Pas);
            
            if(ConfigurationVisite[i][j][i-1][j] == 0 || ConfigurationVisite[i][j][i-1][j] > poids+1)
            {
                succ.add(new Etat_Resolution(i,j,i-1,j,poids,MajPrio(poids,i-1,j),pred.nb_Pas+nb_Pas));
                ConfigurationVisite[i][j][i-1][j] =poids+1;
            }
        }
        
        if(j-1 >=0 && j+1 < t.largeur() && (t.consulter(i,j+1).estLibre() || t.consulter(i,j+1).contient(Case.POUSSEUR)) 
                &&  CaseAccessible[i][j-1] ==  accessible && CasePossibleSac[i][j+1]==2)
        {
            int nb_Pas = CheminLePlusCours(i,j-1)+1;
            int poids = MajPoids(pred,nb_Pas);
            
            if(ConfigurationVisite[i][j][i][j+1] == 0 || ConfigurationVisite[i][j][i][j+1] > poids+1)
            {
                succ.add(new Etat_Resolution(i,j,i,j+1,poids,MajPrio(poids,i,j+1),pred.nb_Pas+nb_Pas));
                ConfigurationVisite[i][j][i][j+1] = poids+1;
            }
            
            
        }
        
        if(i-1 >=0 && i+1 < t.hauteur() && (t.consulter(i+1,j).estLibre() || t.consulter(i+1,j).contient(Case.POUSSEUR))
                &&  CaseAccessible[i-1][j] ==  accessible && CasePossibleSac[i+1][j]==2)
        {
            int nb_Pas = CheminLePlusCours(i-1,j)+1;
            int poids = MajPoids(pred,nb_Pas);
            if(ConfigurationVisite[i][j][i+1][j] == 0 || ConfigurationVisite[i][j][i+1][j] > poids+1)
            {
                succ.add(new Etat_Resolution(i,j,i+1,j,poids,MajPrio(poids,i+1,j),pred.nb_Pas+nb_Pas));
                ConfigurationVisite[i][j][i+1][j] = poids + 1;
            }
            
        }
                
        return succ;
    }
    
    public int MajPoids(Etat_Resolution pred,int nb_Pas)
    {
        return pred.poids + nb_Pas*poids_pousseur+poids_sac;
    }
    public int MajPrio(int poids, int iS,int jS)
    {
        if(poids_pousseur+poids_sac <= 0)
        {
            return poids+(Math.abs(iS-Objectif.x)+Math.abs(jS-Objectif.y));
        }
        return poids+(Math.abs(iS-Objectif.x)+Math.abs(jS-Objectif.y))*(poids_pousseur+poids_sac);
    }
    
    public void MajTerrain(Chemin c)
    {
        
        
        Etat_Resolution e = c.get(c.size()-1);
// Retrait du Sac et du Pousseur
        t.assigner(t.consulter(Sac.x, Sac.y).retrait(Case.SAC), Sac.x, Sac.y);
        t.assigner(t.consulter(lignePousseur, colonnePousseur).retrait(Case.POUSSEUR),  lignePousseur,colonnePousseur);
        
        // Maj Sac
       
        
        t.assigner(Case.SAC, e.iS, e.jS);
        Sac.x=e.iS;
        Sac.y=e.jS;
        
        // Maj Pousseur
       
        t.assigner(Case.POUSSEUR, e.iP, e.jP);
        t.assigner(t.consulter(e.iP, e.jP).ajout(Case.POUSSEUR),  e.iP,e.jP);
        
        lignePousseur = e.iP;
        colonnePousseur = e.jP;
        
        //On recalcule les case accessible et on affiche le nouveau terrain
        RefreshCaseAccessible();
        c.ColorTerrain(tg);
        f.tracer(tg);
    }
    
    
    /* Regarde le terrain et met à jour CasePossibleSac pour indiqué quel case son accessible ou non
    0: Case occupé
    1: Case Impossible
    2: Case Possible
    
    */
    public void InitTerrainResolution()
    {
        CasePossibleSac= new int[t.hauteur()][t.largeur()];
        ConfigurationVisite = new int[t.hauteur()][t.largeur()][t.hauteur()][t.largeur()];
        int i,j;
        for ( i=0; i<t.hauteur(); i++)
        {
            for ( j=0; j<t.largeur(); j++)
            {
               CasePossibleSac[i][j] = 2;
               // Obstacle
               if(t.consulter(i, j).contient(Case.OBSTACLE))
               {
                   CasePossibleSac[i][j]=0;
               }
               // Bord du terrain
               else if(i==0 && i!=Objectif.x)
               {
                    CasePossibleSac[i][j] = 1;
               }
               else if(i==t.hauteur()-1 && i!=Objectif.x)
               {
                   CasePossibleSac[i][j] = 1;
               }
               else if(j==0 && j!= Objectif.y)
               {
                   CasePossibleSac[i][j] = 1;
               }
               else if(j==t.largeur()-1 && j!=Objectif.y)
               {
                   CasePossibleSac[i][j] = 1;
               }
            }
        }    
        
        /*        
	for(i=0; i < t.hauteur(); i ++)
	{
		int case_avant = -1;
		j=0;
		while( case_avant < t.largeur())
		{
			j = case_avant +1;
			while(j < t.largeur() && !t.consulter(i,j).contient(Case.OBSTACLE))
			{
				j++;
			}
			int case_apres = j;
			j= case_avant+1;
			boolean bloque=true;
                        while(j < case_apres && bloque)
			{
				//bloqué = case_a_coté = mur && case not but);
				bloque = ((i==0 || i == t.hauteur() -1) || t.consulter(i-1,j).contient(Case.OBSTACLE) ||t.consulter(i+1,j).contient(Case.OBSTACLE) && !t.consulter(i,j).contient(Case.BUT));
                                j++;
                        }
			if(bloque)
			{
				for(j = case_avant+1; j < case_apres; j++)
				{
					CasePossibleSac[i][j] = 1;
				}
			}
			case_avant = case_apres;
		}
	}

	for(j=0; j < t.largeur(); j ++)
	{
		int case_avant = -1;
		i=0;
		while( case_avant < t.hauteur())
		{
			i = case_avant +1;
			while(i < t.hauteur() && !t.consulter(i,j).contient(Case.OBSTACLE))
			{
				i++;
			}
			int case_apres = i;
			i= case_avant+1;
                        boolean bloque = true;
			while(i < case_apres && bloque)
			{
				//bloqué = case_a_coté = mur && case not but);
				bloque = ((j==0 || j == t.hauteur() -1) || t.consulter(i,j-1).contient(Case.OBSTACLE) ||t.consulter(i,j+1).contient(Case.OBSTACLE) && !t.consulter(i,j).contient(Case.BUT));
                                i++;
                        }
			if(bloque)
			{
				for(i = case_avant+1; i < case_apres; i++)
				{
					CasePossibleSac[i][j] = 1;
				}
			}
			case_avant = case_apres;
		}
	}
	for(i=0;i<t.hauteur(); i++)
	{
		for(j=0; j<t.largeur(); j++)
		{
			if(est_coins(i,j))
				CasePossibleSac[i][j]=1;
		}
	}
*/

}

    public boolean est_coins (int i, int j)
    {
            boolean N=true,S=true,E=true,O=true;
            if (i > 0)
                    N = t.consulter(i-1, j).contient(Case.OBSTACLE);
            if (j >0)
                    O = t.consulter(i, j-1).contient(Case.OBSTACLE);
            if( i < t.hauteur()-1)
                    S = t.consulter(i+1,j).contient(Case.OBSTACLE);
            if( j < t.largeur()-1)
                    E = t.consulter(i, j+1).contient(Case.OBSTACLE);
            return (N && O) || (N && E) || ( S && O) || (S && E);
    }
    
    public void Stat(Chemin c)
    {
        Etat_Resolution last_conf = c.get(c.size()-1);
        System.out.println("##########STAT#############");
        System.out.println("Pousseur : "+ (last_conf.nb_Pas));
        System.out.println("Sac : "+ c.size());
        System.out.println("Nombre de test fait : "+ nb_Test);
        
    }
    
    public void MontrerResolution(Chemin c)
    {
        Etat_Resolution e = c.get(0);
        
        
        t.assigner(t.consulter(Sac.x, Sac.y).retrait(Case.SAC), Sac.x, Sac.y);
        t.assigner(t.consulter(lignePousseur, colonnePousseur).retrait(Case.POUSSEUR),  lignePousseur,colonnePousseur);

        // Maj Sac


        t.assigner(Case.SAC, e.iS, e.jS);
        Sac.x=e.iS;
        Sac.y=e.jS;

        // Maj Pousseur

        t.assigner(Case.POUSSEUR, e.iP, e.jP);
        t.assigner(t.consulter(e.iP, e.jP).ajout(Case.POUSSEUR),  e.iP,e.jP);

        lignePousseur = e.iP;
        colonnePousseur = e.jP;
        
        
        
        
        
        for(int i=1;i<c.size();i++)
        {
            e = c.get(i);
// Retrait du Sac et du Pousseur
            CheminLePlusCours(e.iP-(e.iS-e.iP),e.jP-(e.jS - e.jP));

            while(!chemin.isEmpty())
            {
                Point p= chemin.pop();
                t.assigner(t.consulter(lignePousseur, colonnePousseur).retrait(Case.POUSSEUR),  lignePousseur,colonnePousseur); 
                t.assigner(Case.POUSSEUR, p.x, p.y);
                t.assigner(t.consulter(p.x, p.y).ajout(Case.POUSSEUR),  p.x,p.y);
                lignePousseur = p.x;
                colonnePousseur = p.y;
                f.tracer(tg);
            }
            t.assigner(t.consulter(Sac.x, Sac.y).retrait(Case.SAC), Sac.x, Sac.y);
            t.assigner(t.consulter(lignePousseur, colonnePousseur).retrait(Case.POUSSEUR),  lignePousseur,colonnePousseur);

            // Maj Sac


            t.assigner(Case.SAC, e.iS, e.jS);
            Sac.x=e.iS;
            Sac.y=e.jS;

            // Maj Pousseur

            t.assigner(Case.POUSSEUR, e.iP, e.jP);
            t.assigner(t.consulter(e.iP, e.jP).ajout(Case.POUSSEUR),  e.iP,e.jP);

            lignePousseur = e.iP;
            colonnePousseur = e.jP;
            // Maj Pousseur

            
            f.tracer(tg);
            
        }
    }
}




