package it.cnr.isti.melampo.index.settings;

import it.cnr.isti.melampo.vir.exceptions.BoFException;
import it.cnr.isti.melampo.vir.exceptions.VIRException;
import it.cnr.isti.vir.file.FeaturesCollectorsArchives;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

public class LireSettings {

	private FeaturesCollectorsArchives fcArchives;
	private String featureClass;
//	private int m_itemsToIndex = 0;
	private boolean m_createIndex = true;
	private String m_luceneIndexPathLire;
	private int m_nPivots;
	private String m_PivotsPath;
	private int m_toppivsI;
	private int m_toppivsQ;

	public LireSettings() throws IOException, VIRException {
		loadProperties(new File("conf/LIRE_MP7ALL.properties"));
	}

	public int getToppivsI() {
		return this.m_toppivsI;
	}

	public LireSettings(File propertyFile) throws IOException, VIRException {
		loadProperties(propertyFile);
	}

	private void loadProperties(File propertyFile) throws IOException,
			VIRException {
		Properties indexprops = new Properties();
		FileInputStream pfis = null;

		File parentDir = propertyFile.getParentFile();

		ArrayList<File> archives = new ArrayList<File>();
		try {
			pfis = new FileInputStream(propertyFile);
			indexprops.load(pfis);

			int index = 0;

			String archiveFile = null;
			while ((archiveFile = indexprops.getProperty("archive_" + index)) != null) {
				if (archiveFile.startsWith("*")) {
					archiveFile = parentDir.getPath()
							+ archiveFile.substring(1);
				}
				archives.add(new File(archiveFile));
				index++;
			}

			File[] archiveFiles = new File[archives.size()];
			archives.toArray(archiveFiles);

			// feature classes
			// index = 0;
			// featureClass = null;
			// FeatureClassCollector fcc = null;
			try {
				// if ((featureClass =
				// indexprops.getProperty("featureClassImpl_" + index)) != null)
				// {
				// fcc = new FeatureClassCollector(Class.forName(featureClass));
				// index++;
				// while ((featureClass =
				// indexprops.getProperty("featureClassImpl_" + index)) != null)
				// {
				// fcc.add(Class.forName(featureClass));
				// index++;
				// }
				// } else {
				// throw new
				// VIRException("Error, unable to retrieve featureClassImpl_"
				// +index + " parameter");
				// }
				// CoPhIRv2Reader.setFeatures(fcc);
				fcArchives = new FeaturesCollectorsArchives(archiveFiles);

			} catch (Exception e) {
				throw new BoFException(e);
			}

//			m_itemsToIndex = Integer.parseInt(indexprops
//					.getProperty("items_to_index"));

			this.m_luceneIndexPathLire = indexprops
					.getProperty("luceneIndexPath");
			if (this.m_luceneIndexPathLire.startsWith("*")) {
				this.m_luceneIndexPathLire = (parentDir.getPath() + this.m_luceneIndexPathLire
						.substring(1));
			}

			this.m_createIndex = Boolean.parseBoolean(indexprops
					.getProperty("createIndex"));
			this.m_nPivots = Integer.parseInt(indexprops
					.getProperty("num_of_pivots"));

			this.m_PivotsPath = indexprops.getProperty("PivotsPath");
			if (this.m_PivotsPath.startsWith("*")) {
				this.m_PivotsPath = (parentDir.getPath() + this.m_PivotsPath
						.substring(1));
			}

			this.m_toppivsI = Integer.parseInt(indexprops
					.getProperty("maxpivsIndex"));
			this.m_toppivsQ = Integer.parseInt(indexprops
					.getProperty("maxpivsQuery"));
		} finally {
			if (pfis != null)
				pfis.close();
		}
	}

	public String getFeatureClass() {
		return this.featureClass;
	}

	public FeaturesCollectorsArchives getFCArchives() {
		return fcArchives;
	}

//	public int getItemsToIndex() {
//		return m_itemsToIndex;
//	}

	public boolean isCreateIndex() {
		return this.m_createIndex;
	}

	public int getnPivots() {
		return this.m_nPivots;
	}

	public String getPivotsPath() {
		return this.m_PivotsPath;
	}

	public int getToppivsQ() {
		return this.m_toppivsQ;
	}

	public String getLuceneIndexPathLire() {
		return this.m_luceneIndexPathLire;
	}
}