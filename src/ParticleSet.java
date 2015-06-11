
import lejos.robotics.navigation.Move;
import lejos.robotics.navigation.Pose;
import java.awt.Rectangle;

/**
 * Represents a particle set for the particle filtering algorithm.
 * 
 * This is a version of the leJOS class MCLParticles with the particles
 * generated within a 2D map of black/white tiles. 
 * 
 * @author  Ole Caprani
 * @version 22.05.15
 *
 */
public class ParticleSet
{
  // Constants
  private static final float BIG_FLOAT = 10000f;
  
  // Static variables
  public static int maxIterations = 100;

  // Instance variables
  private float distanceNoiseFactor = 0.02f;
  private float angleNoiseFactor = 1f;
  private int numParticles;
  private Particle[] particles;
  private Map map;
  private int _iterations;   
  private float _x, _y, _heading;
  private float minX, maxX, minY, maxY;
  private double varX, varY, varH;
  
  
  /**
   * Returns the best best estimate of the current pose;
   * @return the estimated pose
   */
  public Pose getPose()
  {
    estimatePose();
    return new Pose(_x, _y, _heading);
  }
  
  /**
   * Create a set of particles.
   *
   * @param map the map of the enclosed environment
   */
  public ParticleSet(int numParticles, Map m)
  {
    this.numParticles = numParticles;
    map = m;
    particles = new Particle[numParticles];    
    for (int i = 0; i < numParticles; i++) 
    {
     particles[i] =  generateParticle(map);     
    }
  }
  
  private Particle generateParticle(Map m)
  {
	int sizeX = m.getDimX()*m.getWidth();
	int sizeY = m.getDimY()*m.getWidth();
	
	// Generate a particle with a location (x,y) randomly chosen within the
	// 2D area of the map. The heading can be chosen as suggested in several
	// different ways.
	Particle p = new Particle(new Pose(
   		 (float)(Math.random()*sizeX), (float)(Math.random()*sizeY), 
   		 //(float)(Math.random()*360)));
   		 (float)((int)(Math.random()*2)*180)));
   		 //0));
     p.setWeight(1);
     
     return p;
  }

  /**
   * Return the number of particles in the set
   *
   * @return the number of particles
   */
  public int numParticles() 
  {
    return numParticles;
  }

  /**
   * Get a specific particle
   *
   * @param i the index of the particle
   * @return the particle
   */
  public Particle getParticle(int i) 
  {
    return particles[i];
  }


  /**
   * Resample the set picking those with higher weights.
   *
   * Note that the new set has multiple instances of the particles with higher
   * weights.
   *
   * @return true iff lost
   */
  
  public boolean resample() 
  {
	    // Rename particles as oldParticles and create a new set
	    Particle[] oldParticles = particles;
	    particles = new Particle[numParticles];

	    // Continually pick a random number and select the particles with
	    // weights greater than or equal to it until we have a full
	    // set of particles.
	    int count = 0;
	    int iterations = 0;

	    while (count < numParticles) 
	    {
	      iterations++;
	      if (iterations >= maxIterations) 
	      {
	        System.out.println("Lost: count = " + count);
	        if (count > 0) { // Duplicate the ones we have so far
	          for (int i = count; i < numParticles; i++) 
	          {
	            particles[i] = new Particle(particles[i % count].getPose());
	            particles[i].setWeight(particles[i % count].getWeight());
	          }
	          return false;
	        } else 
	          { // Completely lost - generate a new set of particles
	          for (int i = 0; i < numParticles; i++) 
	          {
	            particles[i] = generateParticle(map);
	          }
	          return true;
	        }
	      }
	      float rand = (float) Math.random();
	      for (int i = 0; i < numParticles && count < numParticles; i++) {
	        if (oldParticles[i].getWeight() >= rand) {
	          Pose p = oldParticles[i].getPose();
	          float x = p.getX();
	          float y = p.getY();
	          float angle = p.getHeading();

	          // Create a new instance of the particle and set its weight
	          particles[count] = new Particle(new Pose(x, y, angle));
	          particles[count++].setWeight(1);
	        }
	      }
	    }
	    _iterations = iterations;
	    return false;
	  }


  /**
   * Calculate the weight for each particle
   * 
   */
  public void  calculateWeights(int lightValue, Map map)
  {   
    for (int i = 0; i < numParticles; i++)
    {
      particles[i].calculateWeight(lightValue, map);
    }
  }

  /**
   * Apply a move to each particle
   *
   * @param move the move to apply
   */
  public void applyMove(Move move) 
  {
    for (int i = 0; i < numParticles; i++) 
    {
      particles[i].applyMove(move, distanceNoiseFactor, angleNoiseFactor);
    }
  }
  
  /**
   * The highest weight of any particle
   *
   * @return the highest weight
   */
  public float getMaxWeight() 
  {
    float wt = 0;
    for (int i = 0; i < particles.length; i ++ ) 
    	wt = Math.max(wt,particles[i].getWeight());
    return wt;
  }

  /**
   * Set the distance noise factor
   * @param factor the distance noise factor
   */
  public void setDistanceNoiseFactor(float factor) 
  {
    distanceNoiseFactor = factor;
  }

  /**
   * Set the distance angle factor
   * @param factor the distance angle factor
   */
  public void setAngleNoiseFactor(float factor) 
  {
    angleNoiseFactor = factor;
  }

  /**
   * Set the maximum iterations for the resample algorithm
   * @param max the maximum iterations
   */
  public void setMaxIterations(int max) 
  {
    maxIterations = max;
  }
  
  public int getIterations() 
  {
      return _iterations;
  } 
  
  /**
   * Estimate pose from weighted average of the particles
   * Calculate statistics
   */
  public void estimatePose()
  {
    float totalWeights = 0;
    float estimatedX = 0;
    float estimatedY = 0;
    float estimatedAngle = 0;
    varX = 0;
    varY = 0;
    varH = 0;
    minX = BIG_FLOAT;
    minY = BIG_FLOAT;
    maxX = -BIG_FLOAT;
    maxY = -BIG_FLOAT;

    for (int i = 0; i < numParticles; i++)
    {
      Pose p = particles[i].getPose();
      float x = p.getX();
      float y = p.getY();
      //float weight = particles.getParticle(i).getWeight();
      float weight = 1; // weight is historic at this point, as resample has been done
      estimatedX += (x * weight);
      varX += (x * x * weight);
      estimatedY += (y * weight);
      varY += (y * y * weight);
      float head = p.getHeading();
      estimatedAngle += (head * weight);
      varH += (head * head * weight);
      totalWeights += weight;

      if (x < minX)  minX = x;

      if (x > maxX)maxX = x;
      if (y < minY)minY = y;
      if (y > maxY)   maxY = y;
    }

    estimatedX /= totalWeights;
    varX /= totalWeights;
    varX -= (estimatedX * estimatedX);
    estimatedY /= totalWeights;
    varY /= totalWeights;
    varY -= (estimatedY * estimatedY);
    estimatedAngle /= totalWeights;
    varH /= totalWeights;
    varH -= (estimatedAngle * estimatedAngle);
    
    // Normalize angle
    while (estimatedAngle > 180) estimatedAngle -= 360;
    while (estimatedAngle < -180) estimatedAngle += 360;
    
    _x = estimatedX;
    _y = estimatedY;
    _heading = estimatedAngle;
  }
  /**
   * Returns the minimum rectangle enclosing all the particles
   * @return rectangle : the minimum rectangle enclosing all the particles
   */
  public Rectangle getErrorRect()
  {
    return new Rectangle((int) minX, (int) minY,
            (int) (maxX - minX), (int) (maxY - minY));
  }

  /**
   * Returns the maximum value of  X in the particle set
   * @return   max X
   */
  public float getMaxX()
  {
    return maxX;
  }

  /**
   * Returns the minimum value of   X in the particle set;
   * @return minimum X
   */
  public float getMinX()
  {
    return minX;
  }

  /**
   * Returns the maximum value of Y in the particle set;
   * @return max y
   */
  public float getMaxY()
  {
    return maxY;
  }

  /**
   * Returns the minimum value of Y in the particle set;
   * @return minimum Y
   */
  public float getMinY()
  {
    return minY;
  }
  
  /**
   * Returns the standard deviation of the X values in the particle set;
   * @return sigma X
   */
  public float getSigmaX()
  {
    return (float) Math.sqrt(varX);
  }

  /**
   * Returns the standard deviation of the Y values in the particle set;
   * @return sigma Y
   */
  public float getSigmaY()
  {
    return (float) Math.sqrt(varY);
  }
  
  /**
   * Returns the standard deviation of the heading values in the particle set;
   * @return sigma heading
   */
  public float getSigmaHeading()
  {
    return (float) Math.sqrt(varH);
  }
  
}
