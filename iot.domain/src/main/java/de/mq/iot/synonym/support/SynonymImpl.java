package de.mq.iot.synonym.support;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.Assert;

import de.mq.iot.synonym.Synonym;

@Document(collection = "Synonym")
class SynonymImpl implements Synonym {
	
	@Id
	private final String key;

	private final Type type;

	private final String value;

	private final String description;

	@SuppressWarnings("unused")
	private SynonymImpl() {
		key = null;
		type = null;
		value = null;
		description = null;
	}

	SynonymImpl(final String key, final String value) {
		this(key, value, Type.Devive, null);
	}

	SynonymImpl(final String key, final String value, final Type type, final String description) {
		Assert.hasText(key, "Key is mandatory");
		Assert.hasText(value, "Value is mandatory");
		Assert.notNull(type, "Type is mandatory");
		this.key = key;
		this.type = type;
		this.value = value;
		this.description = description;
	}

	SynonymImpl(final String key, final String value, final Type type) {
		this(key, value, type, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mq.iot.synonym.Synonym#key()
	 */
	@Override
	public String key() {
		return key;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mq.iot.synonym.Synonym#value()
	 */
	@Override
	public String value() {
		return value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mq.iot.synonym.Synonym#type()
	 */
	@Override
	public Type type() {
		return type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mq.iot.synonym.Synonym#description()
	 */
	@Override
	public String description() {
		return description;
	}

	/*
	 * @Override(non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return key.hashCode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object other) {

		if (!(other instanceof Synonym)) {
			return super.equals(other);
		}
		return key.equals(((Synonym) other).key());

	}

}
