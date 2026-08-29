package za.co.hpsc.web.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import za.co.hpsc.web.enums.CompetitorCategory;
import za.co.hpsc.web.utils.ValueUtil;

@Converter(autoApply = true)
public class CompetitorCategoryConverter implements AttributeConverter<CompetitorCategory, String> {
    @Override
    public String convertToDatabaseColumn(CompetitorCategory attribute) {
        return ValueUtil.nullAsDefaultString(attribute, null);
    }


    @Override
    public CompetitorCategory convertToEntityAttribute(String dbData) {
        return CompetitorCategory.getByName(dbData).orElse(CompetitorCategory.NONE);
    }
}
