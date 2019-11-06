/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.l2.text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.vmware.vipclient.i18n.exceptions.Level2Exception;
import com.vmware.vipclient.i18n.exceptions.Level2ExceptionConstants;
import com.vmware.vipclient.i18n.util.PatternProps;

public class MessagePattern {
    public enum ApostropheMode {
        /**
         * A literal apostrophe is represented by
         * either a single or a double apostrophe pattern character.
         * Within a MessageFormat pattern, a single apostrophe only starts quoted literal text
         * if it immediately precedes a curly brace {},
         * or a pipe symbol | if inside a choice format,
         * or a pound symbol # if inside a plural format.
         * <p>
         * This is the default behavior starting with ICU 4.8.
         * 
         * @stable ICU 4.8
         */
        DOUBLE_OPTIONAL,
        /**
         * A literal apostrophe must be represented by
         * a double apostrophe pattern character.
         * A single apostrophe always starts quoted literal text.
         * <p>
         * This is the behavior of ICU 4.6 and earlier, and of {@link java.text.MessageFormat}.
         * 
         * @stable ICU 4.8
         */
        DOUBLE_REQUIRED
    }

    private ApostropheMode              aposMode;
    private String                      msg;
    private ArrayList<Part>             parts           = new ArrayList<Part>();
    private ArrayList<Double>           numericValues;
    private boolean                     hasArgNames;
    private boolean                     hasArgNumbers;
    private boolean                     needsAutoQuoting;
    private volatile boolean            frozen;
    private final String                dataKey_index   = "index";

    private static final ApostropheMode defaultAposMode = ApostropheMode.valueOf("DOUBLE_OPTIONAL");// ICUConfig.get("com.ibm.icu.text.MessagePattern.ApostropheMode",
                                                                                                    // "DOUBLE_OPTIONAL")

    private static final ArgType[]      argTypes        = ArgType.values();

    public MessagePattern() {
        aposMode = defaultAposMode;
    }

    public MessagePattern(String pattern) {
        parse(pattern);
    }

    /**
     * Constructs an empty MessagePattern.
     * 
     * @param mode
     *            Explicit ApostropheMode.
     * @stable ICU 4.8
     */
    public MessagePattern(ApostropheMode mode) {
        aposMode = mode;
    }

    public MessagePattern parse(String pattern) {
        preParse(pattern);
        parseMessage(0, 0, 0, ArgType.NONE);
        postParse();
        return this;
    }

    /**
     * Parses a PluralFormat pattern string.
     * 
     * @param pattern
     *            a PluralFormat pattern string
     * @return this
     * @throws IllegalArgumentException
     *             for syntax errors in the pattern string
     * @throws IndexOutOfBoundsException
     *             if certain limits are exceeded
     *             (e.g., argument number too high, argument name too long, etc.)
     * @throws NumberFormatException
     *             if a number could not be parsed
     * @stable ICU 4.8
     */
    public MessagePattern parsePluralStyle(String pattern) {
        preParse(pattern);
        parsePluralOrSelectStyle(ArgType.PLURAL, 0, 0);
        postParse();
        return this;
    }

    private void preParse(String pattern) {
        if (isFrozen()) {
            throw new UnsupportedOperationException(
                    "Attempt to parse(" + prefix(pattern) + ") on frozen MessagePattern instance.");
        }
        msg = pattern;
        hasArgNames = hasArgNumbers = false;
        needsAutoQuoting = false;
        parts.clear();
        if (numericValues != null) {
            numericValues.clear();
        }
    }

    private void postParse() {
        // Nothing to be done currently.
    }

    private int parseMessage(int index, int msgStartLength, int nestingLevel, ArgType parentType) {
        if (nestingLevel > Part.MAX_VALUE) {
            throw new IndexOutOfBoundsException();
        }
        int msgStart = parts.size();
        addPart(Part.Type.MSG_START, index, msgStartLength, nestingLevel);
        index += msgStartLength;
        while (index < msg.length()) {
            char c = msg.charAt(index++);
            if (c == '\'') {
                index = parseApostrophe(index, parentType);
            } else if (parentType.hasPluralStyle() && c == '#') {
                // The unquoted # in a plural message fragment will be replaced
                // with the (number-offset).
                addPart(Part.Type.REPLACE_NUMBER, index - 1, 1, 0);
            } else if (c == '{') {
                index = parseArg(index - 1, 1, nestingLevel);
            } else if ((nestingLevel > 0 && c == '}') || (parentType == ArgType.CHOICE && c == '|')) {
                return getSubmessageLimitIndex(parentType, c, msgStart, index, nestingLevel);
            } // else: c is part of literal text
        }
        if (nestingLevel > 0 && !inTopLevelChoiceMessage(nestingLevel, parentType)) {
            throw new IllegalArgumentException(
                    Level2ExceptionConstants.UNMATCHEDBRACES + prefix());
        }
        addLimitPart(msgStart, Part.Type.MSG_LIMIT, index, 0, nestingLevel);
        return index;
    }

    private int parseApostrophe(int index, ArgType parentType) {
        if (index == msg.length()) {
            // The apostrophe is the last character in the pattern.
            // Add a Part for auto-quoting.
            addPart(Part.Type.INSERT_CHAR, index, 0, '\''); // value=char to be inserted
            needsAutoQuoting = true;
        } else {
            char c = msg.charAt(index);
            if (c == '\'') {
                // double apostrophe, skip the second one
                addPart(Part.Type.SKIP_SYNTAX, index++, 1, 0);
            } else if (aposMode == ApostropheMode.DOUBLE_REQUIRED ||
                    c == '{' || c == '}' ||
                    (parentType == ArgType.CHOICE && c == '|') ||
                    (parentType.hasPluralStyle() && c == '#')) {
                // Fix bug 2340341: When argument in message is quoted by single quote, it will not replaced by real
                // value
                int endIndex = msg.indexOf('\'', index + 1);
                if (c == '{' && msg.substring(index, endIndex).matches("^\\s*\\{\\s*\\d+\\s*\\}\\s*$")) {
                } else {
                    // skip the quote-starting apostrophe
                    addPart(Part.Type.SKIP_SYNTAX, index - 1, 1, 0);
                    // find the end of the quoted literal text
                    index = findEndQuoteIndex(index);
                }
            } else {
                // Interpret the apostrophe as literal text.
                // Add a Part for auto-quoting.
                addPart(Part.Type.INSERT_CHAR, index, 0, '\''); // value=char to be inserted
                needsAutoQuoting = true;
            }
        }
        return index;
    }

    public int findEndQuoteIndex(int index) {
        for (;;) {
            index = msg.indexOf('\'', index + 1);
            if (index >= 0) {
                if ((index + 1) < msg.length() && msg.charAt(index + 1) == '\'') {
                    // double apostrophe inside quoted literal text
                    // still encodes a single apostrophe, skip the second one
                    addPart(Part.Type.SKIP_SYNTAX, ++index, 1, 0);
                } else {
                    // skip the quote-ending apostrophe
                    addPart(Part.Type.SKIP_SYNTAX, index++, 1, 0);
                    break;
                }
            } else {
                // The quoted text reaches to the end of the of the message.
                index = msg.length();
                // Add a Part for auto-quoting.
                addPart(Part.Type.INSERT_CHAR, index, 0, '\''); // value=char to be inserted
                needsAutoQuoting = true;
                break;
            }
        }
        return index;
    }

    private int parseArg(int index, int argStartLength, int nestingLevel) {
        int argStart = parts.size();
        ArgType argType = ArgType.NONE;
        addPart(Part.Type.ARG_START, index, argStartLength, argType.ordinal());
        int nameIndex = index = skipWhiteSpace(index + argStartLength);
        if (index == msg.length()) {
            throw new IllegalArgumentException(
                    Level2ExceptionConstants.UNMATCHEDBRACES + prefix());
        }
        // parse argument name or number
        index = parseArgNameOrNumber(nameIndex, index);
        index = skipWhiteSpace(index);
        if (index == msg.length()) {
            throw new IllegalArgumentException(
                    Level2ExceptionConstants.UNMATCHEDBRACES + prefix());
        }
        char c = msg.charAt(index);
        if (c == '}') {
            // all done
        } else if (c != ',') {
            throw new IllegalArgumentException(Level2ExceptionConstants.BADARGUMENTSYNTAX + prefix(nameIndex));
        } else /* ',' */ {
            // parse argument type: case-sensitive a-zA-Z
            Map<String, Object> map = parseArgType(argStart, nameIndex, index);
            // look for an argument style (pattern)
            argType = (ArgType) map.get("argType");
            index = (int) map.get(dataKey_index);
            index = parseArgStyle(argType, nameIndex, index, nestingLevel);
        }
        // Argument parsing stopped on the '}'.
        addLimitPart(argStart, Part.Type.ARG_LIMIT, index, 1, argType.ordinal());
        return index + 1;
    }

    public int parseArgNameOrNumber(int nameIndex, int index) {
        index = skipIdentifier(index);
        int number = parseArgNumber(nameIndex, index);
        if (number >= 0) {
            int length = index - nameIndex;
            if (length > Part.MAX_LENGTH || number > Part.MAX_VALUE) {
                throw new IndexOutOfBoundsException(
                        "Argument number too large: " + prefix(nameIndex));
            }
            hasArgNumbers = true;
            addPart(Part.Type.ARG_NUMBER, nameIndex, length, number);
        } else if (number == ARG_NAME_NOT_NUMBER) {
            int length = index - nameIndex;
            if (length > Part.MAX_LENGTH) {
                throw new IndexOutOfBoundsException(
                        "Argument name too long: " + prefix(nameIndex));
            }
            hasArgNames = true;
            addPart(Part.Type.ARG_NAME, nameIndex, length, 0);
        } else { // number<-1 (ARG_NAME_NOT_VALID)
            throw new IllegalArgumentException(Level2ExceptionConstants.BADARGUMENTSYNTAX + prefix(nameIndex));
        }
        return index;
    }

    public Map<String, Object> parseArgType(int argStart, int nameIndex, int index) {
        Map<String, Object> map = new HashMap<String, Object>();
        int typeIndex = index = skipWhiteSpace(index + 1);
        while (index < msg.length() && isArgTypeChar(msg.charAt(index))) {
            ++index;
        }
        int length = index - typeIndex;
        index = skipWhiteSpace(index);
        if (index == msg.length()) {
            throw new IllegalArgumentException(
                    Level2ExceptionConstants.UNMATCHEDBRACES + prefix());
        }
        char c;
        if (length == 0 || ((c = msg.charAt(index)) != ',' && c != '}')) {
            throw new IllegalArgumentException(Level2ExceptionConstants.BADARGUMENTSYNTAX + prefix(nameIndex));
        }
        if (length > Part.MAX_LENGTH) {
            throw new IndexOutOfBoundsException(
                    "Argument type name too long: " + prefix(nameIndex));
        }
        ArgType argType = getArgType(typeIndex, length);
        // change the ARG_START type from NONE to argType
        parts.get(argStart).value = (short) argType.ordinal();
        if (argType == ArgType.SIMPLE) {
            addPart(Part.Type.ARG_TYPE, typeIndex, length, 0);
        }
        map.put(dataKey_index, index);
        map.put("argType", argType);
        return map;
    }

    private ArgType getArgType(int typeIndex, int typeLength) {
        ArgType argType = ArgType.SIMPLE;
        if (typeLength == 6) {
            // case-insensitive comparisons for complex-type names
            if (isChoice(typeIndex)) {
                argType = ArgType.CHOICE;
            } else if (isPlural(typeIndex)) {
                argType = ArgType.PLURAL;
            } else if (isSelect(typeIndex)) {
                argType = ArgType.SELECT;
            }
        } else if (typeLength == 13 && isSelectOrdinal(typeIndex)) {
            argType = ArgType.SELECTORDINAL;
        }
        return argType;
    }

    public int parseArgStyle(ArgType argType, int nameIndex, int index, int nestingLevel) {
        char c = msg.charAt(index);
        if (c == '}') {
            if (argType != ArgType.SIMPLE) {
                throw new IllegalArgumentException(
                        "No style field for complex argument: " + prefix(nameIndex));
            }
        } else /* ',' */ {
            ++index;
            if (argType == ArgType.PLURAL || argType == ArgType.SELECT) {
                index = parsePluralOrSelectStyle(argType, index, nestingLevel);
            } else {
                throw new Level2Exception("Unsupported argument type '" + argType.toString() + "'!");
            }
        }
        return index;
    }

    private int getSubmessageLimitIndex(ArgType parentType, char c, int msgStart, int index, int nestingLevel) {
        int limitLength = (parentType == ArgType.CHOICE && c == '}') ? 0 : 1;
        addLimitPart(msgStart, Part.Type.MSG_LIMIT, index - 1, limitLength, nestingLevel);
        if (parentType == ArgType.CHOICE) {
            // Let the choice style parser see the '}' or '|'.
            return index - 1;
        } else {
            // continue parsing after the '}'
            return index;
        }
    }

    private int parsePluralOrSelectStyle(ArgType argType, int index, int nestingLevel) {
        int start = index;
        boolean isEmpty = true;
        boolean hasOther = false;
        for (;;) {
            // First, collect the selector looking for a small set of terminators.
            // It would be a little faster to consider the syntax of each possible
            // token right here, but that makes the code too complicated.
            index = skipWhiteSpace(index);
            if (checkTerminators(argType, start, index, nestingLevel, hasOther)) {
                return index;
            }
            int selectorIndex = index;
            if (argType.hasPluralStyle() && msg.charAt(selectorIndex) == '=') {
                // explicit-value plural selector: =double
                index = parseExplicitValuePluralSelector(argType, start, selectorIndex, index);
            } else {
                Map<String, Object> map = parsePluralOffsetOrNormalSelector(argType, start, selectorIndex, index,
                        isEmpty);
                index = (int) map.get(dataKey_index);
                isEmpty = (boolean) map.get("isEmpty");
                hasOther = (boolean) map.get("hasOther");
                boolean hasOffset = (boolean) map.get("hasOffset");
                if (hasOffset) {
                    continue;
                }
            }
            // parse the message fragment following the selector
            index = skipWhiteSpace(index);
            if (index == msg.length() || msg.charAt(index) != '{') {
                throw new IllegalArgumentException(
                        "No message fragment after " +
                                argType.toString().toLowerCase(Locale.ENGLISH) +
                                " selector: " + prefix(selectorIndex));
            }
            index = parseMessage(index, 1, nestingLevel + 1, argType);
            isEmpty = false;
        }
    }

    private boolean checkTerminators(ArgType argType, int start, int index, int nestingLevel, boolean hasOther) {
        boolean hasTerminates = false;
        boolean eos = index == msg.length();
        if (eos || msg.charAt(index) == '}') {
            if (eos == inMessageFormatPattern(nestingLevel)) {
                throw new IllegalArgumentException(
                        "Bad " +
                                argType.toString().toLowerCase(Locale.ENGLISH) +
                                Level2ExceptionConstants.PATTERNSYNTAX + prefix(start));
            }
            if (!hasOther) {
                throw new IllegalArgumentException(
                        "Missing 'other' keyword in " +
                                argType.toString().toLowerCase(Locale.ENGLISH) +
                                " pattern in " + prefix());
            }
            hasTerminates = true;
        }
        return hasTerminates;
    }

    public int parseExplicitValuePluralSelector(ArgType argType, int start, int selectorIndex, int index) {
        index = skipDouble(index + 1);
        int length = index - selectorIndex;
        if (length == 1) {
            throw new IllegalArgumentException(
                    "Bad " +
                            argType.toString().toLowerCase(Locale.ENGLISH) +
                            Level2ExceptionConstants.PATTERNSYNTAX + prefix(start));
        }
        if (length > Part.MAX_LENGTH) {
            throw new IndexOutOfBoundsException(
                    "Argument selector too long: " + prefix(selectorIndex));
        }
        addPart(Part.Type.ARG_SELECTOR, selectorIndex, length, 0);
        parseDouble(selectorIndex + 1, index, false); // adds ARG_INT or ARG_DOUBLE
        return index;
    }

    public Map<String, Object> parsePluralOffsetOrNormalSelector(ArgType argType, int start, int selectorIndex,
            int index, boolean isEmpty) {
        Map<String, Object> map = new HashMap<String, Object>();
        boolean hasOther = false;
        boolean hasOffset = false;
        index = skipIdentifier(index);
        int length = index - selectorIndex;
        if (length == 0) {
            throw new IllegalArgumentException(
                    "Bad " +
                            argType.toString().toLowerCase(Locale.ENGLISH) +
                            Level2ExceptionConstants.PATTERNSYNTAX + prefix(start));
        }
        // Note: The ':' in "offset:" is just beyond the skipIdentifier() range.
        if (argType.hasPluralStyle() && length == 6 && index < msg.length() &&
                msg.regionMatches(selectorIndex, "offset:", 0, 7)) {
            index = parsePluralOffset(isEmpty, start, index);
            isEmpty = false;
            // continue; // no message fragment after the offset
            hasOffset = true;
        } else {
            // normal selector word
            if (length > Part.MAX_LENGTH) {
                throw new IndexOutOfBoundsException(
                        "Argument selector too long: " + prefix(selectorIndex));
            }
            addPart(Part.Type.ARG_SELECTOR, selectorIndex, length, 0);
            if (msg.regionMatches(selectorIndex, "other", 0, length)) {
                hasOther = true;
            }
        }
        map.put(dataKey_index, index);
        map.put("isEmpty", isEmpty);
        map.put("hasOther", hasOther);
        map.put("hasOffset", hasOffset);
        return map;
    }

    public int parsePluralOffset(boolean isEmpty, int start, int index) {
        // plural offset, not a selector
        if (!isEmpty) {
            throw new IllegalArgumentException(
                    "Plural argument 'offset:' (if present) must precede key-message pairs: " +
                            prefix(start));
        }
        // allow whitespace between offset: and its value
        int valueIndex = skipWhiteSpace(index + 1); // The ':' is at index.
        index = skipDouble(valueIndex);
        if (index == valueIndex) {
            throw new IllegalArgumentException(
                    "Missing value for plural 'offset:' " + prefix(start));
        }
        if ((index - valueIndex) > Part.MAX_LENGTH) {
            throw new IndexOutOfBoundsException(
                    "Plural offset value too long: " + prefix(valueIndex));
        }
        parseDouble(valueIndex, index, false); // adds ARG_INT or ARG_DOUBLE
        return index;
    }

    private static int parseArgNumber(CharSequence s, int start, int limit) {
        if ((s.charAt(start) == '0' && limit - start == 1) ||
                (s.charAt(start) != '0' && isNumeric(s.subSequence(start, limit).toString()))) {
            return Integer.parseInt(s.subSequence(start, limit).toString());
        } else if (s.charAt(start) == '0' && limit - start > 1 && isNumeric(s.subSequence(start, limit).toString())) {
            return ARG_NAME_NOT_VALID;
        } else {
            return ARG_NAME_NOT_NUMBER;
        }
    }

    public static boolean isNumeric(String str) {
        for (int i = str.length(); --i >= 0;) {
            int chr = str.charAt(i);
            if (chr < 48 || chr > 57)
                return false;
        }
        return true;
    }

    private int parseArgNumber(int start, int limit) {
        return parseArgNumber(msg, start, limit);
    }

    /**
     * Parses a number from the specified message substring.
     * 
     * @param start
     *            start index into the message string
     * @param limit
     *            limit index into the message string, must be start<limit
     * @param allowInfinity
     *            true if U+221E is allowed (for ChoiceFormat)
     */
    private void parseDouble(int start, int limit, boolean allowInfinity) {
        assert start < limit;
        // fake loop for easy exit and single throw statement
        for (;;) {
            // fast path for small integers and infinity

            int isNegative = 0; // not boolean so that we can easily add it to value
            int index = start;
            char c = msg.charAt(index++);
            if (c == '-') {
                isNegative = 1;
                if (index == limit) {
                    break; // no number
                }
                c = msg.charAt(index++);
            } else if (c == '+') {
                if (index == limit) {
                    break; // no number
                }
                c = msg.charAt(index++);
            }
            int isInfinity = parseInfinity(c, start, limit, index, isNegative, allowInfinity);
            if (isInfinity == 1) {// infinity
                return;
            } else if (isInfinity == 2) {// bad infinity
                break;
            }
            // try to parse the number as a small integer but fall back to a double
            parseNumber(c, start, limit, index, isNegative);
            return;
        }
        throw new NumberFormatException(
                "Bad syntax for numeric value: " + msg.substring(start, limit));
    }

    private int parseInfinity(char c, int start, int limit, int index, int isNegative, boolean allowInfinity) {
        int isInfinity = 0;// not infinity
        if (c == 0x221e) { // infinity
            if (allowInfinity && index == limit) {
                addArgDoublePart(
                        isNegative != 0 ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY,
                        start, limit - start);
                isInfinity = 1;// infinity
            } else {
                isInfinity = 2;// bad infinity
            }
        }
        return isInfinity;
    }

    private void parseNumber(char c, int start, int limit, int index, int isNegative) {
        int value = 0;
        while ('0' <= c && c <= '9') {
            value = value * 10 + (c - '0');
            if (value > (Part.MAX_VALUE + isNegative)) {
                break; // not a small-enough integer
            }
            if (index == limit) {
                addPart(Part.Type.ARG_INT, start, limit - start, isNegative != 0 ? -value : value);
                return;
            }
            c = msg.charAt(index++);
        }
        // Let Double.parseDouble() throw a NumberFormatException.
        double numericValue = Double.parseDouble(msg.substring(start, limit));
        addArgDoublePart(numericValue, start, limit - start);
        return;
    }

    /**
     * Appends the s[start, limit[ substring to sb, but with only half of the apostrophes
     * according to JDK pattern behavior.
     * 
     * @internal
     */
    /* package */ static void appendReducedApostrophes(String s, int start, int limit,
            StringBuilder sb) {
        int doubleApos = -1;
        for (;;) {
            int i = s.indexOf('\'', start);
            if (i < 0 || i >= limit) {
                sb.append(s, start, limit);
                break;
            }
            if (i == doubleApos) {
                // Double apostrophe at start-1 and start==i, append one.
                sb.append('\'');
                ++start;
                doubleApos = -1;
            } else {
                // Append text between apostrophes and skip this one.
                sb.append(s, start, i);
                doubleApos = start = i + 1;
            }
        }
    }

    /**
     * Clears this MessagePattern.
     * countParts() will return 0.
     * 
     * @stable ICU 4.8
     */
    public void clear() {
        // Mostly the same as preParse().
        if (isFrozen()) {
            throw new UnsupportedOperationException(
                    "Attempt to clear() a frozen MessagePattern instance.");
        }
        msg = null;
        hasArgNames = hasArgNumbers = false;
        needsAutoQuoting = false;
        parts.clear();
        if (numericValues != null) {
            numericValues.clear();
        }
    }

    /**
     * Clears this MessagePattern and sets the ApostropheMode.
     * countParts() will return 0.
     * 
     * @param mode
     *            The new ApostropheMode.
     * @stable ICU 4.8
     */
    public void clearPatternAndSetApostropheMode(ApostropheMode mode) {
        clear();
        aposMode = mode;
    }

    /**
     * @return this instance's ApostropheMode.
     * @stable ICU 4.8
     */
    public ApostropheMode getApostropheMode() {
        return aposMode;
    }

    /**
     * @return true if getApostropheMode() == ApostropheMode.DOUBLE_REQUIRED
     * @internal
     */
    /* package */ boolean jdkAposMode() {
        return aposMode == ApostropheMode.DOUBLE_REQUIRED;
    }

    /**
     * @return the parsed pattern string (null if none was parsed).
     * @stable ICU 4.8
     */
    public String getPatternString() {
        return msg;
    }

    /**
     * Does the parsed pattern have named arguments like {first_name}?
     * 
     * @return true if the parsed pattern has at least one named argument.
     * @stable ICU 4.8
     */
    public boolean hasNamedArguments() {
        return hasArgNames;
    }

    /**
     * Does the parsed pattern have numbered arguments like {2}?
     * 
     * @return true if the parsed pattern has at least one numbered argument.
     * @stable ICU 4.8
     */
    public boolean hasNumberedArguments() {
        return hasArgNumbers;
    }

    /**
     * {@inheritDoc}
     * 
     * @stable ICU 4.8
     */
    @Override
    public String toString() {
        return msg;
    }

    /**
     * Validates and parses an argument name or argument number string.
     * An argument name must be a "pattern identifier", that is, it must contain
     * no Unicode Pattern_Syntax or Pattern_White_Space characters.
     * If it only contains ASCII digits, then it must be a small integer with no leading zero.
     * 
     * @param name
     *            Input string.
     * @return &gt;=0 if the name is a valid number,
     *         ARG_NAME_NOT_NUMBER (-1) if it is a "pattern identifier" but not all ASCII digits,
     *         ARG_NAME_NOT_VALID (-2) if it is neither.
     * @stable ICU 4.8
     */
    public static int validateArgumentName(String name) {
        if (!PatternProps.isIdentifier(name)) {
            return ARG_NAME_NOT_VALID;
        }
        return parseArgNumber(name, 0, name.length());
    }

    /**
     * Return value from {@link #validateArgumentName(String)} for when
     * the string is a valid "pattern identifier" but not a number.
     * 
     * @stable ICU 4.8
     */
    public static final int ARG_NAME_NOT_NUMBER = -1;

    /**
     * Return value from {@link #validateArgumentName(String)} for when
     * the string is invalid.
     * It might not be a valid "pattern identifier",
     * or it have only ASCII digits but there is a leading zero or the number is too large.
     * 
     * @stable ICU 4.8
     */
    public static final int ARG_NAME_NOT_VALID  = -2;

    /**
     * Returns a version of the parsed pattern string where each ASCII apostrophe
     * is doubled (escaped) if it is not already, and if it is not interpreted as quoting syntax.
     * <p>
     * For example, this turns "I don't '{know}' {gender,select,female{h''er}other{h'im}}."
     * into "I don''t '{know}' {gender,select,female{h''er}other{h''im}}."
     * 
     * @return the deep-auto-quoted version of the parsed pattern string.
     * @see MessageFormat#autoQuoteApostrophe(String)
     * @stable ICU 4.8
     */
    public String autoQuoteApostropheDeep() {
        if (!needsAutoQuoting) {
            return msg;
        }
        StringBuilder modified = null;
        // Iterate backward so that the insertion indexes do not change.
        int count = countParts();
        for (int i = count; i > 0; --i) {
            Part part = getPart(i - 1);
            if (part.getType() == Part.Type.INSERT_CHAR) {
                if (modified == null) {
                    modified = new StringBuilder(msg.length() + 10).append(msg);
                }
                modified.insert(part.index, (char) part.value);
            }
        }
        if (modified == null) {
            return msg;
        } else {
            return modified.toString();
        }
    }

    /**
     * Returns the number of "parts" created by parsing the pattern string.
     * Returns 0 if no pattern has been parsed or clear() was called.
     * 
     * @return the number of pattern parts.
     * @stable ICU 4.8
     */
    public int countParts() {
        return parts.size();
    }

    /**
     * Gets the i-th pattern "part".
     * 
     * @param i
     *            The index of the Part data. (0..countParts()-1)
     * @return the i-th pattern "part".
     * @throws IndexOutOfBoundsException
     *             if i is outside the (0..countParts()-1) range
     * @stable ICU 4.8
     */
    public Part getPart(int i) {
        return parts.get(i);
    }

    /**
     * Returns the Part.Type of the i-th pattern "part".
     * Convenience method for getPart(i).getType().
     * 
     * @param i
     *            The index of the Part data. (0..countParts()-1)
     * @return The Part.Type of the i-th Part.
     * @throws IndexOutOfBoundsException
     *             if i is outside the (0..countParts()-1) range
     * @stable ICU 4.8
     */
    public Part.Type getPartType(int i) {
        return parts.get(i).type;
    }

    /**
     * Returns the pattern index of the specified pattern "part".
     * Convenience method for getPart(partIndex).getIndex().
     * 
     * @param partIndex
     *            The index of the Part data. (0..countParts()-1)
     * @return The pattern index of this Part.
     * @throws IndexOutOfBoundsException
     *             if partIndex is outside the (0..countParts()-1) range
     * @stable ICU 4.8
     */
    public int getPatternIndex(int partIndex) {
        return parts.get(partIndex).index;
    }

    /**
     * Returns the substring of the pattern string indicated by the Part.
     * Convenience method for getPatternString().substring(part.getIndex(), part.getLimit()).
     * 
     * @param part
     *            a part of this MessagePattern.
     * @return the substring associated with part.
     * @stable ICU 4.8
     */
    public String getSubstring(Part part) {
        int index = part.index;
        return msg.substring(index, index + part.length);
    }

    /**
     * Compares the part's substring with the input string s.
     * 
     * @param part
     *            a part of this MessagePattern.
     * @param s
     *            a string.
     * @return true if getSubstring(part).equals(s).
     * @stable ICU 4.8
     */
    public boolean partSubstringMatches(Part part, String s) {
        return part.length == s.length() && msg.regionMatches(part.index, s, 0, part.length);
    }

    /**
     * Returns the numeric value associated with an ARG_INT or ARG_DOUBLE.
     * 
     * @param part
     *            a part of this MessagePattern.
     * @return the part's numeric value, or NO_NUMERIC_VALUE if this is not a numeric part.
     * @stable ICU 4.8
     */
    public double getNumericValue(Part part) {
        Part.Type type = part.type;
        if (type == Part.Type.ARG_INT) {
            return part.value;
        } else if (type == Part.Type.ARG_DOUBLE) {
            return numericValues.get(part.value);
        } else {
            return NO_NUMERIC_VALUE;
        }
    }

    /**
     * Special value that is returned by getNumericValue(Part) when no
     * numeric value is defined for a part.
     * 
     * @see #getNumericValue
     * @stable ICU 4.8
     */
    public static final double NO_NUMERIC_VALUE = -123456789;

    /**
     * Returns the "offset:" value of a PluralFormat argument, or 0 if none is specified.
     * 
     * @param pluralStart
     *            the index of the first PluralFormat argument style part.
     *            (0..countParts()-1)
     * @return the "offset:" value.
     * @throws IndexOutOfBoundsException
     *             if pluralStart is outside the (0..countParts()-1) range
     * @stable ICU 4.8
     */
    public double getPluralOffset(int pluralStart) {
        Part part = parts.get(pluralStart);
        if (part.type.hasNumericValue()) {
            return getNumericValue(part);
        } else {
            return 0;
        }
    }

    /**
     * Returns the index of the ARG|MSG_LIMIT part corresponding to the ARG|MSG_START at start.
     * 
     * @param start
     *            The index of some Part data (0..countParts()-1);
     *            this Part should be of Type ARG_START or MSG_START.
     * @return The first i&gt;start where getPart(i).getType()==ARG|MSG_LIMIT at the same
     *         nesting level,
     *         or start itself if getPartType(msgStart)!=ARG|MSG_START.
     * @throws IndexOutOfBoundsException
     *             if start is outside the (0..countParts()-1) range
     * @stable ICU 4.8
     */
    public int getLimitPartIndex(int start) {
        int limit = parts.get(start).limitPartIndex;
        if (limit < start) {
            return start;
        }
        return limit;
    }

    public static final class Part {
        private static final int MAX_LENGTH = 0xffff;
        private static final int MAX_VALUE  = Short.MAX_VALUE;

        private final Type       type;
        private final int        index;
        private final char       length;
        private short            value;
        private int              limitPartIndex;

        private Part(Type t, int i, int l, int v) {
            type = t;
            index = i;
            length = (char) l;
            value = (short) v;
        }

        /**
         * Returns the type of this part.
         * 
         * @return the part type.
         */
        public Type getType() {
            return type;
        }

        /**
         * Returns the pattern string index associated with this Part.
         * 
         * @return this part's pattern string index.
         */
        public int getIndex() {
            return index;
        }

        /**
         * Returns the length of the pattern substring associated with this Part.
         * This is 0 for some parts.
         * 
         * @return this part's pattern substring length.
         */
        public int getLength() {
            return length;
        }

        /**
         * Returns the pattern string limit (exclusive-end) index associated with this Part.
         * Convenience method for getIndex()+getLength().
         * 
         * @return this part's pattern string limit index, same as getIndex()+getLength().
         */
        public int getLimit() {
            return index + length;
        }

        /**
         * Returns a value associated with this part.
         * See the documentation of each part type for details.
         * 
         * @return the part value.
         */
        public int getValue() {
            return value;
        }

        /**
         * Returns the argument type if this part is of type ARG_START or ARG_LIMIT,
         * otherwise ArgType.NONE.
         * 
         * @return the argument type for this part.
         */
        public ArgType getArgType() {
            Type msgType = getType();
            if (msgType == Type.ARG_START || msgType == Type.ARG_LIMIT) {
                return argTypes[value];
            } else {
                return ArgType.NONE;
            }
        }

        /**
         * Part type constants.
         */
        public enum Type {
            /**
             * Start of a message pattern (main or nested).
             * The length is 0 for the top-level message
             * and for a choice argument sub-message, otherwise 1 for the '{'.
             * The value indicates the nesting level, starting with 0 for the main message.
             * <p>
             * There is always a later MSG_LIMIT part.
             */
            MSG_START,
            /**
             * End of a message pattern (main or nested).
             * The length is 0 for the top-level message and
             * the last sub-message of a choice argument,
             * otherwise 1 for the '}' or (in a choice argument style) the '|'.
             * The value indicates the nesting level, starting with 0 for the main message.
             */
            MSG_LIMIT,
            /**
             * Indicates a substring of the pattern string which is to be skipped when formatting.
             * For example, an apostrophe that begins or ends quoted text
             * would be indicated with such a part.
             * The value is undefined and currently always 0.
             */
            SKIP_SYNTAX,
            /**
             * Indicates that a syntax character needs to be inserted for auto-quoting.
             * The length is 0.
             * The value is the character code of the insertion character. (U+0027=APOSTROPHE)
             */
            INSERT_CHAR,
            /**
             * Indicates a syntactic (non-escaped) # symbol in a plural variant.
             * When formatting, replace this part's substring with the
             * (value-offset) for the plural argument value.
             * The value is undefined and currently always 0.
             */
            REPLACE_NUMBER,
            /**
             * Start of an argument.
             * The length is 1 for the '{'.
             * The value is the ordinal value of the ArgType. Use getArgType().
             * <p>
             * This part is followed by either an ARG_NUMBER or ARG_NAME,
             * followed by optional argument sub-parts (see ArgType constants)
             * and finally an ARG_LIMIT part.
             */
            ARG_START,
            /**
             * End of an argument.
             * The length is 1 for the '}'.
             * The value is the ordinal value of the ArgType. Use getArgType().
             */
            ARG_LIMIT,
            /**
             * The argument number, provided by the value.
             */
            ARG_NUMBER,
            /**
             * The argument name.
             * The value is undefined and currently always 0.
             */
            ARG_NAME,
            /**
             * The argument type.
             * The value is undefined and currently always 0.
             */
            ARG_TYPE,
            /**
             * The argument style text.
             * The value is undefined and currently always 0.
             */
            ARG_STYLE,
            /**
             * A selector substring in a "complex" argument style.
             * The value is undefined and currently always 0.
             */
            ARG_SELECTOR,
            /**
             * An integer value, for example the offset or an explicit selector value
             * in a PluralFormat style.
             * The part value is the integer value.
             */
            ARG_INT,
            /**
             * A numeric value, for example the offset or an explicit selector value
             * in a PluralFormat style.
             * The part value is an index into an internal array of numeric values;
             * use getNumericValue().
             */
            ARG_DOUBLE;

            /**
             * Indicates whether this part has a numeric value.
             * If so, then that numeric value can be retrieved via {@link MessagePattern#getNumericValue(Part)}.
             * 
             * @return true if this part has a numeric value.
             */
            public boolean hasNumericValue() {
                return this == ARG_INT || this == ARG_DOUBLE;
            }
        }

    }

    /**
     * Argument type constants.
     * Returned by Part.getArgType() for ARG_START and ARG_LIMIT parts.
     *
     * Messages nested inside an argument are each delimited by MSG_START and MSG_LIMIT,
     * with a nesting level one greater than the surrounding message.
     * 
     * @stable ICU 4.8
     */
    public enum ArgType {
        /**
         * The argument has no specified type.
         */
        NONE,
        /**
         * The argument has a "simple" type which is provided by the ARG_TYPE part.
         * An ARG_STYLE part might follow that.
         */
        SIMPLE,
        /**
         * The argument is a ChoiceFormat with one or more
         * ((ARG_INT | ARG_DOUBLE), ARG_SELECTOR, message) tuples.
         */
        CHOICE,
        /**
         * The argument is a cardinal-number PluralFormat with an optional ARG_INT or ARG_DOUBLE offset
         * (e.g., offset:1)
         * and one or more (ARG_SELECTOR [explicit-value] message) tuples.
         * If the selector has an explicit value (e.g., =2), then
         * that value is provided by the ARG_INT or ARG_DOUBLE part preceding the message.
         * Otherwise the message immediately follows the ARG_SELECTOR.
         */
        PLURAL,
        /**
         * The argument is a SelectFormat with one or more (ARG_SELECTOR, message) pairs.
         */
        SELECT,
        /**
         * The argument is an ordinal-number PluralFormat
         * with the same style parts sequence and semantics as {@link ArgType#PLURAL}.
         */
        SELECTORDINAL;

        /**
         * @return true if the argument type has a plural style part sequence and semantics,
         *         for example {@link ArgType#PLURAL} and {@link ArgType#SELECTORDINAL}.
         */
        public boolean hasPluralStyle() {
            return this == PLURAL || this == SELECTORDINAL;
        }
    }

    private int skipWhiteSpace(int index) {
        return PatternProps.skipWhiteSpace(msg, index);
    }

    private int skipIdentifier(int index) {
        return PatternProps.skipIdentifier(msg, index);
    }

    /**
     * Skips a sequence of characters that could occur in a double value.
     * Does not fully parse or validate the value.
     */
    private int skipDouble(int index) {
        while (index < msg.length()) {
            char c = msg.charAt(index);
            // U+221E: Allow the infinity symbol, for ChoiceFormat patterns.
            if ((c < '0' && "+-.".indexOf(c) < 0) || (c > '9' && c != 'e' && c != 'E' && c != 0x221e)) {
                break;
            }
            ++index;
        }
        return index;
    }

    private static boolean isArgTypeChar(int c) {
        return ('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z');
    }

    private boolean isChoice(int index) {
        String argType = msg.substring(index, index + 6);
        if (argType.equalsIgnoreCase("choice")) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isPlural(int index) {
        String argType = msg.substring(index, index + 6);
        if (argType.equalsIgnoreCase("plural")) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isSelect(int index) {
        String argType = msg.substring(index, index + 6);
        if (argType.equalsIgnoreCase("select")) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isOrdinal(int index) {
        String argType = msg.substring(index, index + 7);
        if (argType.equalsIgnoreCase("ordinal")) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isSelectOrdinal(int typeIndex) {
        return isSelect(typeIndex) && isOrdinal(typeIndex + 6);
    }

    /**
     * @return true if we are inside a MessageFormat (sub-)pattern,
     *         as opposed to inside a top-level choice/plural/select pattern.
     */
    private boolean inMessageFormatPattern(int nestingLevel) {
        return nestingLevel > 0 || parts.get(0).type == Part.Type.MSG_START;
    }

    /**
     * @return true if we are in a MessageFormat sub-pattern
     *         of a top-level ChoiceFormat pattern.
     */
    private boolean inTopLevelChoiceMessage(int nestingLevel, ArgType parentType) {
        return nestingLevel == 1 &&
                parentType == ArgType.CHOICE &&
                parts.get(0).type != Part.Type.MSG_START;
    }

    private void addPart(Part.Type type, int index, int length, int value) {
        parts.add(new Part(type, index, length, value));
    }

    private void addLimitPart(int start, Part.Type type, int index, int length, int value) {
        parts.get(start).limitPartIndex = parts.size();
        addPart(type, index, length, value);
    }

    private void addArgDoublePart(double numericValue, int start, int length) {
        int numericIndex;
        if (numericValues == null) {
            numericValues = new ArrayList<Double>();
            numericIndex = 0;
        } else {
            numericIndex = numericValues.size();
            if (numericIndex > Part.MAX_VALUE) {
                throw new IndexOutOfBoundsException("Too many numeric values");
            }
        }
        numericValues.add(numericValue);
        addPart(Part.Type.ARG_DOUBLE, start, length, numericIndex);
    }

    private static final int MAX_PREFIX_LENGTH = 24;

    /**
     * Returns a prefix of s.substring(start). Used for Exception messages.
     * 
     * @param s
     * @param start
     *            start index in s
     * @return s.substring(start) or a prefix of that
     */
    private static String prefix(String s, int start) {
        StringBuilder prefix = new StringBuilder(MAX_PREFIX_LENGTH + 20);
        if (start == 0) {
            prefix.append("\"");
        } else {
            prefix.append("[at pattern index ").append(start).append("] \"");
        }
        int substringLength = s.length() - start;
        if (substringLength <= MAX_PREFIX_LENGTH) {
            prefix.append(start == 0 ? s : s.substring(start));
        } else {
            int limit = start + MAX_PREFIX_LENGTH - 4;
            if (Character.isHighSurrogate(s.charAt(limit - 1))) {
                // remove lead surrogate from the end of the prefix
                --limit;
            }
            prefix.append(s, start, limit).append(" ...");
        }
        return prefix.append("\"").toString();
    }

    private static String prefix(String s) {
        return prefix(s, 0);
    }

    private String prefix(int start) {
        return prefix(msg, start);
    }

    private String prefix() {
        return prefix(msg, 0);
    }

    /**
     * Determines whether this object is frozen (immutable) or not.
     * 
     * @return true if this object is frozen.
     * @stable ICU 4.8
     */
    public boolean isFrozen() {
        return frozen;
    }
}
