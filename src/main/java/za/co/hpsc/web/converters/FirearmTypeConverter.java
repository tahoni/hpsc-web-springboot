package za.co.hpsc.web.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import za.co.hpsc.web.enums.FirearmType;

@Converter(autoApply = true)
public class FirearmTypeConverter implements AttributeConverter<FirearmType, String> {

    @Override
    public String convertToDatabaseColumn(FirearmType attribute) {
        return ((attribute != null) ? attribute.getNames().stream().findFirst().orElse(null) : null);
    }

    @Override
    public FirearmType convertToEntityAttribute(String dbData) {
        return FirearmType.getByName(dbData).orElse(null);
    }
}
