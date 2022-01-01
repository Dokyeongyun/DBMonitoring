package root.javafx.Model;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class TypeAndFieldName {
	private Class<?> clazz;
	private String fieldName;
}
