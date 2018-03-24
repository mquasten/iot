package de.mq.iot.state.support;

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

import de.mq.iot.resource.ResourceIdentifier;

@Repository
abstract class HomematicXmlApiStateRepositoryImpl implements StateRepository {

	public Collection<Map<String, String>> findStates(final ResourceIdentifier uniformResourceIdentifier) {

		final ResponseEntity<String> res = webClientBuilder().build().get().uri(uniformResourceIdentifier.uri(), uniformResourceIdentifier.parameters()).exchange().block().toEntity(String.class).block();

		httpStatusGuard(res);

		final NodeList nodes = evaluate(res);
		return IntStream.range(0, nodes.getLength()).mapToObj(i -> attributesToMap(nodes.item(i).getAttributes())).collect(Collectors.toList());

	}

	private NodeList evaluate(final ResponseEntity<String> res) {
		try {
			return (NodeList) xpath().evaluate("/systemVariables/systemVariable", new InputSource(new StringReader(res.getBody())), XPathConstants.NODESET);
		} catch (XPathExpressionException ex) {
			throw new IllegalStateException("Unable to evalualte xpath expression", ex);

		}
	}

	private void httpStatusGuard(final ResponseEntity<String> res) {
		if (! res.getStatusCode().is2xxSuccessful()) {

			throw new HttpStatusCodeException(res.getStatusCode(), res.getStatusCode().getReasonPhrase()) {

				private static final long serialVersionUID = 1L;
			};

		}
	}

	private Map<String, String> attributesToMap(final NamedNodeMap nodeMap) {
		final Map<String, String> attributes = IntStream.range(0, nodeMap.getLength()).mapToObj(j -> new AbstractMap.SimpleImmutableEntry<String, String>(nodeMap.item(j).getNodeName(), nodeMap.item(j).getNodeValue()))
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue));
		return attributes;
	}

	@Lookup
	abstract Builder webClientBuilder();

	@Lookup
	abstract XPath xpath();

}
