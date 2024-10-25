/*
  Copyright 2006 by Sean Luke and George Mason University
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/

package biomass.space.tests;

import sim.portrayal.continuous.*;
import sim.engine.*;
import sim.display.*;
import javax.swing.*;
import java.awt.Color;


public class NeigborSearchTestUI extends GUIState
    {

    public Display2D display;
    public JFrame displayFrame;

    ContinuousPortrayal2D vidPortrayal = new ContinuousPortrayal2D();

    public static void main(String[] args)
        {
        new NeigborSearchTestUI().createController();
        }

    public NeigborSearchTestUI() { super(new NeighborSearchTest( System.currentTimeMillis())); }
    public NeigborSearchTestUI(SimState state) { super(state); }

    public static String getName() { return "Pedrator-Prey Model"; }
        
    public void start()
        {
        super.start();
        setupPortrayals();
        }

    public void load(SimState state)
        {
        super.load(state);
        setupPortrayals();
        }
        
    public void setupPortrayals()
        {
        // tell the portrayals what to portray and how to portray them
        vidPortrayal.setField(((NeighborSearchTest)state).environment);
            
        // reschedule the displayer
        display.reset();
        display.setBackdrop(Color.white);
                
        // redraw the display
        display.repaint();
        }

    public void init(Controller c)
        {
        super.init(c);

        // make the displayer
        display = new Display2D(1000,1000,this);
        displayFrame = display.createFrame();
        displayFrame.setTitle("Predator-Prey Display");
        c.registerFrame(displayFrame);   // register the frame so it appears in the "Display" list
        displayFrame.setVisible(true);
        display.attach( vidPortrayal, "Agents" );
        }
        
    public void quit()
        {
        super.quit();
        
        if (displayFrame!=null) displayFrame.dispose();
        displayFrame = null;
        display = null;
        }

    }
