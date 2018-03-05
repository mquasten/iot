package de.mq.iot.state;

import java.io.StringReader;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.reactive.function.client.WebClient.Builder;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

@Repository
abstract class HomematicXmlApiStateRepositoryImpl {

	Collection<Map<String, String>> findStates() {

		// http://mq65.ddns.net:2000/addons/xmlapi/sysvarlist.cgi{}

		final ResponseEntity<String> res = webClientBuilder().build().get()
				.uri("http://{host}:{port}/addons/xmlapi/sysvarlist.cgi", "mq65.ddns.net", 2000).exchange().block()
				.toEntity(String.class).block();
		httpStatusGuard(res);

		final NodeList nodes = evaluate(res);

		return IntStream.range(0, nodes.getLength()).mapToObj(i -> attributesToMap(nodes.item(i).getAttributes()))
				.collect(Collectors.toList());

	}

	private NodeList evaluate(final ResponseEntity<String> res) {
		try {
			return (NodeList) xpath().evaluate("/systemVariables/systemVariable",
					new InputSource(new StringReader(res.getBody())), XPathConstants.NODESET);
		} catch (XPathExpressionException ex) {
			throw new IllegalStateException("Unable to evalualte xpath expression", ex);

		}
	}

	private void httpStatusGuard(final ResponseEntity<String> res) {
		if (res.getStatusCode().is4xxClientError() || res.getStatusCode().is5xxServerError()) {

			throw new HttpStatusCodeException(res.getStatusCode(), res.getStatusCode().getReasonPhrase()) {

				private static final long serialVersionUID = 1L;
			};

		}
	}

	private Map<String, String> attributesToMap(final NamedNodeMap nodeMap) {
		final Map<String, String> attributes = IntStream.range(0, nodeMap.getLength())
				.mapToObj(j -> new AbstractMap.SimpleImmutableEntry<String, String>(nodeMap.item(j).getNodeName(),
						nodeMap.item(j).getNodeValue()))
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue));
		return attributes;
	}

	@Lookup
	abstract Builder webClientBuilder();

	@Lookup
	abstract XPath xpath();

}
