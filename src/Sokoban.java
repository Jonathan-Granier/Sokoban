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

/*
@author : Granier Jonathan Et Bouvier-Denoix Gabriel
Pour l'utilisation :
Se placer dans les sources.
    Pour compiler :
        javac -classpath .:Dessin.jar Sokoban.java
    Pour executer:
        java -classpath .:Dessin.jar Dessin.Run Sokoban <Terrain>
*/


import Dessin.*;
import java.io.*;
import java.awt.*;
import java.util.*;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class Sokoban {
    public static void main(Fenetre f, Evenements e, String [] args) {
        // Desactive les mecanismes mis en oeuvre (reexecution d'une partie des
        // commandes, cache, hashage des commandes) dans Dessin lorsque les
        // commandes successives ne modifient qu'une partie de l'affichage.
        // Ici, l'affichage d'un terrain ecrase l'integralite de l'affichage
        // precedent.
        Random rand;
        Dessin.Parameters.requiresOverdraw = false;

        Terrain t = null;
        if (args.length > 0) {
            try {
                t = new Terrain(new FileInputStream(args[0]));
            } catch (FileNotFoundException ex) {
                System.err.println(ex);
                System.exit(1);
            }
        } else
            t = Terrain.defaut();
        
        
        
        
        f.setDrawAreaSize(100*t.largeur(),100*t.hauteur());
        TerrainGraphique tg = new TerrainGraphique(f, t);
        Moteur m = new Moteur(t,tg,f);
        

        e.addMouseListener(new EcouteurDeSouris(f, tg, m));
        e.addKeyListener(new EcouteurDeClavier(f,tg,m));
        
        m.RefreshCaseAccessible();
        f.tracerSansDelai(tg);
        
         while (! m.victoire) {
            e.waitForEvent();
        } 
    }
}
