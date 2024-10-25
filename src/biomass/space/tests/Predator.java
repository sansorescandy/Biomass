/*
  Copyright 2006 by Sean Luke and George Mason University
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/

package biomass.space.tests;

import sim.util.*;
import sim.engine.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;

import sim.portrayal.*;

public /*strictfp*/ class Predator extends Agent
    {
    private static final long serialVersionUID = 1;

    protected boolean greedy = false;
    public final boolean getIsGreedy() { return greedy; }
    public final void setIsGreedy( final boolean b ) { greedy = b; }

    public Predator(double length, double biomass, Double2D location ) 
        {
        super(length, biomass, location );
        }

    Double2D desiredLocation = null;
    Double2D suggestedLocation = null;
    int steps = 0;

    public void step( final SimState state )
    {
        NeighborSearchTest nst = (NeighborSearchTest)state;
        double relative_distance;
        double relative_weight;
        double min_encounter_time=Double.MAX_VALUE;
		double encounter_time;
		Prey mostAccesiblePrey = null;

		Double2D scale = new Double2D (agentLocation.x*(nst.viewportx/NeighborSearchTest.XMAX),agentLocation.y*(nst.viewporty/NeighborSearchTest.YMAX));  
        Bag agents = nst.environment.getNeighborsExactlyWithinDistance(agentLocation, NeighborSearchTest.PREDATOR_PERCEPTION_FACTOR*length, false);
        //System.out.println("Posibles Presas:" +nst.environment.allObjects.numObjs);
        //System.out.println("MostAccesiblePrey: "+mostAccesiblePrey);
        if( agents != null )
        {
            for( int i = 0 ; i < agents.numObjs ; i++ )
            {
                if( agents.objs[i] != null &&
                    agents.objs[i] != this ) //This verification is necessary because the object that is searching its neighbors is included
                    {                       //in the returned list not necessary in our model
                    // if agent is not prey
                    if( ! (((Agent)agents.objs[i]) instanceof Prey ))
                        continue;
                    Prey prey = (Prey)(agents.objs[i]);
                    // if agent is too big is not a prey
                    if( NeighborSearchTest.PREY_MAXSIZE_RATIO<prey.length/length )
                        continue;
                    //Hay una presa de tamaño adecuado
                    relative_distance=distance( agentLocation, prey.agentLocation )/length;
                    relative_weight=prey.biomass/biomass;
                    encounter_time=calculateHuntEncounterTime(relative_distance, relative_weight);
                    if(encounter_time<min_encounter_time){
        				min_encounter_time=encounter_time;
        				mostAccesiblePrey=prey;
        			}  
                 }
            }
            if( mostAccesiblePrey != null ) {
            	//System.out.println("Presa más accesible: "+ mostAccesiblePrey.getType());
            	//Dirección de la presa
            	velocity=new Vector2D(mostAccesiblePrey.agentLocation.x-agentLocation.x, mostAccesiblePrey.agentLocation.y-agentLocation.y);
            	//La velocidad de cacería en un paso del tiempo
            	double huntDistanceTimeStep=NeighborSearchTest.HUNT_SPEED_FACTOR*length*NeighborSearchTest.TIMESTEP;
            	if(velocity.magnitude()>huntDistanceTimeStep) {
            		velocity=velocity.setMagnitude(huntDistanceTimeStep);
            		double x=agentLocation.x + velocity.x;
            		double y=agentLocation.y + velocity.y;
            		if(x<NeighborSearchTest.XMIN)  {
                		x=NeighborSearchTest.XMIN;
                		velocity.x=-velocity.x;
            		}
                	else
                		if(x>NeighborSearchTest.XMAX) {
                		    x=NeighborSearchTest.XMAX;
                		    velocity.x=-velocity.x;
                		}
            		if(y<NeighborSearchTest.YMIN)  {
                		y=NeighborSearchTest.YMIN;
                		velocity.y=-velocity.y;
            		}
                	else
                		if(y>NeighborSearchTest.YMAX) {
                		    y=NeighborSearchTest.YMAX;
                		    velocity.y=-velocity.y;
                		}
            		agentLocation = new Double2D(x, y);
            		
            		scale = new Double2D (agentLocation.x*(nst.viewportx/NeighborSearchTest.XMAX),agentLocation.y*(nst.viewporty/NeighborSearchTest.YMAX));   
                    
                    nst.environment.setObjectLocation(this,agentLocation);
                    //System.out.println("No está cerca");
            	}
            	else {
            		double x=agentLocation.x + velocity.x;
            		double y=agentLocation.y + velocity.y;
            		if(x<NeighborSearchTest.XMIN)  {
                		x=NeighborSearchTest.XMIN;
                		velocity.x=-velocity.x;
            		}
                	else
                		if(x>NeighborSearchTest.XMAX) {
                		    x=NeighborSearchTest.XMAX;
                		    velocity.x=-velocity.x;
                		}
            		if(y<NeighborSearchTest.YMIN)  {
                		y=NeighborSearchTest.YMIN;
                		velocity.y=-velocity.y;
            		}
                	else
                		if(y>NeighborSearchTest.YMAX) {
                		    y=NeighborSearchTest.YMAX;
                		    velocity.y=-velocity.y;
                		}
            		//System.out.println("Numero de elementos en el environment :"+nst.environment.allObjects.numObjs);
            		//System.out.println("Se come a :"+mostAccesiblePrey.getType());
            		agentLocation = new Double2D(agentLocation.x + velocity.x, agentLocation.y + velocity.y);
            		scale = new Double2D (agentLocation.x*(nst.viewportx/NeighborSearchTest.XMAX),agentLocation.y*(nst.viewporty/NeighborSearchTest.YMAX));  
                    nst.environment.setObjectLocation(this,agentLocation);
            		nst.environment.remove(mostAccesiblePrey); //se come a la presa
            		mostAccesiblePrey.setDead(true);
            		//System.out.println("Numero de elementos en el environment :"+nst.environment.allObjects.numObjs);
            		
            	}
            		
            }
            else { // Si encontró agentes pero no encontró presas en su rango de percepción: Wander around
            	if (nst.random.nextDouble()<NeighborSearchTest.CHANGEDIR) {
            		velocity=velocity.normalize();
            		Vector2D v=randomDirection(nst);
            		v=v.normalize();
            		velocity.x+=v.x;
            		velocity.y+=v.y;
            		//System.out.println("Cambio Dir");
            	}
            		
            		velocity=velocity.setMagnitude(NeighborSearchTest.WANDER_SPEED_FACTOR*length*NeighborSearchTest.TIMESTEP);
            		double x=agentLocation.x + velocity.x;
            		double y=agentLocation.y + velocity.y;
            		if(x<NeighborSearchTest.XMIN)  {
                		x=NeighborSearchTest.XMIN;
                		velocity.x=-velocity.x;
            		}
                	else
                		if(x>NeighborSearchTest.XMAX) {
                		    x=NeighborSearchTest.XMAX;
                		    velocity.x=-velocity.x;
                		}
            		if(y<NeighborSearchTest.YMIN)  {
                		y=NeighborSearchTest.YMIN;
                		velocity.y=-velocity.y;
            		}
                	else
                		if(y>NeighborSearchTest.YMAX) {
                		    y=NeighborSearchTest.YMAX;
                		    velocity.y=-velocity.y;
                		}
            		agentLocation = new Double2D(x, y);
            		scale = new Double2D (agentLocation.x*(nst.viewportx/NeighborSearchTest.XMAX),agentLocation.y*(nst.viewporty/NeighborSearchTest.YMAX));
                    nst.environment.setObjectLocation(this,agentLocation);
            }
        }
        
    }
    
    double calculateHuntEncounterTime(double rel_dist, double rel_weight)
	{
		return(rel_dist/(NeighborSearchTest.HUNT_SPEED_FACTOR*(1-rel_weight)));
	}
    
    // returns a random directions
    public Vector2D randomDirection( final SimState state )
        {
        Vector2D temp = new Vector2D( 1.0 - 2.0 * state.random.nextDouble(),
            1.0 - 2.0 * state.random.nextDouble() );
        return temp;
        }


    protected Color predatorColor = new Color(255,0,0);
    public void draw(Object object, Graphics2D graphics, DrawInfo2D info)
        {
        double diamx = info.draw.width*length*0.5;
        double diamy = info.draw.height*length*0.5;

        graphics.setColor( predatorColor );
        //graphics.setPaint( predatorColor );
        graphics.fillOval((int)(info.draw.x-diamx/2),(int)(info.draw.y-diamy/2),(int)(diamx),(int)(diamy));
        //graphics.drawOval((int)(info.draw.x-NeighborSearchTest.PREDATOR_PERCEPTION_FACTOR*info.draw.width*length/2), (int)(info.draw.y-NeighborSearchTest.PREDATOR_PERCEPTION_FACTOR*info.draw.height*length/2), (int)(NeighborSearchTest.PREDATOR_PERCEPTION_FACTOR*info.draw.width*length), (int)(NeighborSearchTest.PREDATOR_PERCEPTION_FACTOR*info.draw.height*length));
        //graphics.fillOval((int)(agentLocation.x-length*2/2),(int)(agentLocation.y-length*2/2),(int)length*2,(int)length*2);
        //graphics.fillOval((int)(agentLocation.x-length*NeighborSearchTest.PREDATOR_PERCEPTION_FACTOR/2),(int)(agentLocation.y-length*NeighborSearchTest.PREDATOR_PERCEPTION_FACTOR/2),(int)(length*NeighborSearchTest.PREDATOR_PERCEPTION_FACTOR),(int)(length*NeighborSearchTest.PREDATOR_PERCEPTION_FACTOR));
       
        //graphics.fill(fish_shape);
        //graphics.fill(fish_perc);
        }


    }
