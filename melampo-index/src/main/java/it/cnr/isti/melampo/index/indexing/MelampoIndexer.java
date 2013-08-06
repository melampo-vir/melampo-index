package it.cnr.isti.melampo.index.indexing;

import it.cnr.isti.melampo.vir.exceptions.BoFException;
import it.cnr.isti.melampo.vir.exceptions.VIRException;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.index.CorruptIndexException;

public interface MelampoIndexer {
	public void OpenIndex(File propertyFile) throws IOException, VIRException;

	public void addDocument(String doc, String docId) throws BoFException,
	CorruptIndexException, IOException;

	public void closeIndex() throws CorruptIndexException, IOException;
}
