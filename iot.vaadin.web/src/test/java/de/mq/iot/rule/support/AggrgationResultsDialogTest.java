package de.mq.iot.rule.support;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

class AggrgationResultsDialogTest {
	
	private final Dialog dialog = Mockito.mock(Dialog.class);
	
	private SimpleAggrgationResultsDialog aggrgationResultsDialog;
	
	private Map<String, Object> fields = new HashMap<>();
	
	@BeforeEach
	void setup() {
		aggrgationResultsDialog = new SimpleAggrgationResultsDialog(dialog);
		
		fields.putAll(Arrays.asList(aggrgationResultsDialog.getClass().getDeclaredFields()).stream().filter(field -> !Modifier.isStatic(field.getModifiers())).collect(Collectors.toMap(Field::getName, field -> ReflectionTestUtils.getField(aggrgationResultsDialog, field.getName()))));
		
	}
	
	@Test
	void init() {
		final HorizontalLayout resultsLayout = resultsLayout();
		assertNotNull(resultsLayout);
		
		final HorizontalLayout exceptionsLayout = exceptionsLayout();
		assertNotNull(exceptionsLayout);
		
		assertFalse(resultsLayout.isVisible());
		assertFalse(exceptionsLayout.isVisible());
	}

	private HorizontalLayout exceptionsLayout() {
		return  (HorizontalLayout) fields.get("exceptionsLayout");
	}

	private HorizontalLayout resultsLayout() {
		return (HorizontalLayout) fields.get("resultsLayout");
	}

}
