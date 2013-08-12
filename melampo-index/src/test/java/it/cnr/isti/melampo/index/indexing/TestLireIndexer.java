package it.cnr.isti.melampo.index.indexing;

import it.cnr.isti.melampo.index.BaseIndexTest;
import it.cnr.isti.melampo.index.settings.LireSettings;
import it.cnr.isti.melampo.vir.exceptions.VIRException;
import it.cnr.isti.vir.features.FeaturesCollectorArr;
import it.cnr.isti.vir.features.mpeg7.LireObject;
import it.cnr.isti.vir.id.IDString;
import it.cnr.isti.vir.readers.CoPhIRv2Reader;
import it.cnr.isti.vir.similarity.metric.LireMetric;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.junit.Test;

public class TestLireIndexer extends BaseIndexTest {

	LireIndexer mp7cIndex = null;

	@Test
	public void testImageIndexing() throws Exception {

		final File featuresFile = new File(
				"./src/test/resources/features/cluj_avram_iancu.xml");
		openIndex();

		indexFromFeaturesFile(featuresFile);

		mp7cIndex.closeIndex();
		
		//test successful if no exception thrown
		//the verification of the indexing process can be checked (by now) only through search 
	}

	@Test
	public void testImageIndexingAll() throws Exception {

		File featuresFolder = new File("./src/test/resources/features/");
		openIndex();

		String[] featureFileNames = featuresFolder.list();
		File featuresFile;
		
		for (int i = 0; i < featureFileNames.length; i++) {
			featuresFile = new File(featuresFolder, featureFileNames[i]);
			indexFromFeaturesFile(featuresFile);
		}
		
		mp7cIndex.closeIndex();
		///test successful if no exception thrown
		//the verification of the indexing process can be checked (by now) only through search 
	}

	protected void indexFromFeaturesFile(final File featuresFile)
			throws Exception {

		FeaturesCollectorArr features = CoPhIRv2Reader
				.getObj(new BufferedReader(new FileReader(featuresFile)));
		String docId = featuresFile.getName();
		features.setID(new IDString(docId));

		// coll.add(features);
		LireObject obj = new LireObject(features);
		mp7cIndex.addDocument(obj, docId);
	}

	public void openIndex() throws IOException, VIRException {
		if (mp7cIndex == null) {
			CoPhIRv2Reader.setFeatures(LireMetric.reqFeatures);
			// img2Features = new Image2Features(confDir);
			LireSettings settings = getSettingsFromFile();
			// coll = settings.getFCArchives().getArchive(0);
			mp7cIndex = new LireIndexer();
			mp7cIndex.OpenIndex(settings);
		}
	}

}
