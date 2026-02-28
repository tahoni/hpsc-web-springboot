package za.co.hpsc.web.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import za.co.hpsc.web.enums.Division;

@Converter(autoApply = true)
public class DivisionConvweter implements AttributeConverter<Division, String> {
    @Override
    public String convertToDatabaseColumn(Division division) {
        return ((division != null) ? division.getName() : null);
    }

    @Override
    public Division convertToEntityAttribute(String s) {
        return Division.getByName(s).orElse(null);
    }
}
