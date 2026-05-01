package za.co.hpsc.web.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import za.co.hpsc.web.enums.MatchCategory;
import za.co.hpsc.web.utils.ValueUtil;

@Converter(autoApply = true)
public class MatchCategoryConverter implements AttributeConverter<MatchCategory, String> {
    @Override
    public String convertToDatabaseColumn(MatchCategory matchCategory) {
        return ValueUtil.nullAsDefaultString(matchCategory, null);
    }

    @Override
    public MatchCategory convertToEntityAttribute(String s) {
        return MatchCategory.getByName(s).orElse(null);
    }
}
