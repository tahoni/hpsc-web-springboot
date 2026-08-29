package za.co.hpsc.web.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import za.co.hpsc.web.enums.MatchCategory;
import za.co.hpsc.web.utils.ValueUtil;

/**
 * JPA {@link AttributeConverter} that maps a {@link MatchCategory} to and from its
 * display name for persistence.
 *
 * <p>
 * On write, the match category's name is stored. On read, the name is looked up against
 * {@link MatchCategory#fromName(String)}; if no match is found, {@code null} is used.
 * </p>
 */
@Converter(autoApply = true)
public class MatchCategoryConverter implements AttributeConverter<MatchCategory, String> {
    @Override
    public String convertToDatabaseColumn(MatchCategory attribute) {
        return ValueUtil.nullAsDefaultString(attribute, null);
    }

    @Override
    public MatchCategory convertToEntityAttribute(String dbData) {
        return MatchCategory.fromName(dbData).orElse(null);
    }
}
