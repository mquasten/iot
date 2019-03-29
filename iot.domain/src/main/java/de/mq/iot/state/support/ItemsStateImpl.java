package de.mq.iot.state.support;



import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;


class ItemsStateImpl extends AbstractState<Integer> implements ItemList {

	ItemsStateImpl(long id, String name, LocalDateTime lastupdate) {
		super(id, name, lastupdate);
		assign(value -> items.containsKey(value));
	}

	private int value = 0;
	private final Map<Integer, String> items = new HashMap<>();

	@Override
	public final Integer value() {
		return value;
	}

	@Override
	public final void assign(final Integer value) {
		this.value = value != null ? value : 0;
		valueInListGuard(this.value);

	}
	
	@Override
	public final void assign(final String value) {
		this.value=items.entrySet().stream().filter(entry -> entry.getValue().equalsIgnoreCase(value)).map(Entry::getKey).findFirst().orElseThrow(() -> new IllegalArgumentException("Value is not in valueList."));
	}
	
	@Override
	public final String stringValue() {
		valueInListGuard(value);
		return items.get(value);
		
	}

	private void valueInListGuard(final Integer value) {
		if (!validate(value)) {
			throw new IllegalArgumentException("Value is not in valueList.");
		}
	}

	@Override
	public final Collection<Entry<Integer, String>> items() {
		return Collections.unmodifiableList(items.entrySet().stream().sorted((e1, e2) -> (int) Math.signum(e1.getKey() - e2.getKey())).collect(Collectors.toList()));
	}

}
