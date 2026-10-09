package za.co.hpsc.web.helpers;

import org.apache.commons.lang3.math.NumberUtils;
import org.jspecify.annotations.NonNull;
import za.co.hpsc.web.constants.IpscConstants;
import za.co.hpsc.web.domain.MatchCompetitor;
import za.co.hpsc.web.models.ipsc.matchcompetitor.request.MatchCompetitorRequest;

import java.util.*;

/**
 * Helper methods for reporting why a match competitor could not be built from a request.
 *
 * @since 14.0.0
 */
public final class MatchCompetitorHelpers {

    private MatchCompetitorHelpers() {
        // Helper class, not to be instantiated
    }

    /**
     * Builds an error message describing every required field that is still unset on a match competitor after the
     * request's fields were applied to it.
     *
     * <p>
     * The competitor, match, match club, competitor category, division, firearm type and power factor are checked in
     * that order. The match club is optional, so it is only reported when the request gave one that could not be
     * resolved. For each other one that is {@code null}, a message is added saying whether the request did not specify the
     * value or specified a value that could not be resolved.
     * </p>
     *
     * @param matchCompetitor the match competitor the request's fields were applied to.
     * @param request         the request the match competitor was built from, used to describe the missing values.
     * @return the messages for all missing required fields, separated by {@code "; "}, or an empty string if none
     * are missing.
     * @since 14.0.0
     */
    public static String getErrorMessagesForMissingRequiredFields(@NonNull MatchCompetitor matchCompetitor,
                                                                  @NonNull MatchCompetitorRequest request) {
        List<String> errorMessages = new ArrayList<>();

        if (matchCompetitor.getCompetitor() ==  null) {
            errorMessages.add(getErrorMessageForMissingCompetitor(request));
        }
        if (matchCompetitor.getMatch() == null) {
            errorMessages.add(getErrorMessageForMissingMatch(request));
        }
        // The match club is optional, so it is only reported when one was given but could not be resolved
        if ((matchCompetitor.getMatchClub() == null) && (request.getMatchClub() != null)
                && !request.getMatchClub().isBlank()) {
            errorMessages.add(getErrorMessageForMissingMatchClub(request));
        }
        if (matchCompetitor.getCompetitorCategory() == null) {
            errorMessages.add(getErrorMessageForMissingCompetitorCategory(request));
        }
        if (matchCompetitor.getDivision() == null) {
            errorMessages.add(getErrorMessageForMissingDivision(request));
        }
        if (matchCompetitor.getFirearmType() == null) {
            errorMessages.add(getErrorMessageForMissingFirearmType(request));
        }
        if (matchCompetitor.getPowerFactor() == null) {
            errorMessages.add(getErrorMessageForMissingPowerFactor(request));
        }

        if (errorMessages.isEmpty()) {
            return "";
        } else {
            return String.join("; ", errorMessages);
        }
    }

    /**
     * Builds the message for a match competitor with no competitor.
     *
     * @param request the request the match competitor was built from.
     * @return the competitor error message.
     * @since 14.0.0
     */
    private static String getErrorMessageForMissingCompetitor(MatchCompetitorRequest request) {
        List<String> errorMessages = new ArrayList<>();

        // Determine if the competitor number was supplied, and it was not blank or a generic competitor number
        boolean isCompetitorNumberSpecified = ((request.getCompetitorNumber() != null) &&
                (!request.getCompetitorNumber().isBlank()) &&
                (!IpscConstants.EXCLUDE_ICS_ALIAS.contains(
                        NumberUtils.toInt(request.getCompetitorNumber().trim(), 0))));
        boolean isCompetitorNameSupplied = ((request.getCompetitorName() != null) &&
                (!request.getCompetitorName().isBlank()));

        // No competitor specified
        if ((request.getCompetitorId() == null) && (!isCompetitorNameSupplied) && (!isCompetitorNumberSpecified)) {
            errorMessages.add("Competitor not specified");
        }

        // Competitor not found
        if (request.getCompetitorId() != null) {
            errorMessages.add("Competitor not found for ID " + request.getCompetitorId());
        }
        if (isCompetitorNameSupplied) {
            String normalisedCompetitorName = CompetitorHelpers.cleanCompetitorName(request.getCompetitorName());
            errorMessages.add("Competitor not found for name " + normalisedCompetitorName);
        }
        if (isCompetitorNumberSpecified) {
            errorMessages.add("Competitor not found for competitor number " + request.getCompetitorNumber());
        }
        return String.join(", ", errorMessages);
    }

    /**
     * Builds the message for a match competitor with no match.
     *
     * @param request the request the match competitor was built from.
     * @return the match error message.
     * @since 14.0.0
     */
    private static String getErrorMessageForMissingMatch(MatchCompetitorRequest request) {
        if (request.getMatchId() == null) {
            return "Match not specified";
        } else {
            return "Match not found for ID " + request.getMatchId();
        }
    }

    /**
     * Builds the message for a match club that was given but could not be resolved.
     *
     * @param request the request the match competitor was built from.
     * @return the club error message.
     * @since 14.0.0
     */
    private static String getErrorMessageForMissingMatchClub(MatchCompetitorRequest request) {
        return "Club not found for " + request.getMatchClub();
    }

    /**
     * Builds the message for a competitor category that was given but could not be resolved. A blank category
     * resolves to {@code CompetitorCategory.NONE}, so it is never reported here.
     *
     * @param request the request the match competitor was built from.
     * @return the competitor category error message.
     * @since 14.0.0
     */
    private static String getErrorMessageForMissingCompetitorCategory(MatchCompetitorRequest request) {
        return "Competitor category not found for " + request.getCompetitorCategory();
    }

    /**
     * Builds the message for a match competitor with no division.
     *
     * @param request the request the match competitor was built from.
     * @return the division error message.
     * @since 14.0.0
     */
    private static String getErrorMessageForMissingDivision(MatchCompetitorRequest request) {
        if ((request.getDivision() == null) || request.getDivision().isBlank()) {
            return "Division not specified";
        } else {
            return "Division not found for " + request.getDivision();
        }
    }

    /**
     * Builds the message for a match competitor with no firearm type.
     *
     * @param request the request the match competitor was built from.
     * @return the firearm type error message.
     * @since 14.0.0
     */
    private static String getErrorMessageForMissingFirearmType(MatchCompetitorRequest request) {
        if ((request.getFirearmType() == null) || request.getFirearmType().isBlank()) {
            return "Firearm type not specified";
        } else {
            return "Firearm type not found for " + request.getFirearmType();
        }
    }

    /**
     * Builds the message for a match competitor with no power factor.
     *
     * @param request the request the match competitor was built from.
     * @return the power factor error message.
     * @since 14.0.0
     */
    private static String getErrorMessageForMissingPowerFactor(MatchCompetitorRequest request) {
        if ((request.getPowerFactor() == null) || request.getPowerFactor().isBlank()) {
            return "Power factor not specified";
        } else {
            return "Power factor not found for " + request.getPowerFactor();
        }
    }
}
