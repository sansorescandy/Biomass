/*
  Copyright 2006 by Sean Luke and George Mason University
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/

package biomass.space.tests;

import sim.field.continuous.*;

import java.awt.Toolkit;

import sim.engine.*;
import sim.util.*;


public /*strictfp*/ class NeighborSearchTest extends SimState
    {
    private static final long serialVersionUID = 1;

    public static final double XMIN = 0;
    public static final double XMAX = 576;
    public static final double YMIN = 0;
    public static final double YMAX = 360;
    public static final double TIMESTEP = 1;
    public static final double CHANGEDIR = 0.05;

    public static final double DIAMETER = 8;
    
    public static final double PREDATOR_PERCEPTION_FACTOR = 25;
    public static final double PREY_PERCEPTION_FACTOR = 25;
    public static final double HUNT_SPEED_FACTOR = 7;
    public static final double ESCAPE_SPEED_FACTOR = 5;
    public static final double WANDER_SPEED_FACTOR = 1;
    public static final double PREY_MAXSIZE_RATIO = 1;
    public static final double PREDATOR_MAXSIZE_RATIO = 2.0;
    
    
    
    public static final int NUM_PREDATORS = 1;
    public static final int NUM_PREYS = 500;
    
    

    public Continuous2D environment = null;
    public double viewportx = Toolkit.getDefaultToolkit().getScreenSize().width;
    public double viewporty = Toolkit.getDefaultToolkit().getScreenSize().height*0.85;

    /** Creates a simulation with the given random number seed. */
    public NeighborSearchTest(long seed)
        {
        super(seed);
        }


    public void start()
        {
        super.start();  // clear out the schedule

        //environment = new Continuous2D(25.0, viewportx, viewporty );
        environment = new Continuous2D(25.0, XMAX, YMAX );

        // Schedule the agents -- we could instead use a RandomSequence, which would be faster,
        // but this is a good test of the scheduler
        for(int x=0;x<NUM_PREDATORS;x++)
            {
            Double2D loc = null;
            Agent agent = null;
                loc = new Double2D( random.nextDouble()*(XMAX),
                    random.nextDouble()*(YMAX));
                    agent = new Predator(12, 343+x, loc );
               
            environment.setObjectLocation(agent,loc);
            schedule.scheduleRepeating(agent);
            }
        for(int x=0;x<NUM_PREYS;x++)
        {
        Double2D loc = null;
        Agent agent = null;
            loc = new Double2D( random.nextDouble()*(XMAX),
                random.nextDouble()*(YMAX));
            	double b=random.nextDouble()*225;
            	double l=(b/225)*5;
                agent = new Prey(10, 225+x/2, loc );
        Double2D scale = new Double2D (loc.x*viewportx/XMAX,loc.y*viewporty/YMAX);   
        environment.setObjectLocation(agent,loc);
        schedule.scheduleRepeating(agent);
        }
        }

    public static void main(String[] args)
        {
        doLoop(NeighborSearchTest.class, args);
        System.exit(0);
        }    
    }
