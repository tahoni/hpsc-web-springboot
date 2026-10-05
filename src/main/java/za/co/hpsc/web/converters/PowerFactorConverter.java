package za.co.hpsc.web.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import za.co.hpsc.web.enums.PowerFactor;
import za.co.hpsc.web.utils.ValueUtil;

/**
 * JPA {@link AttributeConverter} that maps a {@link PowerFactor} to and from its
 * display name for persistence.
 *
 * <p>
 * On write, the power factor's name is stored. On read, the name is looked up against
 * {@link PowerFactor#fromName(String)}; if no match is found, {@code null} is used.
 * </p>
 *
 * @since 5.3.0
 */
@Converter(autoApply = true)
public class PowerFactorConverter implements AttributeConverter<PowerFactor, String> {
    @Override
    public String convertToDatabaseColumn(PowerFactor attribute) {
        return ValueUtil.nullAsDefaultString(attribute, null);
    }

    @Override
    public PowerFactor convertToEntityAttribute(String dbData) {
        return PowerFactor.fromName(dbData).orElse(null);
    }
}
