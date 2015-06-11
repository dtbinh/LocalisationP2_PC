
import java.util.Random;

import lejos.robotics.navigation.Move;
import lejos.robotics.navigation.Pose;
import lejos.geom.*;
import java.awt.Color;

/**
 * Represents a particle for the particle filtering algorithm. The state of the
 * particle is the pose, which represents a possible pose of the robot.
 * The weight represents the relative probability that the robot has this
 * pose. Weights are from 0 to 1.
 * 
 * This is a version of the leJOS class MCLParticle with a light sensor used to 
 * detect black or white tiles in a 2D map of black/white tiles. 
 * 
 * @author  Ole Caprani
 * @version 22.05.15
 * 
 * 
 */
public class Particle 
{
  private static Random rand = new Random();
  private Pose pose;
  private float weight = 1;
  private int blackWhiteThreshold = 500;

  /**
   * Create a particle with a specific pose
   * 
   * @param pose the pose
   */
  public Particle(Pose pose) {
    this.pose = pose;
  }

  /**
   * Set the weight for this particle
   * 
   * @param weight the weight of this particle
   */
  public void setWeight(float weight) {
    this.weight = weight;
  }

  /**
   * Return the weight of this particle
   * 
   * @return the weight
   */
  public float getWeight() 
  {
    return weight;
  }

  /**
   * Return the pose of this particle
   * 
   * @return the pose
   */
  
  public Pose getPose() 
  {
    return pose;
  }
  
  /**
   * Calculate the weight for this particle by comparing the reading in this
   * position as seen on the map with the
   * robot's light sensor reading.
   * 
   */
  public void calculateWeight(int lightValue, Map m) 
  {
	  if ( m.getColor(pose) == Color.BLACK )
	  {
		  if ( lightValue > blackWhiteThreshold) 
			  weight = 0.9f;
		  else
			  weight = 0.1f;
	  }
	  else 
	  {
		  if ( m.getColor(pose) == Color.WHITE )
		  {
			  if ( lightValue > blackWhiteThreshold) 
				  weight = 0.1f;
			  else
				  weight = 0.9f;
		  }
		  else // outside the map
			  weight = 0.0f;
		  
	  }	  
  }

  /**
   * Apply the robot's move to the particle with a bit of random noise.
   * Only works for rotate or travel movements.
   * 
   * @param move the robot's move
   */
  public void applyMove(Move move, float distanceNoiseFactor, float angleNoiseFactor) 
  {
    
    float ym = (move.getDistanceTraveled() * ((float) Math.sin(Math.toRadians(pose.getHeading()))));
    float xm = (move.getDistanceTraveled() * ((float) Math.cos(Math.toRadians(pose.getHeading()))));


    pose.setLocation(new Point(
    		         (float) (pose.getX() + xm + (distanceNoiseFactor * xm * rand.nextGaussian())),
                     (float) (pose.getY() + ym + (distanceNoiseFactor * ym * rand.nextGaussian()))));
    pose.setHeading(
       (float) (pose.getHeading() + move.getAngleTurned() + (angleNoiseFactor  * rand.nextGaussian())));
    pose.setHeading((float) ((int) (pose.getHeading() + 0.5f) % 360));

  }
}
