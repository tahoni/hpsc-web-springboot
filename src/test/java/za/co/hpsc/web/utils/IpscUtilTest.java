package za.co.hpsc.web.utils;

import org.junit.jupiter.api.Test;
import za.co.hpsc.web.constants.IpscConstants;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class IpscUtilTest {

    @Test
    void returnsIntegerWhenAliasIsNumericAndNotExcluded() {
        Integer result = IpscUtil.getValidSapsaNumber("12345");

        assertEquals(12345, result);
    }

    @Test
    void returnsIntegerWhenAliasHasLeadingZeros() {
        Integer result = IpscUtil.getValidSapsaNumber("00042");

        assertEquals(42, result);
    }

    @Test
    void returnsNullWhenAliasIsNull() {
        assertNull(IpscUtil.getValidSapsaNumber(null));
    }

    @Test
    void returnsNullWhenAliasIsBlank() {
        assertNull(IpscUtil.getValidSapsaNumber(""));
    }

    @Test
    void returnsNullWhenAliasContainsOnlyWhitespace() {
        assertNull(IpscUtil.getValidSapsaNumber("   "));
    }

    @Test
    void returnsNullWhenAliasIsNotNumeric() {
        assertNull(IpscUtil.getValidSapsaNumber("AB12"));
    }

    @Test
    void returnsNullWhenAliasIsInExcludeList() {
        for (String excludedAlias : IpscConstants.EXCLUDE_ICS_ALIAS) {
            Integer result = IpscUtil.getValidSapsaNumber(excludedAlias);

            assertNull(result);
        }
    }

    @Test
    void returnsNullWhenAliasIsCreatableButNotIntegerString() {
        assertNull(IpscUtil.getValidSapsaNumber("1e3"));
    }

    @Test
    void returnsNullWhenAliasIsOutsideIntegerRange() {
        assertNull(IpscUtil.getValidSapsaNumber("999999999999"));
    }
}

