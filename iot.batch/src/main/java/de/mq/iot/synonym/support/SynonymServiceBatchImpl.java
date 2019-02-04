package de.mq.iot.synonym.support;



import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;

import de.mq.iot.state.Command;
import de.mq.iot.state.Commands;
import de.mq.iot.synonym.Synonym;
import de.mq.iot.synonym.SynonymService;

@Service
public class SynonymServiceBatchImpl implements SynonymServiceBatch {
	
	
	private  final SynonymService synonymService; 
	
	@Autowired
	public SynonymServiceBatchImpl(SynonymService synonymService) {
		this.synonymService = synonymService;
	}


	
	
	
	/* (non-Javadoc)
	 * @see de.mq.iot.synonym.support.SynonymService#exportSynonyms()
	 */
	@Override
	@Commands(commands = {  @Command( name = "exportSynonyms", arguments = {}) })
	public  void exportSynonyms() throws Exception {
		final List<Synonym> synonyms = new ArrayList<>(synonymService.deviveSynonyms());
		
		try ( Writer writer = new FileWriter("synonyms.csv");) {
	     StatefulBeanToCsv<Synonym> beanToCsv = new StatefulBeanToCsvBuilder<Synonym>(writer).withThrowExceptions(true).build();
	   
	    
	     beanToCsv.write(synonyms);
	    
		}
		System.out.println(synonyms.get(0).key());
	}

}
