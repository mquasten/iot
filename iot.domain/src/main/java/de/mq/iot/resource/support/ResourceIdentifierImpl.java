package de.mq.iot.resource.support;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import de.mq.iot.resource.ResourceIdentifier;

@Document
class ResourceIdentifierImpl implements ResourceIdentifier {

	@Id
	private String id;

	private String uri;

	private final Map<String, String> parameters = new HashMap<>();

	@SuppressWarnings("unused")
	private ResourceIdentifierImpl() {

	}

	ResourceIdentifierImpl(final ResourceType resourceType, final String uri) {
		this.id = resourceType.name();
		this.uri = uri;
	}

	@Override
	public ResourceType id() {
		return this.id == null ? null : ResourceIdentifier.ResourceType.valueOf(this.id);

	}

	@Override
	public String uri() {
		return uri;
	}

	@Override
	public Map<String, String> parameters() {
		return Collections.unmodifiableMap(parameters);
	}

	@Override
	public void assign(final Map<String, String> parameters) {
		this.parameters.clear();
		this.parameters.putAll(parameters);

	}

}
