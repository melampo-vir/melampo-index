package it.cnr.isti.melampo.index;

import it.cnr.isti.melampo.index.settings.LireSettings;
import it.cnr.isti.melampo.vir.exceptions.VIRException;

import java.io.File;
import java.io.IOException;

public class BaseIndexTest {

	public static final String TEST_DOC_ID = "cluj_avram_iancu.xml";
	
	public BaseIndexTest() {
		super();
	}

	protected LireSettings getSettingsFromFile() throws IOException, VIRException {
		return new LireSettings( getConfigIndexFile());
	}

	protected File getConfigIndexFile() {
		return new File(getIndexConfigFolder(), "LIRE_MP7ALL.properties");
	}

	protected File getIndexConfigFolder() {
		return new File( "./src/test/resources/lire/conf");
	}

}