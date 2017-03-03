/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import Dessin.Fenetre;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

/**
 *
 * @author granijon
 */
public class TimerAnimation implements ActionListener  {
   
    Timer timer;
    Moteur m;
    Fenetre f;
    TerrainGraphique tg;
    public TimerAnimation (Fenetre f, TerrainGraphique tg,Moteur m,Timer T)
    {
        this.f = f;
        this.tg = tg;
        this.m = m;
        timer = T;
    }
    public void next()
    {
       
        if(m.chemin.isEmpty())
            timer.stop();
        else
        {
            m.MajPositionPousseur();
            m.score++;
            f.tracerSansDelai(tg);
        }       
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        next();
    }
}
