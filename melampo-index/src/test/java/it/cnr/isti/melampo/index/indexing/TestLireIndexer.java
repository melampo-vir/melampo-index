package it.cnr.isti.melampo.index.indexing;

import it.cnr.isti.melampo.index.settings.LireSettings;
import it.cnr.isti.melampo.vir.exceptions.VIRException;
import it.cnr.isti.vir.features.FeaturesCollectorArr;
import it.cnr.isti.vir.features.FeaturesCollectorException;
import it.cnr.isti.vir.features.mpeg7.LireObject;
import it.cnr.isti.vir.features.mpeg7.vd.MPEG7VDFormatException;
import it.cnr.isti.vir.id.IDString;
import it.cnr.isti.vir.readers.CoPhIRv2Reader;
import it.cnr.isti.vir.similarity.metric.LireMetric;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

import org.junit.Test;

public class TestLireIndexer {
	
	LireIndexer mp7cIndex = null;
	
	@Test
	public void testImageIndexing() throws Exception {

		FeaturesCollectorArr features = CoPhIRv2Reader
				.getObj(new BufferedReader(new FileReader("./src/test/resources/features/cluj_avram_iancu.xml")));
		final String docId = "cluj_avram_iancu.xml";
		features.setID(new IDString(docId));
		
		// coll.add(features);
		LireObject obj = new LireObject(features);
		openIndex();
		mp7cIndex.addDocument(obj, docId);
		mp7cIndex.closeIndex();
	}

	public void openIndex() throws IOException, VIRException{
		CoPhIRv2Reader.setFeatures(LireMetric.reqFeatures);
		//img2Features = new Image2Features(confDir);
		String confDir = "./src/test/resources/lire/conf";
		LireSettings settings = new LireSettings(new File(confDir, "LIRE_MP7ALL.properties"));
		// coll = settings.getFCArchives().getArchive(0);
		mp7cIndex = new LireIndexer();
		mp7cIndex.OpenIndex(settings);
	}
	
	
}
