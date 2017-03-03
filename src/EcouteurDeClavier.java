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
import Dessin.Fenetre;
import java.awt.event.*;

class EcouteurDeClavier implements KeyListener {
    Fenetre f;
    TerrainGraphique tg;
    Moteur m;
    Chemin c;
    EcouteurDeClavier(Fenetre f, TerrainGraphique tg, Moteur m) {
        this.f = f;
        this.tg = tg;
        this.m = m;
        c = new Chemin();
    }
    
    
    
    public void keyPressed(KeyEvent e) {
        int i,j;
        i = m.lignePousseur;
        j = m.colonnePousseur;
        boolean actionFait = false;
        
        
        switch (e.getKeyCode()) {
        case KeyEvent.VK_UP:
            System.out.println("Up");
            actionFait = actionFait || m.actionClavier(i-1, j);
            
            break;
        case KeyEvent.VK_RIGHT:
            System.out.println("Right");
            actionFait = actionFait || m.actionClavier(i, j+1);
            break;
        case KeyEvent.VK_DOWN:
            System.out.println("Down");
            actionFait = actionFait || m.actionClavier(i+1, j);
            break;
        case KeyEvent.VK_LEFT:
            System.out.println("Left");
            actionFait = actionFait || m.actionClavier(i, j-1);
            break;
        case KeyEvent.VK_P:
            System.out.println("Résolution avec le pousseur en priorite");
            c = m.FaireResolution(1,0);
            break;
         case KeyEvent.VK_S:
            System.out.println("Résolution avec le Sac en priorite");
            c = m.FaireResolution(0,1);
            break; 
         case KeyEvent.VK_R:
            System.out.println("Solution");
            if(!c.isEmpty())
            {
                m.MontrerResolution(c);
            }
        default:
        
        }
        if(actionFait)
        {
            m.score++;
            f.tracerSansDelai(tg);
        }
    }

    public void keyReleased(KeyEvent e) {}
    public void keyTyped(KeyEvent e) {}
}
