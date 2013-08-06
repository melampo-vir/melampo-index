package it.cnr.isti.melampo.index.searching;

import it.cnr.isti.melampo.vir.exceptions.VIRException;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.PerFieldAnalyzerWrapper;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.Searcher;

public interface MelampoSearcher {

	public void  OpenIndex(File propertyFile)  throws IOException, VIRException;
	
	public Searcher getIndex();
	
	public String prepareQuery(String value, String field,
			boolean isQueryID) throws VIRException;

	public void query() throws ParseException, IOException;
	
	public Occur getOccur();

	public void reorderResults();

	public String[][] getResults(int startFrom, int numElements)  throws IOException;
	
	public IndexReader getIndexReader();
		
	public Analyzer getAnalyzer();
}
