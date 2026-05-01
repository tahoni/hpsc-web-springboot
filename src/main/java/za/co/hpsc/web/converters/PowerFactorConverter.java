package za.co.hpsc.web.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import za.co.hpsc.web.enums.PowerFactor;
import za.co.hpsc.web.utils.ValueUtil;

@Converter(autoApply = true)
public class PowerFactorConverter implements AttributeConverter<PowerFactor, String> {
    @Override
    public String convertToDatabaseColumn(PowerFactor powerFactor) {
        return ValueUtil.nullAsDefaultString(powerFactor, null);
    }

    @Override
    public PowerFactor convertToEntityAttribute(String s) {
        return PowerFactor.getByName(s).orElse(null);
    }
}
