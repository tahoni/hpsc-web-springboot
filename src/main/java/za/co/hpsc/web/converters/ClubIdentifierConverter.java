package za.co.hpsc.web.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import za.co.hpsc.web.enums.ClubIdentifier;

@Converter(autoApply = true)
public class ClubIdentifierConverter implements AttributeConverter<ClubIdentifier, String> {
    @Override
    public String convertToDatabaseColumn(ClubIdentifier clubIdentifier) {
        return ((clubIdentifier != null) ? clubIdentifier.getName() : null);
    }

    @Override
    public ClubIdentifier convertToEntityAttribute(String s) {
        return ClubIdentifier.getByName(s).orElse(null);
    }
}
