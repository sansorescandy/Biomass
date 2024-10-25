/*
  Copyright 2006 by Sean Luke and George Mason University
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/

package biomass.space.tests;

import sim.engine.*;
import sim.util.Double2D;
import sim.portrayal.*;
import java.awt.geom.*;

public abstract /*strictfp*/ class Agent extends SimplePortrayal2D implements Steppable
    {
    private static final long serialVersionUID = 1;

    public double length;
    public double biomass;
    public Double2D agentLocation; 
    public Vector2D velocity;
    

    public int intID = -1;

    public Agent(double length, double biomass, Double2D location )
        {
        this.length = length;
        this.biomass = biomass;
        this.agentLocation = location;
        velocity = new Vector2D(location);
        velocity=velocity.normalize();
        }

    double distanceSquared( final Double2D loc1, Double2D loc2 )
        {
        return( (loc1.x-loc2.x)*(loc1.x-loc2.x)+(loc1.y-loc2.y)*(loc1.y-loc2.y) );
        }
    
    double distance( final Double2D loc1, Double2D loc2 )
    {
    return( Math.sqrt((loc1.x-loc2.x)*(loc1.x-loc2.x)+(loc1.y-loc2.y)*(loc1.y-loc2.y)) );
    }


    }
