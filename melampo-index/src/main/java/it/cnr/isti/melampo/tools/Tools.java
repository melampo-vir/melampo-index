package it.cnr.isti.melampo.tools;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Tools {

	static char startAlpha = 'a';
	static char endAlpha = 'z';
	
	static int alphabetSize = (int)endAlpha - (int)startAlpha;
	
	public  static String int2Str(int n){		
		
		char r = (char)(n%alphabetSize+startAlpha);
		
		String s = ""+r;
		n = n / alphabetSize;
		
		while(n>0){
			r = (char)(n%alphabetSize+startAlpha);
			s = s+r;
			n = n / alphabetSize;
		}
		
		return s;
		
	}
	
	public static String file2String(File source) throws IOException {
		String content = null;
		DataInputStream dis = null;
		final byte[] buffer = new byte[(int) source.length()];
		try {
			dis = new DataInputStream(new BufferedInputStream(
					new FileInputStream(source)));

			dis.readFully(buffer);
			content = new String(buffer, "UTF-8");
		} finally {
			if (dis != null)
				dis.close();
		}
		return content;
	}
	
    public static void string2File(String text, File file) throws IOException {
		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(file);
			fileWriter.write(text);
		} finally {
				if (fileWriter != null) 
					fileWriter.close();
		}
	}
    
public static String[][] getAllOrderedProperties(File propertyFile) throws IOException {
        
        BufferedReader bufferedReader = null;
        ArrayList<String[]> temp = new ArrayList<String[]>();
        String[][] orderedProperties = null;
        try {
            
            //Construct the BufferedReader object
            bufferedReader = new BufferedReader(new FileReader(propertyFile));
            
            String line = null;
            
            while ((line = bufferedReader.readLine()) != null) {
            	line = line.trim();
            	if (!line.startsWith("#")) {
            		int equalsIndex = line.indexOf("=");
            		if (equalsIndex != -1) {
            			String[] property = {line.substring(0, equalsIndex).trim(), line.substring(equalsIndex + 1).trim()};
            			temp.add(property);
            		}
            	}
                //Process the data, here we just print it out
            }
            orderedProperties = new String[temp.size()][2];
            temp.toArray(orderedProperties);
        } finally {
            //Close the BufferedReader
                if (bufferedReader != null)
                    bufferedReader.close();
        }
        return orderedProperties;
    }

}
