package za.co.hpsc.web.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import za.co.hpsc.web.enums.Division;
import za.co.hpsc.web.utils.ValueUtil;

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
