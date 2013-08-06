package it.cnr.isti.melampo.vir.lire;

import it.cnr.isti.melampo.index.settings.LireSettings;
import it.cnr.isti.melampo.vir.exceptions.VIRException;
import it.cnr.isti.vir.features.IFeaturesCollector;
import it.cnr.isti.vir.features.mpeg7.LireObject;
import it.cnr.isti.vir.readers.CoPhIRv2Reader;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;

public class LireSearcher {

	private LireSettings settings;

	public LireSearcher() throws IOException, VIRException {
		settings = new LireSettings();
	}

	public LireSearcher(File propertyFile) throws IOException, VIRException {
		settings = new LireSettings(propertyFile);
	}
	
	public LireSearcher(LireSettings settings) throws IOException, VIRException {
		this.settings = settings;
	}

	public LireObject getObject(String query)
			throws VIRException {
		IFeaturesCollector featureColl;
		LireObject obj;
		try {
				featureColl = CoPhIRv2Reader.getObj(new BufferedReader(new StringReader(query)));
			obj = new LireObject(featureColl);
		} catch (Exception e) {
			throw new VIRException(e);
		}
		return obj;
	}

//	public static void main(String[] args) {
//		try {
//			LireSearcher searcher = new LireSearcher();
//			LireObject obj = searcher
//					.getObject("ACA-F-009623-0000", true);
//			System.out.println(obj.getFeatures().toString());
//
//			String xml = Tools.file2String(new File("test/AAE-S-000124-8Z5J.xml"));
//			obj = searcher.getObject(xml, false);
//			System.out.println(obj.getFeatures().toString());
//
//		} catch (VIRException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//	}

	public LireSettings getSettings() {
		return settings;
	}

}
