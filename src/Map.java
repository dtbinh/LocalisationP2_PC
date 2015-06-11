import java.awt.Color;
import lejos.robotics.navigation.Pose;

public class Map 
{
	// The map of the robot environment
	int dimX, dimY, width, rangeX, rangeY;
	private Color[][]  map;
    
    public Map(int dimX, int dimY, int width)
    {
    	this.dimX = dimX;
    	this.dimY = dimY;
    	this.width = width;
    	rangeX = dimX*width;
    	rangeY = dimY*width;
    	map = new Color[dimX][dimY];
    	for (int i = 0; i < dimX; i++)
    	for (int j = 0; j < dimY; j++) map[i][j] = Color.WHITE;
    }
    
    
    public void setColor(int i, int j, Color c)
    {
    	map[i][j] = c;
    }
    
    public Color getColor(int i, int j)
    {
    	return (map[i][j]);
    }
    
    public Color getColor(Pose p)
    {
    	float x = p.getX(), y = p.getY();
    	if ( x < 0 || x > rangeX || y < 0 || y > rangeY )
    		return Color.ORANGE;
    	else
    		return map[(int)(x/width)][(int)(y/width)];
    }
    
    public int getWidth()
    {
    	return (width);
    }
    
    public int getDimX()
    {
    	return (dimX);
    }
    
    public int getDimY()
    {
    	return (dimY);
    }
}
