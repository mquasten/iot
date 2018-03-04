package de.mq.iot.state;

import java.util.Collection;

import org.springframework.http.ResponseEntity;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.mashape.unirest.http.exceptions.UnirestException;

import de.mq.iot.domain.state.State;


class HomematicXmlApiStateRepositoryImpl {
	
	
	
	
	Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
	
	
		

	Collection<State<?>> findStates() throws UnirestException {
		marshaller.setPackagesToScan("de.mq.iot.state");
		
		marshaller.setMappedClass(HomematicState.class);
		
		//HttpResponse<String> response = Unirest.get("http://{host}/addons/xmlapi/sysvarlist.cgi").routeParam("host", "192.168.2.104").asString();
		
		
		//System.out.println(response.getBody());
		
		
		
		ResponseEntity<String>  res = WebClient.builder().build().get().uri("http://{host}/addons/xmlapi/sysvarlist.cgi",  "192.168.2.104").exchange().block().toEntity(String.class).block();
		
		if( res.getStatusCode().is4xxClientError() || res.getStatusCode().is5xxServerError() ) {
			
			throw new HttpStatusCodeException(res.getStatusCode(), res.getStatusCode().getReasonPhrase()) {

				private static final long serialVersionUID = 1L;
			};
			
		}
		System.out.println(res.getBody());
		
		
		//final String response = WebClient.create("http://192.168.2.104/addons/xmlapi/sysvarlist.cgix"  ).get().retrieve().bodyToMono(String.class).block();
		
		
		
/*		
		final XPathOperations  xPathOperations  = new Jaxp13XPathTemplate();
		xPathOperations.evaluate("/systemVariables/systemVariable", new StreamSource(new StringReader(response)), (node, num) -> {
			
			
			String var = nodeToString(node);
			
			System.out.println(var);
			HomematicState result  = (HomematicState) marshaller.unmarshal(new StreamSource(new StringReader(var)));
			
		
			System.out.println(result.getId()+ "," + result.getName()+ "," + result.getMin());
			return null;
		});
		return null;
		
		
		*/
		return null;
		
		
	}
	
	


}
