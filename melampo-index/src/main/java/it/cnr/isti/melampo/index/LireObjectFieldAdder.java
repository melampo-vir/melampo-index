package it.cnr.isti.melampo.index;

import it.cnr.isti.melampo.tools.OrderTopKArrayList;
import it.cnr.isti.melampo.vir.exceptions.BoFException;
import it.cnr.isti.vir.features.mpeg7.LireObject;
import it.cnr.isti.vir.similarity.metric.Metric;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;

public class LireObjectFieldAdder {
	
	protected String m_fieldname;
	protected LireObject[] m_moPivots;
	protected Metric m_metric;
	protected String m_frmat;
	
	public LireObjectFieldAdder(Metric metric){
		m_metric = metric;
		
		if (metric.toString().contains("LireMetric")){
			m_fieldname = Parameters.LIRE_MP7ALL;
			return;
		}
		
		if (metric.toString().contains("LireSCMetric")){
			m_fieldname = Parameters.MP7SC;
			return;
		}
		
		if (metric.toString().contains("LireCLMetric")){
			m_fieldname = Parameters.MP7CL;
			return;
		}
		
		if (metric.toString().contains("LireEHMetric")){
			m_fieldname = Parameters.MP7EH;
			return;
		}
		
	}
	
	public void addFieldToDoc(org.apache.lucene.document.Document doc, LireObject o, int toppivs) throws IOException{
		
		String field = metricObjectToString(o, toppivs);
		
	    doc.add(new Field(m_fieldname, field, Field.Store.NO, Field.Index.ANALYZED, Field.TermVector.YES));
	}

	
	public LireObjectFieldAdder(String fileadname){
		m_fieldname = fileadname;
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
	
	//to be used for indexing
	public String metricObjectToString(LireObject mo, int toppivs){
		String strDoc = "";
		int j,k;

		OrderTopKArrayList pivdist = new OrderTopKArrayList(toppivs);
		
		for(j=0;j<m_moPivots.length;j++){
			
			pivdist.insert(pivDist(j, mo), j);
		}

				
		for(j=0;j<toppivs;j++){	

			for(k=0;k<toppivs-j;k++) strDoc = strDoc + String.format(m_frmat,pivdist.get(j))+" ";
			
		}
		return strDoc;
	
}
	
	// to be used only for querying

	public String metricObjectToStringQ(LireObject mo, int toppivs, int maxpivs){
		String strDoc = "";
		int j,k;

		OrderTopKArrayList pivdist = new OrderTopKArrayList(toppivs);
		
		synchronized (LireObjectFieldAdder.class) {
			for(j=0;j<m_moPivots.length;j++){
				
				pivdist.insert(pivDist(j, mo), j);
			}
		}
				
		for(j=0;j<toppivs;j++){	

			//strDoc = strDoc +String.format(m_frmat,pivdist.get(j))+"^"+(maxpivs-j)+" ";
			strDoc = strDoc +String.format(m_frmat,pivdist.get(j))+"^"+(toppivs-j)+" ";
			
		}
		return strDoc;
	
	}
	
	public String inverseMetricObjectToStringQ(LireObject mo, int toppivs, int maxpivs){
		String strDoc = "";
		int j,k;

		OrderTopKArrayList pivdist = new OrderTopKArrayList(m_moPivots.length);
		
		for(j=0;j<m_moPivots.length;j++){
			
			pivdist.insert(pivDist(j, mo), j);
		}

		k = maxpivs; 
		for(j=m_moPivots.length-1;j>=m_moPivots.length-toppivs;j--){	

			strDoc = strDoc + String.format(m_frmat,pivdist.get(j))+"^"+(k)+" ";
			k--;
			
		}
		return strDoc;
	
}
	
	public double pivDist(int j, LireObject mo) {
		return m_metric.distance(m_moPivots[j], mo);
	}
	
	public void AddIDField(Document doc, String id) throws CorruptIndexException, IOException, BoFException{
		
		doc.add(new Field(Parameters.IDFIELD, id, Field.Store.YES, Field.Index.NOT_ANALYZED, Field.TermVector.NO));		
	}
	
}
