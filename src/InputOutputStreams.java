
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import lejos.pc.comm.NXTConnector;

public class InputOutputStreams
{
	private NXTConnector conn;
	private boolean USB;
	private DataInputStream inDat; 
	private DataOutputStream outDat; 

	public InputOutputStreams(boolean USB)
	{
		conn   = new NXTConnector();
		this.USB = USB;
	}
	
	public String open()
	{		
		if ( USB )
		{
			// Connect to any NXT using USB
			if (!conn.connectTo("usb://")){
				return "No NXT found using USB";
			}
		}
		else {
			// Connect to any NXT using Bluetooth
			if (!conn.connectTo("btspp://")){
				return "No NXT found using Bluetooth";
			}
		}
		inDat  = new DataInputStream(conn.getInputStream());
		outDat = new DataOutputStream(conn.getOutputStream());
		return "Connected";
	}
	
	public boolean output(float param)
	{
		boolean result;
		try {
		    outDat.writeFloat(param);
		    outDat.flush();
		    result = true;
		} catch (Exception e) {
		    result = false;
		}
		return result;
	}
	
	public float input()
	{
		float result;
		try {
            result = inDat.readFloat();
        } catch (Exception e) {
            result = -1;
        } 
		return result;
	}
	
	public String close()
	{
		String result;
		try {
			inDat.close();
			outDat.close();
			result =  "Closed streams";
		} catch (Exception e) {
			result = "IO Exception streams";
		}
		
		try {
			conn.close();
			result = result + "  Closed connection";
		} catch (Exception e) {
			result = result + "  IO Exception connection";
		}
		return result;
	}
}
