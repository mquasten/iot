package de.mq.iot.model;

import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.MessageSource;

import com.vaadin.flow.component.HasText;

import de.mq.iot.model.LocalizeView;

public class LocalizeViewTest {

	private static final String TRANSLATED_NAME = "Translated name";

	private final MessageSource messageSource = Mockito.mock(MessageSource.class);

	@BeforeEach
	void setup() {
		Mockito.doReturn(TRANSLATED_NAME).when(messageSource).getMessage("viewmock_name", null, "???", Locale.GERMAN);
		Mockito.doReturn(TRANSLATED_NAME).when(messageSource).getMessage("viewmock2_name", null, "???", Locale.GERMAN);
	}

	@Test
	public final void localizeView() {
		final ViewMock view = new ViewMock();
		view.localize(messageSource, Locale.GERMAN);
		Mockito.verify(view.getText()).setText(TRANSLATED_NAME);
	}

	@Test
	public final void localizeViewSimpleName() {
		final ViewMock2 view = new ViewMock2();
		view.localize(messageSource, Locale.GERMAN);
		Mockito.verify(view.getText()).setText(TRANSLATED_NAME);
	}

}

@I18NKey(value = "viewmock_")
class ViewMock implements LocalizeView {
	@I18NKey("name")
	HasText text = Mockito.mock(HasText.class);

	public HasText getText() {
		return text;
	}
}

class ViewMock2 implements LocalizeView {
	@I18NKey("name")
	HasText text = Mockito.mock(HasText.class);

	public HasText getText() {
		return text;
	}
}