package za.co.hpsc.web.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import za.co.hpsc.web.enums.MatchCategory;

@Converter(autoApply = true)
public class MatchCategoryConverter implements AttributeConverter<MatchCategory, String> {
    @Override
    public String convertToDatabaseColumn(MatchCategory matchCategory) {
        return ((matchCategory != null) ? matchCategory.getName() : null);
    }

    @Override
    public MatchCategory convertToEntityAttribute(String s) {
        return MatchCategory.getByName(s).orElse(null);
    }
}
