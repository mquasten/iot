package de.mq.iot.state;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collection;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.xml.xpath.Jaxp13XPathTemplate;
import org.springframework.xml.xpath.XPathOperations;
import org.w3c.dom.Node;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import de.mq.iot.domain.state.State;


class HomematicXmlApiStateRepositoryImpl {
	
	

	
	Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
	
	
		

	Collection<State<?>> findStates() throws UnirestException {
		marshaller.setPackagesToScan("de.mq.iot.state");
		
		marshaller.setMappedClass(HomematicState.class);
		
		HttpResponse<String> response = Unirest.get("http://{host}/addons/xmlapi/sysvarlist.cgi").routeParam("host", "192.168.2.104").asString();
		
		
		System.out.println(response.getBody());
		
		final XPathOperations  xPathOperations  = new Jaxp13XPathTemplate();
		xPathOperations.evaluate("/systemVariables/systemVariable", new StreamSource(new StringReader(response.getBody())), (node, num) -> {
			
			
			String var = nodeToString(node);
			
			System.out.println(var);
			HomematicState result  = (HomematicState) marshaller.unmarshal(new StreamSource(new StringReader(var)));
			
		
			System.out.println(result.getId()+ "," + result.getName()+ "," + result.getMin());
			return null;
		});
		return null;
		
		
		
		
		
	}
	
	

	private  String nodeToString(Node node)  {
	   
		try {
			final StringWriter buf = new StringWriter();
		    
			final Transformer xform = TransformerFactory.newInstance().newTransformer();
			xform.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			xform.transform(new DOMSource(node), new StreamResult(buf));
			return buf.toString();
		} catch (Exception ex) {
			 throw new IllegalArgumentException(ex);
		} 
	   
	   
	}

}
