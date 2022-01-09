package root.javafx.CustomView;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;

import root.core.domain.UnitString;

public class UnitStringConverter<T, I> extends AbstractBeanField<T, I> {

	@Override
	protected Object convert(String value) throws CsvDataTypeMismatchException, CsvConstraintViolationException {
		return new UnitString(value);
	}

}
