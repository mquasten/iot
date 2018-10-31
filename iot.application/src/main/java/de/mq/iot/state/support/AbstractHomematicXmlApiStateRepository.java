package de.mq.iot.state.support;

import java.io.StringReader;
import java.time.Duration;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.reactive.function.client.WebClient.Builder;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import de.mq.iot.resource.ResourceIdentifier;
import de.mq.iot.state.State;

@Repository
abstract class AbstractHomematicXmlApiStateRepository implements StateRepository {
	static final String VALUE_PARAMETER_NAME = "value";
	static final String ID_PARAMETER_NAME = "id";
	static final String STATE_CHANGE_URL_PARAMETER = String.format("?ise_id={id}&new_value={value}", ID_PARAMETER_NAME, VALUE_PARAMETER_NAME);

	enum XmlApiParameters {
		Sysvarlist("sysvarlist.cgi"),

		ChangeSysvar("statechange.cgi"),
		
		FunctionList("functionlist.cgi");
		
		static final String RESOURCE_PARAMETER_NAME = "resource";
		private final String resource;

		private XmlApiParameters(final String resource) {
			this.resource = resource;
		}

		final Map<String, String> parameters(final ResourceIdentifier uniformResourceIdentifier) {
			final Map<String, String> parameters = new HashMap<>();
			parameters.putAll(uniformResourceIdentifier.parameters());
			parameters.put(RESOURCE_PARAMETER_NAME, resource);
			return parameters;
		}

		final String resource() {
			return resource;
		}
	}

	private final Duration timeout;
	private final ConversionService conversionService;

	@Autowired
	AbstractHomematicXmlApiStateRepository(final ConversionService conversionService, @Value("${mongo.webclient:500}") final Long timeout) {
		this.timeout = Duration.ofMillis(timeout);
		this.conversionService = conversionService;
	}

	@Override
	public Collection<Map<String, String>> findStates(final ResourceIdentifier resourceIdentifier) {
		Assert.notNull(resourceIdentifier, "ResourceIdentifier is mandatory.");

		final ResponseEntity<String> res = webClientBuilder().build().get().uri(resourceIdentifier.uri(), XmlApiParameters.Sysvarlist.parameters(resourceIdentifier)).exchange().block(timeout).toEntity(String.class).block(timeout);

		httpStatusGuard(res);

		
		final NodeList nodes = evaluate(res, "/systemVariables/systemVariable");
		return IntStream.range(0, nodes.getLength()).mapToObj(i -> attributesToMap(nodes.item(i).getAttributes())).collect(Collectors.toList());

	}

	private NodeList evaluate(final ResponseEntity<String> res, final String path) {
		try {
			return (NodeList) xpath().evaluate(path, new InputSource(new StringReader(res.getBody())), XPathConstants.NODESET);
		} catch (XPathExpressionException ex) {
			throw new IllegalStateException("Unable to evalualte xpath expression", ex);

		}
	}

	private void httpStatusGuard(final ResponseEntity<String> res) {
		if (res.getStatusCode().is2xxSuccessful()) {
			return;
		}
		throw newHttpStatusCodeException(res.getStatusCode(), res.getStatusCode().getReasonPhrase());
	}

	private Map<String, String> attributesToMap(final NamedNodeMap nodeMap) {
		return IntStream.range(0, nodeMap.getLength()).mapToObj(j -> new AbstractMap.SimpleImmutableEntry<String, String>(nodeMap.item(j).getNodeName(), nodeMap.item(j).getNodeValue())).collect(Collectors.toMap(Entry::getKey, Entry::getValue));
	}

	@Override
	public void changeState(final ResourceIdentifier resourceIdentifier, final State<?> state) {
		Assert.notNull(state, "State is mandatory.");
		Assert.notNull(state.value(), "State value is mandatiory.");
		Assert.notNull(resourceIdentifier, "ResourceIdentifier is mandatory.");

		final Map<String, String> parameter = XmlApiParameters.ChangeSysvar.parameters(resourceIdentifier);
		final String uri = resourceIdentifier.uri() + STATE_CHANGE_URL_PARAMETER;
		parameter.put(ID_PARAMETER_NAME, "" + state.id());
		parameter.put(VALUE_PARAMETER_NAME, conversionService.convert(state.value(), String.class));

		final ResponseEntity<String> res = webClientBuilder().build().put().uri(uri, parameter).exchange().block(timeout).toEntity(String.class).block(timeout);
		if( ! StringUtils.hasText(res.getBody())) {
			throw newHttpStatusCodeException(HttpStatus.BAD_REQUEST, "Result expected." ) ;
		}
	
		httpStatusGuard(res);
		
		resultChangedGuard(evaluate(res, "/result/*"));
	}

	private void resultChangedGuard(final NodeList nodeList) {
		if (nodeList.getLength() != 1) {
			throw newHttpStatusCodeException(HttpStatus.BAD_REQUEST, "Result expected.");
		}

		final String result = nodeList.item(0).getNodeName();
		;
		if (result.equalsIgnoreCase("changed")) {
			return;
		}

		if (result.equalsIgnoreCase("not_found")) {
			throw newHttpStatusCodeException(HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.getReasonPhrase());
		}

		throw newHttpStatusCodeException(HttpStatus.BAD_REQUEST, result);
	}

	private  HttpStatusCodeException newHttpStatusCodeException(final HttpStatus status, final String result) {
		return new HttpStatusCodeException(status, result) {
			private static final long serialVersionUID = 1L;

		};
	}
	
	
	
	Collection<Long>  findChannelIds(final ResourceIdentifier resourceIdentifier, final String function) {
		Assert.notNull(function, "Function is mandatory.");

		
		
		final ResponseEntity<String> res = webClientBuilder().build().get().uri(resourceIdentifier.uri(), XmlApiParameters.FunctionList.parameters(resourceIdentifier)).exchange().block(timeout).toEntity(String.class).block(timeout);

		httpStatusGuard(res);
		
		
		final NodeList nodes = evaluate(res, String.format("/functionList/function[@name='%s']/channel/@ise_id", function));
		
		return IntStream.range(0, nodes.getLength()).mapToObj(i -> conversionService.convert(nodes.item(i).getFirstChild().getNodeValue(), Long.class)).collect(Collectors.toList());
			 
		
	}
	

	@Lookup
	abstract Builder webClientBuilder();

	@Lookup
	abstract XPath xpath();

}
