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
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import Dessin.Fenetre;

class EcouteurDeSouris implements MouseListener {
    Fenetre f;
    TerrainGraphique tg;
    Moteur m;
    int delay = 100; //milliseconds
    Timer T;
    TimerAnimation ecouteur;
    

    EcouteurDeSouris(Fenetre f, TerrainGraphique tg, Moteur m) {
        this.f = f;
        this.tg = tg;
        this.m = m;
        T = new Timer(delay, null);
        T.start();
    }

    public void mousePressed(MouseEvent e) {
        int x,y;
        int typeAction;
        x = tg.calculeColonne(e.getX());
        y = tg.calculeLigne(e.getY());
        T.stop();
        typeAction = m.actionSouris(y, x);
        // Action Pousseur
        
        if(typeAction == 1)
        {
           T = new Timer(delay, null);
            ecouteur = new TimerAnimation(f,tg,m,T);
            T.addActionListener(ecouteur);
            T.start();
        }
        // Action Sac
        else if (typeAction == 2) {
            // Exemple d'utilisation du statut d'une case : plus on passe par
            // une case, plus celle-ci est foncee.
            //tg.setStatut(tg.getStatut(y,x).darker(),y,x);
            
            m.score++;
            f.tracerSansDelai(tg);
            
        }
    }

    // Il faut aussi une implementation pour les autres methodes de l'interface
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseClicked(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
}
