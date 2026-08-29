package za.co.hpsc.web.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import za.co.hpsc.web.enums.Gender;
import za.co.hpsc.web.utils.ValueUtil;

/**
 * JPA {@link AttributeConverter} that maps a {@link Gender} to and from its display
 * name for persistence.
 *
 * <p>
 * On write, the gender's name is stored. On read, the name is looked up against
 * {@link Gender#fromName(String)}; if no match is found, {@code null} is used.
 * </p>
 *
 * @since 8.0.0
 */
@Converter(autoApply = true)
public class GenderConverter implements AttributeConverter<Gender, String> {
    @Override
    public String convertToDatabaseColumn(Gender attribute) {
        return ValueUtil.nullAsDefaultString(attribute, null);
    }

    @Override
    public Gender convertToEntityAttribute(String dbData) {
        return Gender.fromName(dbData).orElse(null);
    }
}
