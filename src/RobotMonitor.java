/**
 * The RobotMonitor simulates a sequence of moves and sensor readings
 * and use these to update a set of particles in a 2D black/white tile world.
 * 
 * @author Ole Caprani 
 * @version 23-5-2015
 */

import java.awt.Color;

import lejos.robotics.navigation.Move;
import lejos.robotics.navigation.Pose;
import lejos.util.Delay;

public class RobotMonitor 
{
	private InputOutputStreams NXT;
	private boolean USB = false;
	
	
    private Map m = new Map(12, 1, 50);     
    private float lightVal;
    private ParticleSet particles  = new ParticleSet(1000, m);
    private Route route = new Route(0,25,0);
    private RobotGUI view = new RobotGUI(particles, m);
    private Move move;
    private int pause = 1000; // ms between views

    public RobotMonitor()
    {
        m.setColor(3, 0,  Color.BLACK);
        m.setColor(5, 0,  Color.BLACK);
        m.setColor(8, 0,  Color.BLACK);
   
    	view.update(route.getRoute());
    	Delay.msDelay(pause);
    }
    
    public void printParticles()
    {
    	for ( int i=0; i < particles.numParticles(); i++)
    	{
    		Pose p=particles.getParticle(i).getPose();
    		float w = particles.getParticle(i).getWeight();
    		System.out.println("P " + i + " "+p.getX()+" "+p.getY()+" "+p.getHeading()+" "+w);
    	}
    	System.out.println();
    }

    public void motionUpdate(Move move)
    {
    	particles.applyMove(move);
    	route.update(move);
    	view.update(route.getRoute()); 
    	Delay.msDelay(pause);
    }
    
    public void sensorUpdate(int lightValue)
    {
    	particles.calculateWeights(lightValue, m);
    	view.update(route.getRoute()); 
    	Delay.msDelay(pause);
    	particles.resample();
    	view.update(route.getRoute());
    	Delay.msDelay(pause);
    }
    
    public void goSimulation()
    {
    	
    	for ( int i=0; i < 16; i++)
    	{
    		move = new Move(Move.MoveType.TRAVEL, 9, 0, false);
    		motionUpdate(move);
    		sensorUpdate(400);
    	}
    	
    	for ( int i=0; i < 5; i++)
    	{
    		move = new Move(Move.MoveType.TRAVEL, 10, 0, false);
    		motionUpdate(move);
    		sensorUpdate(600);
    	}

		move = new Move(Move.MoveType.TRAVEL, 100, 0, false);
    	motionUpdate(move);
    	sensorUpdate(600);
    	
		move = new Move(Move.MoveType.TRAVEL, 10, 0, false);
    	motionUpdate(move);
    	sensorUpdate(400);
    	
		move = new Move(Move.MoveType.TRAVEL, 10, 0, false);
    	motionUpdate(move);
    	sensorUpdate(400);
    	    	
    	Pose p = particles.getPose();
		System.out.println("Position "+p.getX()+" "+p.getY()+" "+p.getHeading());
		System.out.println("Accuracy "+ particles.getSigmaX()+ " "+ particles.getSigmaY()+" "+particles.getSigmaHeading());
    	
    }
    
    
    private Move getMove()
    {
    	Move m;
    	int type = (int)NXT.input();
    	float distance = NXT.input();
    	float angle = NXT.input();
    	lightVal = NXT.input();
    	m = new Move( (type == 0)? Move.MoveType.TRAVEL : Move.MoveType.ROTATE , 
    			distance, angle, false);
    	System.out.println("Move " + distance + " " + angle);
    	return m;
    }
    
    public void go(){
    	
    	while (true){
    		
    		move = getMove();
        	particles.applyMove(move);        	
        	particles.calculateWeights((int)lightVal, m);
        	route.update(move);
        	view.update(route.getRoute());
        	Pose p = route.getCurrentPose();
            System.out.println("Pose " + p.getX() + " " + p.getY() + " " + p.getHeading());
    		
    		
    		
    	}
    }
    
    public static void main (String [] args)
    {
    	RobotMonitor p = new RobotMonitor();
    	
    	//p.goSimulation();
    	p.go();

     }
}