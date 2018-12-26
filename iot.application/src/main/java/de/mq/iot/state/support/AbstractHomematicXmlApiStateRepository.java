package de.mq.iot.state.support;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.time.Duration;
import java.util.AbstractMap;
import java.util.Arrays;
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
import org.springframework.dao.support.DataAccessUtils;
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
	private static final String VALUE_ATTRIBUTE = "value";
	private static final String ID_ATTRIBUTE = "ise_id";
	private static final String NAME_ATTRIBUTE = "name";
	static final String VALUE_PARAMETER_NAME = VALUE_ATTRIBUTE;
	static final String ID_PARAMETER_NAME = "id";
	static final String STATE_CHANGE_URL_PARAMETER = String.format("?ise_id={id}&new_value={value}", ID_PARAMETER_NAME, VALUE_PARAMETER_NAME);

	enum XmlApiParameters {
		Sysvarlist("sysvarlist.cgi"),

		ChangeSysvar("statechange.cgi"),

		FunctionList("functionlist.cgi"),
		Version("version.cgi"),

		RoomList("roomlist.cgi"), StateList("statelist.cgi");

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
	AbstractHomematicXmlApiStateRepository(final ConversionService conversionService, @Value("${http.timeout:500}") final Long timeout) {
		this.timeout = Duration.ofMillis(timeout);
		this.conversionService = conversionService;
	}

	@Override
	public Collection<Map<String, String>> findStates(final ResourceIdentifier resourceIdentifier) {
		Assert.notNull(resourceIdentifier, "ResourceIdentifier is mandatory.");

		final ResponseEntity<byte[]> res = webClientBuilder().build().get().uri(resourceIdentifier.uri(), XmlApiParameters.Sysvarlist.parameters(resourceIdentifier)).exchange().block(timeout).toEntity(byte[].class).block(timeout);

		httpStatusGuard(res);

		final NodeList nodes = evaluate(res.getBody(), "/systemVariables/systemVariable");
		return  IntStream.range(0, nodes.getLength()).mapToObj(i -> attributesToMap(nodes.item(i).getAttributes())).collect(Collectors.toList());
	
	}

	private NodeList evaluate(final byte[] res, final String path) {
		final InputSource source = new InputSource(new ByteArrayInputStream(res));
		source.setEncoding("ISO-8859-1");
		return evaluate(source, path);
	}

	private NodeList evaluate(final String res, final String path) {
		final InputSource source = new InputSource(new StringReader(res));
		return evaluate(source, path);
	}

	private NodeList evaluate(final InputSource inputSource, final String path) {
		try {
			return (NodeList) xpath().evaluate(path, inputSource, XPathConstants.NODESET);
		} catch (XPathExpressionException ex) {
			throw new IllegalStateException("Unable to evalualte xpath expression", ex);

		}
	}

	private void httpStatusGuard(final ResponseEntity<?> res) {
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
		if (!StringUtils.hasText(res.getBody())) {
			throw newHttpStatusCodeException(HttpStatus.BAD_REQUEST, "Result expected.");
		}

		httpStatusGuard(res);

		resultChangedGuard(evaluate(res.getBody(), "/result/*"));
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see de.mq.iot.state.support.StateRepository#changeState(de.mq.iot.resource.ResourceIdentifier, java.util.Collection)
	 */
	@Override
	public void changeState(final ResourceIdentifier resourceIdentifier, final Collection<Entry<Long,String>> states  ) {
		
		Assert.notNull(states, "Statelist is mandatory.");
		Assert.notNull(resourceIdentifier, "ResourceIdentifier is mandatory.");

		final Map<String, String> parameter = XmlApiParameters.ChangeSysvar.parameters(resourceIdentifier);
		final String uri = resourceIdentifier.uri() + STATE_CHANGE_URL_PARAMETER;
		
		if( states.size() == 0 ) {
			return;
		}
		
		final Collection<Long> ids = states.stream().map(state -> state.getKey()).collect(Collectors.toList());
		
		final Collection<String> values = states.stream().map(state -> state.getValue()).collect(Collectors.toList());
		parameter.put(ID_PARAMETER_NAME,  StringUtils.collectionToCommaDelimitedString(ids));
		parameter.put(VALUE_PARAMETER_NAME, StringUtils.collectionToCommaDelimitedString(values));

		final ResponseEntity<String> res = webClientBuilder().build().put().uri(uri, parameter).exchange().block(timeout).toEntity(String.class).block(timeout);
		
		if (!StringUtils.hasText(res.getBody())) {
			throw newHttpStatusCodeException(HttpStatus.BAD_REQUEST, "Result expected.");
		}
		
		httpStatusGuard(res);
		
		Assert.isTrue(evaluate(res.getBody(), "/result/*").getLength() == states.size(), "Not all states processed.");
		
		
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

	private HttpStatusCodeException newHttpStatusCodeException(final HttpStatus status, final String result) {
		return new HttpStatusCodeException(status, result) {
			private static final long serialVersionUID = 1L;

		};
	}

	/*
	 * (non-Javadoc)
	 * @see de.mq.iot.state.support.StateRepository#findChannelIds(de.mq.iot.resource.ResourceIdentifier, java.lang.String)
	 */
	@Override
	public Collection<Long> findChannelIds(final ResourceIdentifier resourceIdentifier, final Collection<String> functions) {
		Assert.notEmpty(functions, "Functions are mandatory.");

		final ResponseEntity<byte[]> res = webClientBuilder().build().get().uri(resourceIdentifier.uri(), XmlApiParameters.FunctionList.parameters(resourceIdentifier)).exchange().block(timeout).toEntity(byte[].class).block(timeout);

		httpStatusGuard(res);

		final NodeList nodes = evaluate(res.getBody(), String.format("/functionList/function[%s]/channel/@ise_id", xpath(functions)));

		return IntStream.range(0, nodes.getLength()).mapToObj(i -> conversionService.convert(nodes.item(i).getFirstChild().getNodeValue(), Long.class)).collect(Collectors.toList());

	}
	
	final String xpath(final Collection<String> names) {
		final StringBuffer  stringBuffer = new StringBuffer();
		
		names.forEach(name -> {
			if( stringBuffer.length() > 0 ) {
				stringBuffer.append(" or ");
			}
			stringBuffer.append(String.format("@name='%s'", name));
			
		});
		
		return stringBuffer.substring(0);
		
	}

	/*
	 * (non-Javadoc)
	 * @see de.mq.iot.state.support.StateRepository#findCannelsRooms(de.mq.iot.resource.ResourceIdentifier)
	 */
	@Override
	public Map<Long, String> findCannelsRooms(final ResourceIdentifier resourceIdentifier) {

		final ResponseEntity<byte[]> res = webClientBuilder().build().get().uri(resourceIdentifier.uri(), XmlApiParameters.RoomList.parameters(resourceIdentifier)).exchange().block(timeout).toEntity(byte[].class).block(timeout);

		httpStatusGuard(res);

		final NodeList nodes = evaluate(res.getBody(), "/roomList/room/channel");

		return IntStream.range(0, nodes.getLength())
				.mapToObj(i -> new AbstractMap.SimpleImmutableEntry<>(conversionService.convert(nodes.item(i).getAttributes().getNamedItem(ID_ATTRIBUTE).getNodeValue(), Long.class), nodes.item(i).getParentNode().getAttributes().getNamedItem(NAME_ATTRIBUTE).getNodeValue()))
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue));

	}

	@Override
	public Collection<Map<String,String>> findDeviceStates(final ResourceIdentifier resourceIdentifier) {

		final ResponseEntity<byte[]> res = webClientBuilder().build().get().uri(resourceIdentifier.uri(), XmlApiParameters.StateList.parameters(resourceIdentifier)).exchange().block(timeout).toEntity(byte[].class).block(timeout);

		httpStatusGuard(res);

		final NodeList nodes = evaluate(res.getBody(), "/stateList/device/channel/datapoint[(@type='LEVEL')  and string-length(@value) > 0]");
		 IntStream.range(0, nodes.getLength()).mapToObj(i -> attributesToMap(nodes.item(i).getAttributes())).collect(Collectors.toList());
		return IntStream.range(0, nodes.getLength()).mapToObj(i -> {
			final Map<String,String> results = 	new HashMap<>();
					
			results.putAll(attributesToMap(nodes.item(i).getAttributes()));
		
			Arrays.asList(ID_ATTRIBUTE,NAME_ATTRIBUTE).forEach( key -> results.put(key, nodes.item(i).getParentNode().getAttributes().getNamedItem(key).getNodeValue()));
			
			
			
			
		return results; 
		
		}).collect(Collectors.toList());

	}
	
	@Override
	public double findVersion(final ResourceIdentifier resourceIdentifier) {
		
		
		
		final ResponseEntity<byte[]> res = webClientBuilder().build().get().uri(resourceIdentifier.uri(), XmlApiParameters.Version.parameters(resourceIdentifier)).exchange().block(timeout).toEntity(byte[].class).block(timeout);

		httpStatusGuard(res);
		final NodeList nodes = evaluate(res.getBody(), "/version");
			
		return DataAccessUtils.requiredSingleResult( IntStream.range(0, nodes.getLength()).mapToObj(i -> conversionService.convert(nodes.item(i).getTextContent(), Double.class)).collect(Collectors.toList()));
		
	}

	@Lookup
	abstract Builder webClientBuilder();

	@Lookup
	abstract XPath xpath();

}
