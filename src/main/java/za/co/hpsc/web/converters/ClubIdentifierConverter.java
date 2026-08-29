package za.co.hpsc.web.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import za.co.hpsc.web.enums.ClubIdentifier;

@Converter(autoApply = true)
public class ClubIdentifierConverter implements AttributeConverter<ClubIdentifier, String> {
    @Override
    public String convertToDatabaseColumn(ClubIdentifier attribute) {
        return ((attribute != null) ? attribute.getAbbreviation() : null);
    }

    @Override
    public ClubIdentifier convertToEntityAttribute(String dbData) {
        return ClubIdentifier.fromAbbreviation(dbData).orElse(null);
    }
}
