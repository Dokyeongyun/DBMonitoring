package root.javafx.CustomView;

import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

import javafx.scene.control.TextFormatter;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;

public class NumberTextFormatter extends TextFormatter<String> {

	public NumberTextFormatter(StringConverter<String> valueConverter, String defaultValue,
			UnaryOperator<Change> filter) {
		super(valueConverter, defaultValue, filter);
	}

	public NumberTextFormatter() {
		this(new DefaultStringConverter(), "", c -> Pattern.matches("[0-9]*", c.getText()) ? c : null);
	}
}
