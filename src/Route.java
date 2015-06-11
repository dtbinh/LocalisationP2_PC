import java.util.ArrayList;
import lejos.geom.Point;
import lejos.robotics.navigation.Move;
import lejos.robotics.navigation.Pose;

public class Route 
{
	// The route driven as seen from the robot

    private ArrayList<Point> route = new ArrayList<Point>();
    private Pose currentPose = new Pose();
    private float x, y;
    
    public Route(float x, float y, float heading)
    {
        currentPose.setLocation(x, y); currentPose.setHeading(heading);
        route.add(new Point(x,y));
    }
    
    private float normalize(float angle)
    {
    	float a = angle;
    	if ( a >= 0 )
    	{
    		while ( a > 360 )
    			a = a - 360;
    	}
    	else
    	{
    		while ( a < -360 )
    			a = a + 360;
    	}	
    	return a;
    }
    
    public void update(Move move)
    {
    	if ( move.getMoveType() == Move.MoveType.TRAVEL)
    	{
    		float d = move.getDistanceTraveled();
    		double aRad = Math.toRadians(currentPose.getHeading());
    		x = currentPose.getX() + d * ((float) Math.cos(aRad));
    		y = currentPose.getY() + d * ((float) Math.sin(aRad));
    		currentPose.setLocation(x, y);
    		route.add(new Point(x,y));
    	}
    	else
    	{
    		currentPose.setHeading( 
        			normalize(currentPose.getHeading() + move.getAngleTurned()));
    	}
    }
    
    public ArrayList<Point> getRoute()
    {
    	return route;
    }
    
    public Pose getCurrentPose()
    {
    	return currentPose;
    }
}
