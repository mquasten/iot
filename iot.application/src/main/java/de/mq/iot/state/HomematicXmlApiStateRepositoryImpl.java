package de.mq.iot.state;

import java.io.IOException;
import java.io.StringBufferInputStream;
import java.util.Collection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.reactive.function.client.WebClient.Builder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.mq.iot.domain.state.State;

@Repository
abstract class HomematicXmlApiStateRepositoryImpl {

	Collection<State<?>> findStates() throws ParserConfigurationException, SAXException, IOException {

		// http://mq65.ddns.net:2000/addons/xmlapi/sysvarlist.cgi{}

		final ResponseEntity<String> res = webClientBuilder().build().get()
				.uri("http://{host}:{port}/addons/xmlapi/sysvarlist.cgi", "mq65.ddns.net", 2000).exchange().block()
				.toEntity(String.class).block();

		if (res.getStatusCode().is4xxClientError() || res.getStatusCode().is5xxServerError()) {

			throw new HttpStatusCodeException(res.getStatusCode(), res.getStatusCode().getReasonPhrase()) {

				private static final long serialVersionUID = 1L;
			};

		}
		System.out.println(res.getBody());

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();

		Document doc = builder.parse(new StringBufferInputStream(res.getBody()));
		Element root = doc.getDocumentElement();

		NodeList list = root.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			NamedNodeMap attributes = list.item(i).getAttributes();
			for (int j = 0; j < attributes.getLength(); j++) {
				final Node node = attributes.item(j);
				System.out.println(node.getNodeName() + ":" + node.getNodeValue());
			}
			System.out.println("************************************");
		}

		// final String response =
		// WebClient.create("http://192.168.2.104/addons/xmlapi/sysvarlist.cgix"
		// ).get().retrieve().bodyToMono(String.class).block();

		/*
		 * final XPathOperations xPathOperations = new Jaxp13XPathTemplate();
		 * xPathOperations.evaluate("/systemVariables/systemVariable", new
		 * StreamSource(new StringReader(response)), (node, num) -> {
		 * 
		 * 
		 * String var = nodeToString(node);
		 * 
		 * System.out.println(var); HomematicState result = (HomematicState)
		 * marshaller.unmarshal(new StreamSource(new StringReader(var)));
		 * 
		 * 
		 * System.out.println(result.getId()+ "," + result.getName()+ "," +
		 * result.getMin()); return null; }); return null;
		 * 
		 * 
		 */
		return null;

	}

	@Lookup
	abstract Builder webClientBuilder();

}
