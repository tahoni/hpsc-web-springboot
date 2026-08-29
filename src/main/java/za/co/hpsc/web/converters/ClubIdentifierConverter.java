package za.co.hpsc.web.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import za.co.hpsc.web.enums.ClubIdentifier;

/**
 * JPA {@link AttributeConverter} that maps a {@link ClubIdentifier} to and from its
 * abbreviation for persistence.
 *
 * <p>
 * On write, the club's abbreviation is stored. On read, the abbreviation is looked up
 * against {@link ClubIdentifier#fromAbbreviation(String)}; if no match is found,
 * {@code null} is used.
 * </p>
 */
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
