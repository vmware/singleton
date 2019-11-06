/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.l2.text;

import java.text.FieldPosition;
import java.util.HashMap;
import java.util.Locale;
import java.util.Locale.Category;
import java.util.Map;

import com.vmware.vipclient.i18n.I18nFactory;
import com.vmware.vipclient.i18n.base.instances.NumberFormatting;
import com.vmware.vipclient.i18n.l2.plural.parser.PluralRules;
import com.vmware.vipclient.i18n.l2.plural.parser.PluralRules.FixedDecimal;
import com.vmware.vipclient.i18n.l2.plural.parser.PluralRules.PluralType;

public class PluralFormat {
    /**
     * The locale used for standard number formatting and getting the predefined
     * plural rules (if they were not defined explicitely).
     * 
     * @serial
     */
    private Locale                   locale            = null;

    /**
     * The plural rules used for plural selection.
     * 
     * @serial
     */
    private PluralRules              pluralRules       = null;

    /**
     * The applied pattern string.
     * 
     * @serial
     */
    private String                   pattern           = null;

    /**
     * The MessagePattern which contains the parsed structure of the pattern string.
     */
    transient private MessagePattern msgPattern;

    /**
     * The offset to subtract before invoking plural rules.
     */
    transient private double         offset            = 0;

    private String                   dataKey_prevIndex = "prevIndex";
    private String                   dataKey_result    = "result";

    public PluralFormat(String pattern) {
        init(null, PluralType.CARDINAL, Locale.getDefault(Category.FORMAT));
        applyPattern(pattern);
    }

    public PluralFormat(Locale locale, String pattern) {
        init(null, PluralType.CARDINAL, locale);
        applyPattern(pattern);
    }

    private void init(PluralRules rules, PluralType type, Locale locale) {
        this.locale = locale;
        pluralRules = (rules == null) ? PluralRules.forLocale(locale, type)
                : rules;
        resetPattern();
    }

    private void resetPattern() {
        pattern = null;
        if (msgPattern != null) {
            msgPattern.clear();
        }
        offset = 0;
    }

    /**
     * Sets the pattern used by this plural format.
     * The method parses the pattern and creates a map of format strings
     * for the plural rules.
     * Patterns and their interpretation are specified in the class description.
     *
     * @param pattern
     *            the pattern for this plural format.
     * @throws IllegalArgumentException
     *             if the pattern is invalid.
     * @stable ICU 3.8
     */
    public void applyPattern(String pattern) {
        this.pattern = pattern;
        if (msgPattern == null) {
            msgPattern = new MessagePattern();
        }
        try {
            msgPattern.parsePluralStyle(pattern);
            offset = msgPattern.getPluralOffset(0);
        } catch (RuntimeException e) {
            resetPattern();
            throw e;
        }
    }

    public final String format(double number) {
        return format(number, number);
    }

    /**
     * Formats a plural message for a given number and appends the formatted
     * message to the given <code>StringBuffer</code>.
     * 
     * @param number
     *            a number object (instance of <code>Number</code> for which
     *            the plural message should be formatted. If no pattern has been
     *            applied to this <code>PluralFormat</code> object yet, the
     *            formatted number will be returned.
     *            Note: If this object is not an instance of <code>Number</code>,
     *            the <code>toAppendTo</code> will not be modified.
     * @param toAppendTo
     *            the formatted message will be appended to this
     *            <code>StringBuffer</code>.
     * @param pos
     *            will be ignored by this method.
     * @return the string buffer passed in as toAppendTo, with formatted text
     *         appended.
     * @throws IllegalArgumentException
     *             if number is not an instance of Number
     * @stable ICU 3.8
     */
    public StringBuilder format(Object number, StringBuilder toAppendTo,
            FieldPosition pos) {
        if (!(number instanceof Number)) {
            throw new IllegalArgumentException("'" + number + "' is not a Number");
        }
        Number numberObject = (Number) number;
        toAppendTo.append(format(numberObject, numberObject.doubleValue()));
        return toAppendTo;
    }

    private String format(Number numberObject, double number) {
        I18nFactory factory = I18nFactory.getInstance();
        NumberFormatting p = (NumberFormatting) factory.getFormattingInstance(NumberFormatting.class);
        // If no pattern was applied, return the formatted number.
        if (msgPattern == null || msgPattern.countParts() == 0) {
            // return new NumberFormatting().formatNumber(numberObject, locale.toLanguageTag());
            return p.formatNumber(numberObject, locale);
        }

        // Get the appropriate sub-message.
        // Select it based on the formatted number-offset.
        double numberMinusOffset = number - offset;
        String numberString;
        if (offset == 0) {
            // numberString = new NumberFormatting().formatNumber(numberObject, locale.toLanguageTag());
            numberString = p.formatNumber(numberObject, locale);
        } else {
            numberString = p.formatNumber(numberMinusOffset, locale);
        }
        FixedDecimal dec;
        dec = new FixedDecimal(numberMinusOffset);
        int partIndex = findSubMessage(msgPattern, 0, pluralRulesWrapper, dec, number);
        // Replace syntactic # signs in the top level of this sub-message
        // (not in nested arguments) with the formatted number-offset.
        return formatSubMessage(partIndex, numberString);
    }

    private String formatSubMessage(int partIndex, String numberString) {
        StringBuilder result = null;
        int prevIndex = msgPattern.getPart(partIndex).getLimit();
        Map<String, Object> map = new HashMap<String, Object>();
        for (;;) {
            MessagePattern.Part part = msgPattern.getPart(++partIndex);
            MessagePattern.Part.Type type = part.getType();
            int index = part.getIndex();
            if (type == MessagePattern.Part.Type.MSG_LIMIT) {
                return handleMsgLimit(result, prevIndex, index);
            } else if (type == MessagePattern.Part.Type.REPLACE_NUMBER ||
            // JDK compatibility mode: Remove SKIP_SYNTAX.
                    (type == MessagePattern.Part.Type.SKIP_SYNTAX && msgPattern.jdkAposMode())) {
                map = handleReplaceNumberOrSkipSyntax(result, prevIndex, index, part, type, numberString);
            } else if (type == MessagePattern.Part.Type.ARG_START) {
                map = handleArg(result, prevIndex, partIndex, index);
            }
            prevIndex = (int) map.get(dataKey_prevIndex);
            result = (StringBuilder) map.get(dataKey_result);
        }
    }

    private String handleMsgLimit(StringBuilder result, int prevIndex, int index) {
        if (result == null) {
            return pattern.substring(prevIndex, index);
        } else {
            return result.append(pattern, prevIndex, index).toString();
        }
    }

    private Map<String, Object> handleReplaceNumberOrSkipSyntax(StringBuilder result, int prevIndex, int index,
            MessagePattern.Part part, MessagePattern.Part.Type type, String numberString) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (result == null) {
            result = new StringBuilder();
        }
        result.append(pattern, prevIndex, index);
        if (type == MessagePattern.Part.Type.REPLACE_NUMBER) {
            result.append(numberString);
        }
        prevIndex = part.getLimit();
        map.put(dataKey_prevIndex, prevIndex);
        map.put(dataKey_result, result);
        return map;
    }

    private Map<String, Object> handleArg(StringBuilder result, int prevIndex, int partIndex, int index) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (result == null) {
            result = new StringBuilder();
        }
        result.append(pattern, prevIndex, index);
        prevIndex = index;
        partIndex = msgPattern.getLimitPartIndex(partIndex);
        index = msgPattern.getPart(partIndex).getLimit();
        MessagePattern.appendReducedApostrophes(pattern, prevIndex, index, result);
        prevIndex = index;
        map.put(dataKey_prevIndex, prevIndex);
        map.put(dataKey_result, result);
        return map;
    }

    /**
     * Finds the PluralFormat sub-message for the given number, or the "other" sub-message.
     * 
     * @param pattern
     *            A MessagePattern.
     * @param partIndex
     *            the index of the first PluralFormat argument style part.
     * @param selector
     *            the PluralSelector for mapping the number (minus offset) to a keyword.
     * @param context
     *            worker object for the selector.
     * @param number
     *            a number to be matched to one of the PluralFormat argument's explicit values,
     *            or mapped via the PluralSelector.
     * @return the sub-message start part index.
     */
    /* package */ static int findSubMessage(
            MessagePattern pattern, int partIndex,
            PluralSelector selector, Object context, double number) {
        int count = pattern.countParts();
        double offset;
        MessagePattern.Part part = pattern.getPart(partIndex);
        if (part.getType().hasNumericValue()) {
            offset = pattern.getNumericValue(part);
            ++partIndex;
        } else {
            offset = 0;
        }
        // The keyword is null until we need to match against a non-explicit, not-"other" value.
        // Then we get the keyword from the selector.
        // (In other words, we never call the selector if we match against an explicit value,
        // or if the only non-explicit keyword is "other".)
        String keyword = null;
        // When we find a match, we set msgStart>0 and also set this boolean to true
        // to avoid matching the keyword again (duplicates are allowed)
        // while we continue to look for an explicit-value match.
        boolean haveKeywordMatch = false;
        // msgStart is 0 until we find any appropriate sub-message.
        // We remember the first "other" sub-message if we have not seen any
        // appropriate sub-message before.
        // We remember the first matching-keyword sub-message if we have not seen
        // one of those before.
        // (The parser allows [does not check for] duplicate keywords.
        // We just have to make sure to take the first one.)
        // We avoid matching the keyword twice by also setting haveKeywordMatch=true
        // at the first keyword match.
        // We keep going until we find an explicit-value match or reach the end of the plural style.
        int msgStart = 0;
        // Iterate over (ARG_SELECTOR [ARG_INT|ARG_DOUBLE] message) tuples
        // until ARG_LIMIT or end of plural-only pattern.
        do {
            part = pattern.getPart(partIndex++);
            MessagePattern.Part.Type type = part.getType();
            if (type == MessagePattern.Part.Type.ARG_LIMIT) {
                break;
            }
            assert type == MessagePattern.Part.Type.ARG_SELECTOR;
            // part is an ARG_SELECTOR followed by an optional explicit value, and then a message
            Map<String, Object> map = handleArgSelector(pattern, partIndex, keyword, selector, context, number, offset);
            keyword = (String) map.get("keyword");
            haveKeywordMatch = (boolean) map.get("haveKeywordMatch");
            partIndex = (int) map.get("partIndex");
            if (haveKeywordMatch)
                return partIndex;
            partIndex = pattern.getLimitPartIndex(partIndex);
        } while (++partIndex < count);
        return msgStart;
    }

    private static Map<String, Object> handleArgSelector(MessagePattern pattern, int partIndex, String keyword,
            PluralSelector selector, Object context, double number, double offset) {
        Map<String, Object> map = new HashMap<String, Object>();
        boolean haveKeywordMatch = false;
        MessagePattern.Part part = pattern.getPart(partIndex - 1);
        if (pattern.getPartType(partIndex).hasNumericValue()) {
            // explicit value like "=2"
            part = pattern.getPart(partIndex++);
            if (number == pattern.getNumericValue(part)) {
                // matches explicit value
                haveKeywordMatch = true;
            }
        } else {
            // plural keyword like "few" or "other"
            // Compare "other" first and call the selector if this is not "other".
            if (keyword == null)
                keyword = selector.select(context, number - offset);
            if (pattern.partSubstringMatches(part, keyword)) {
                // keyword matches
                haveKeywordMatch = true;
            }
        }
        map.put("keyword", keyword);
        map.put("haveKeywordMatch", haveKeywordMatch);
        map.put("partIndex", partIndex);
        return map;
    }

    /**
     * Interface for selecting PluralFormat keywords for numbers.
     * The PluralRules class was intended to implement this interface,
     * but there is no public API that uses a PluralSelector,
     * only MessageFormat and PluralFormat have PluralSelector implementations.
     * Therefore, PluralRules is not marked to implement this non-public interface,
     * to avoid confusing users.
     * 
     * @internal
     */
    /* package */ interface PluralSelector {
        /**
         * Given a number, returns the appropriate PluralFormat keyword.
         *
         * @param context
         *            worker object for the selector.
         * @param number
         *            The number to be plural-formatted.
         * @return The selected PluralFormat keyword.
         */
        public String select(Object context, double number);
    }

    // See PluralSelector:
    // We could avoid this adapter class if we made PluralSelector public
    // (or at least publicly visible) and had PluralRules implement PluralSelector.
    private final class PluralSelectorAdapter implements PluralSelector {
        public String select(Object context, double number) {
            FixedDecimal dec = (FixedDecimal) context;
            double num = dec.isNegative ? -number : number;
            if (dec.source != num) {
                throw new IllegalArgumentException();
            }
            return pluralRules.select(dec);
        }
    }

    transient private PluralSelectorAdapter pluralRulesWrapper = new PluralSelectorAdapter();
}
