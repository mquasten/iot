package de.mq.iot.support;



import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
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
public class CsvServiceImpl  {
	
	private Function<String, Writer> supplier = name -> newWriter(name);

	private Writer newWriter(String name)  {
		try {
			return Files.newBufferedWriter(Paths.get(name));
		} catch (final IOException ex) {
			throw new IllegalStateException(ex);
		}
	}
	
	
	private  final SynonymService synonymService; 
	
	@Autowired
	public CsvServiceImpl(SynonymService synonymService) {
		this.synonymService = synonymService;
	}


	
	
	
	
	@Commands(commands = {  @Command( name = "exportSynonyms", arguments = {}) })
	public  void export()   {
		final List<Synonym> synonyms = new ArrayList<>(synonymService.deviveSynonyms());
		try(final Writer writer = supplier.apply("export.csv");) {
		write(synonyms, writer);
		} catch (IOException ex) {
			throw new IllegalStateException(ex);
		}
	}





	private void write(final List<Synonym> synonyms, final Writer writer)   {
		try(final CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                .withHeader("key", "value", "type", "description").withQuoteMode(QuoteMode.MINIMAL).withDelimiter(';'))) {
		
		
				synonyms.forEach(synonym -> print(csvPrinter,synonym));
		} catch (IOException ex) {
			throw new IllegalStateException(ex);
		}
	}





	private void print(final CSVPrinter csvPrinter, final Synonym synonym) {
		try {
			csvPrinter.printRecord(synonym.key(), synonym.value(), synonym.type(), synonym.description());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}





	
}
