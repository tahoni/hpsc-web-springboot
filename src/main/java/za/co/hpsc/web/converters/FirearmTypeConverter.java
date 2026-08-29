package za.co.hpsc.web.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import za.co.hpsc.web.enums.FirearmType;

/**
 * JPA {@link AttributeConverter} that maps a {@link FirearmType} to and from its
 * primary display name for persistence.
 *
 * <p>
 * On write, the first of the firearm type's names is stored. On read, the name is
 * looked up against {@link FirearmType#fromName(String)}; if no match is found,
 * {@code null} is used.
 * </p>
 */
@Converter(autoApply = true)
public class FirearmTypeConverter implements AttributeConverter<FirearmType, String> {

    @Override
    public String convertToDatabaseColumn(FirearmType attribute) {
        return ((attribute != null) ? attribute.getNames().stream().findFirst().orElse(null) : null);
    }

    @Override
    public FirearmType convertToEntityAttribute(String dbData) {
        return FirearmType.fromName(dbData).orElse(null);
    }
}
