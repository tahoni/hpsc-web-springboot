package za.co.hpsc.web.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import za.co.hpsc.web.enums.CompetitorCategory;

@Converter(autoApply = true)
public class CompwtitorCategoryConverter implements AttributeConverter<CompetitorCategory, String> {
    @Override
    public String convertToDatabaseColumn(CompetitorCategory competitorCategory) {
        return ((competitorCategory != null) ? competitorCategory.getName() : null);
    }

    @Override
    public CompetitorCategory convertToEntityAttribute(String s) {
        return CompetitorCategory.getByName(s).orElseGet(() -> CompetitorCategory.NONE);
    }
}
