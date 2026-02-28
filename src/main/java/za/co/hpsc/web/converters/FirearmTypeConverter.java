package za.co.hpsc.web.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import za.co.hpsc.web.enums.FirearmType;

@Converter(autoApply = true)
public class FirearmTypeConverter implements AttributeConverter<FirearmType, String> {

    @Override
    public String convertToDatabaseColumn(FirearmType firearmType) {
        return ((firearmType != null) ? firearmType.getNames().stream().findFirst().orElse(null) : null);
    }

    @Override
    public FirearmType convertToEntityAttribute(String s) {
        return FirearmType.getByName(s).orElse(null);
    }
}
