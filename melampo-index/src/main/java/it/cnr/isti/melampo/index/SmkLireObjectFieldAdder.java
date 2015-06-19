package it.cnr.isti.melampo.index;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;

import it.cnr.isti.vir.features.mpeg7.LireObject;
import it.cnr.isti.vir.similarity.metric.Metric;

public class SmkLireObjectFieldAdder extends LireObjectFieldAdder{

	public SmkLireObjectFieldAdder(Metric metric) {
		super(metric);
		// TODO Auto-generated constructor stub
	}
	
	public SmkLireObjectFieldAdder(String fieldName) {
		super(fieldName);
		// TODO Auto-generated constructor stub
	}
	
	//binary file
		public void loadBinPivots(String filename, int NumOfPivots) {
			System.out.println("Loading "+filename);
		
			m_moPivots = new LireObject[NumOfPivots];
			
	        try{
	        	DataInputStream in = null;
	        	in = new DataInputStream( new BufferedInputStream ( new FileInputStream(filename)));
	            //ObjectInputStream ros_file=new ObjectInputStream(new FileInputStream(filename));
				
	            for(int i=0;i<NumOfPivots;i++)
				{
	            	m_moPivots[i] = new LireObject(in);
				}
				
	            in.close();
	            
	        }catch(Exception e){
	            System.out.println("Cannot open file "+filename+" to load reference objects;");
	            e.printStackTrace();
	        }
	        
	        m_frmat = "%0"+(int)Math.ceil(Math.log10(NumOfPivots))+"d";
			
		}

}
