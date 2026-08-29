package za.co.hpsc.web.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import za.co.hpsc.web.enums.Gender;
import za.co.hpsc.web.utils.ValueUtil;

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
