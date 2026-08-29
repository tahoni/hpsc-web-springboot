package za.co.hpsc.web.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import za.co.hpsc.web.enums.Division;
import za.co.hpsc.web.utils.ValueUtil;

/**
 * JPA {@link AttributeConverter} that maps a {@link Division} to and from its display
 * name for persistence.
 *
 * <p>
 * On write, the division's name is stored. On read, the name is looked up against
 * {@link Division#fromName(String)}; if no match is found, {@code null} is used.
 * </p>
 */
@Converter(autoApply = true)
public class DivisionConverter implements AttributeConverter<Division, String> {
    @Override
    public String convertToDatabaseColumn(Division attribute) {
        return ValueUtil.nullAsDefaultString(attribute, null);
    }

    @Override
    public Division convertToEntityAttribute(String dbData) {
        return Division.fromName(dbData).orElse(null);
    }
}
