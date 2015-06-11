
/**
 * The PilotGUI Class
 * 
 * This class can be used to visualize the actions of a particle filter 
 * Localization algorithm while it is being used in a solution of the
 * LMICSE MCL project.
 * 
 * @author Ole Caprani
 * @version 14/5/15
 */

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.JFrame;
import lejos.geom.Point;
import lejos.robotics.navigation.Pose;

public class RobotGUI extends JFrame {

    // program will automatically adjust if these three constants are changed
    static int VIS_HEIGHT;            // visualization height  
    static int VIS_WIDTH;             // visualization width
    static final int Y_OFFSET=30;     // vertical offset to visualization
    static final int X_OFFSET=20;     // horizontal offset to visualization
    static final int POINT_WIDTH=2;   // width of points shown in the visualization
                                      // VIS_HEIGHT + 2 * Y_OFFSET is the window height
                                      // VIS_WIDTH + 2 * X_OFFSET is the window width
    
    
    ParticleSet particles;            // the set of sample locations
    ArrayList<Point> route;           // the route as seen from the robot
    Map m;
    
    // an inner class to handle the visualization window being closed manually
    class MyWindowAdapter extends WindowAdapter {   
        public void windowClosing(WindowEvent w) {
            System.exit(0);
        }
    }
    
    // the constructor for the Visualize class    
    public RobotGUI(ParticleSet particles, Map m) 
    { 
    	 this.particles = particles;
    	 this.m = m;
    	 VIS_WIDTH  = m.getDimX()*m.getWidth();
    	 VIS_HEIGHT = m.getDimY()*m.getWidth();
         this.setSize(new Dimension(VIS_WIDTH+X_OFFSET*2, VIS_HEIGHT+Y_OFFSET*2));
         this.setTitle("Monte Carlo Localization");
         this.setVisible(true);
         this.setBackground(Color.WHITE);
         addWindowListener(new MyWindowAdapter());
    }
    
    // update the visualization display based on the estimated robot location and
    // and the locations of the samples
    public void update(ArrayList<Point> route) 
    {
    	this.route = route;
        repaint();
    }
        
    // paint the visualization window - called by repaint in update method, and also by the
    // run-time system whenever it decides the window need to be updated (e.g., when uncovered)
    public void paint(Graphics g) 
    {                    
        Graphics2D g2 = ( Graphics2D ) g; 
        
        super.paint(g);
        displayMap(g2);
        displayParticles(g2);
        displayRoute(g2);
    }
    
    private void displayMap( Graphics2D g2)
    {
        int x1, y1 , x2, y2;
        
        g2.setColor(Color.RED);
        g2.setStroke( new BasicStroke( 1.0f ));
        for (int i = 0; i < m.getDimX()+1; i++) 
        {
        	x1 = winX(i*m.getWidth()); y1 = winY(0);
        	x2 = x1; y2 = winY(m.getDimY()*m.getWidth());
        	g2.drawLine(x1, y1, x2, y2);
        	x1 = winX(0); y1 = winY(i*m.getWidth());
        	x2 = winX(m.getDimX()*m.getWidth()); y2 = y1;
        	g2.drawLine(x1, y1, x2, y2);      	
        }
        
        g2.setColor(new Color( 0, 0, 0, 20 )); // transparent black
        for (int i = 0; i < m.getDimX(); i++) 
        {
        	 for (int j = 0; j < m.getDimY(); j++) 
             {
             	if (m.getColor(i, j) == Color.BLACK )
             		g2.fillRect( winX(i*m.getWidth()), winY((j+1)*m.getWidth()), m.getWidth(), m.getWidth());
             }
        }
    }
    
    private void displayRoute( Graphics2D g2)
    {
        int x1, y1 , x2, y2;
        
        g2.setColor(Color.BLUE);
        g2.setStroke( new BasicStroke( 5.0f ));
        x1 = winX((int)route.get(0).getX()); y1 = winY((int)route.get(0).getY());

        for (int i = 1; i < route.size(); i++) 
        { 
        	Point current = route.get(i);
            x2 = winX((int)current.getX());
        	y2 = winY((int)current.getY());
        	g2.drawLine(x1, y1, x2, y2);
        	x1 = x2; y1 = y2;
        }
    }
    
    private void displayParticles( Graphics2D g2)
    {
        Pose p; Float w;
        
        g2.setStroke( new BasicStroke( 1.0f ));
        for (int i = 0; i < particles.numParticles(); i++) 
        {
            p = particles.getParticle(i).getPose();
            w = particles.getParticle(i).getWeight();
            g2.setColor(Color.BLACK);
            g2.drawRect(winX((int)p.getX()-POINT_WIDTH/2) , 
            		    winY((int)p.getY()+POINT_WIDTH/2), 
            		    POINT_WIDTH, POINT_WIDTH);
            g2.setColor(floatToCol(w));
            g2.fillRect(winX((int)p.getX()-POINT_WIDTH/2) , 
        		    winY((int)p.getY()+POINT_WIDTH/2), 
        		    POINT_WIDTH, POINT_WIDTH); 
        }
    }
        
    private int winX(int x)
    {
    	return x + X_OFFSET;
    }
    
    private int winY(int y)
    {
    	return VIS_HEIGHT - y + Y_OFFSET;
    }
    
    private Color floatToCol(float w)
    {
      int rgbNum = 255 - (int) (w*255.0);
      return new Color (rgbNum,rgbNum,rgbNum);
    }

}
