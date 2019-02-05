package de.mq.iot.synonym.support;



import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.QuoteMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
		try(final BufferedWriter writer = Files.newBufferedWriter(Paths.get("synonyms.csv"));) {
		write(synonyms, writer);}
	}





	private void write(final List<Synonym> synonyms, final BufferedWriter writer) throws IOException {
		try(final CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                .withHeader("key", "value", "type", "description").withQuoteMode(QuoteMode.MINIMAL).withDelimiter(';'))) {
		
		
				synonyms.forEach(synonym -> print(csvPrinter,synonym));
		}
	}





	protected void print(final CSVPrinter csvPrinter, final Synonym synonym) {
		try {
			csvPrinter.printRecord(synonym.key(), synonym.value(), synonym.type(), synonym.description());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
