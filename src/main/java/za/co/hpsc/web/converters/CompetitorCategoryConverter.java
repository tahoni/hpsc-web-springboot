package za.co.hpsc.web.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import za.co.hpsc.web.enums.CompetitorCategory;
import za.co.hpsc.web.utils.ValueUtil;

/**
 * JPA {@link AttributeConverter} that maps a {@link CompetitorCategory} to and from its
 * display name for persistence.
 *
 * <p>
 * On write, the category's name is stored. On read, the name is looked up against
 * {@link CompetitorCategory#fromName(String)}; if no match is found,
 * {@link CompetitorCategory#NONE} is used.
 * </p>
 *
 * @since 5.3.0
 */
@Converter(autoApply = true)
public class CompetitorCategoryConverter implements AttributeConverter<CompetitorCategory, String> {
    @Override
    public String convertToDatabaseColumn(CompetitorCategory attribute) {
        return ValueUtil.nullAsDefaultString(attribute, null);
    }


    @Override
    public CompetitorCategory convertToEntityAttribute(String dbData) {
        return CompetitorCategory.fromName(dbData).orElse(CompetitorCategory.NONE);
    }
}
