/*
  Copyright 2006 by Sean Luke and George Mason University
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
 */

package biomass.space.tests;

import sim.util.*;
import sim.engine.*;
import java.awt.*;
import sim.portrayal.*;

public /*strictfp*/ class Prey extends Agent
{
	private static final long serialVersionUID = 1;

	protected boolean dead = false;
	public final boolean isDead() { return dead; }
	public final void setDead( boolean b ) { dead = b; }

	public Prey(double length, double biomass, Double2D location ) 
	{
		super(length, biomass, location );

	}



	public void step( final SimState state )
	{
		//Predator predator;
		Vector2D escape_vector= null;
		double x;
		double y;

		if(!dead) {
			NeighborSearchTest nst = (NeighborSearchTest)state;
			Double2D scale = new Double2D (agentLocation.x*(nst.viewportx/NeighborSearchTest.XMAX),agentLocation.y*(nst.viewporty/NeighborSearchTest.YMAX));
			Bag agents = nst.environment.getNeighborsExactlyWithinDistance(agentLocation, NeighborSearchTest.PREY_PERCEPTION_FACTOR*length, false);

			if( agents.numObjs > 1 ) //Tiene al menos 1 vecino
			{
				//System.out.println("Tiene vecinos");
				for( int i = 0 ; i < agents.numObjs ; i++ )
				{
					if( agents.objs[i] != null &&
							agents.objs[i] != this ) //This verification is necessary because the object that is searching its neighbors is included
					{                       //in the returned list
						// if agent is not predator
						if( ! (((Agent)agents.objs[i]) instanceof Predator ))
							continue;
						Predator predator = (Predator)(agents.objs[i]);
						
						if(escape_vector==null)
							escape_vector=new Vector2D(0,0);
						double abs_distance=distance( agentLocation, predator.agentLocation ); //Hay un depredador sufucientemente grande
						double encounter_time=calculateEncounterTime(predator, abs_distance);
						//System.out.println("Encounter time = "+encounter_time);
						Vector2D v=new Vector2D(agentLocation.x-predator.agentLocation.x, agentLocation.y-predator.agentLocation.y); // Se obtiene el vector que apunta en dirección al depredador
						v=v.normalize();
						//System.out.println("V antes de amplify v.x = "+v.x+" v.y = "+v.y );
						v=v.amplify(1/encounter_time);
						//System.out.println("Después de amplify v.x = "+v.x+" v.y = "+v.y );
						escape_vector=escape_vector.add(v);
					}  
				}
				if (escape_vector!=null) {
					//System.out.println("Antes velocity.x = "+velocity.x+" velocity.y = "+velocity.y );
					//System.out.println("Escape escape.x = "+escape_vector.x+" escape.y = "+escape_vector.y );
					velocity=escape_vector;
					velocity=velocity.setMagnitude(NeighborSearchTest.ESCAPE_SPEED_FACTOR*length*NeighborSearchTest.TIMESTEP);
					//System.out.println("Después velocity.x = "+velocity.x+" velocity.y = "+velocity.y );
				}
				else { //no tuvo miedo
					if (nst.random.nextDouble()<NeighborSearchTest.CHANGEDIR) {
						velocity=velocity.normalize();
						Vector2D v=randomDirection(nst);
						v=v.normalize();
						velocity.x+=v.x;
						velocity.y+=v.y;
					}
					velocity=velocity.setMagnitude(NeighborSearchTest.WANDER_SPEED_FACTOR*length*NeighborSearchTest.TIMESTEP);
				}
			}
			else { //No tiene vecinos
				//System.out.println("No tiene vecinos");
				if (nst.random.nextDouble()<NeighborSearchTest.CHANGEDIR) {
					velocity=velocity.normalize();
					Vector2D v=randomDirection(nst);
					v=v.normalize();
					velocity.x+=v.x;
					velocity.y+=v.y;
				}
				velocity=velocity.setMagnitude(NeighborSearchTest.WANDER_SPEED_FACTOR*length*NeighborSearchTest.TIMESTEP);
				
			}

			x=agentLocation.x + velocity.x;
			y=agentLocation.y + velocity.y;
			
			//System.out.println("x = "+x+" y = "+y );
			
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
			//System.out.println("x = "+x+" y = "+y );
			agentLocation = new Double2D(x, y);
			scale = new Double2D (agentLocation.x*(nst.viewportx/NeighborSearchTest.XMAX),agentLocation.y*(nst.viewporty/NeighborSearchTest.YMAX));
			nst.environment.setObjectLocation(this,agentLocation);
		}
			
	}

	double calculateEncounterTime(Predator predator, double abs_distance)
	{   
		return ( abs_distance / NeighborSearchTest.ESCAPE_SPEED_FACTOR*predator.length);
	}

	// returns a random directions
	public Vector2D randomDirection( final SimState state )
	{
		Vector2D temp = new Vector2D( 1.0 - 2.0 * state.random.nextDouble(),
				1.0 - 2.0 * state.random.nextDouble() );
		return temp;
	}




	protected Color preyColor = new Color(0,0,0);

	public final void draw(Object object, Graphics2D graphics, DrawInfo2D info)
	{
		double diamx = info.draw.width*length*0.5;
		double diamy = info.draw.height*length*0.5;

		graphics.setColor ( preyColor ); 
		graphics.fillOval((int)(info.draw.x-diamx/2),(int)(info.draw.y-diamy/2),(int)(diamx),(int)(diamy));
	}

}
