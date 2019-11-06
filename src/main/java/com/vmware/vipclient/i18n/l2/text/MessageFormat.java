/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.l2.text;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.text.AttributedCharacterIterator;
import java.text.AttributedCharacterIterator.Attribute;
import java.text.CharacterIterator;
import java.text.FieldPosition;
import java.text.Format;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.vmware.vipclient.i18n.I18nFactory;
import com.vmware.vipclient.i18n.base.instances.NumberFormatting;
import com.vmware.vipclient.i18n.exceptions.VIPUncheckedIOException;
import com.vmware.vipclient.i18n.l2.plural.parser.PluralRules;
import com.vmware.vipclient.i18n.l2.plural.parser.PluralRules.PluralType;
import com.vmware.vipclient.i18n.l2.text.MessagePattern.ArgType;
import com.vmware.vipclient.i18n.l2.text.MessagePattern.Part;
import com.vmware.vipclient.i18n.util.LocaleUtility;

public class MessageFormat {
    private Locale                           locale;

    // ===========================privates============================

    // *Important*: All fields must be declared *transient* so that we can fully
    // control serialization!
    // See for example Joshua Bloch's "Effective Java", chapter 10 Serialization.

    /**
     * The MessagePattern which contains the parsed structure of the pattern string.
     */
    private transient MessagePattern         msgPattern;
    /**
     * Cached formatters so we can just use them whenever needed instead of creating
     * them from scratch every time.
     */
    private transient Map<Integer, Format>   cachedFormatters;

    private transient PluralSelectorProvider pluralProvider;
    private transient PluralSelectorProvider ordinalProvider;

    public MessageFormat() {

    }

    public MessageFormat(String pattern) {
        this.locale = LocaleUtility.defaultLocale;
        applyPattern(pattern);
    }

    public MessageFormat(String pattern, Locale locale) {
        this.locale = locale;
        applyPattern(pattern);
    }

    /**
     * Sets the pattern used by this message format.
     * Parses the pattern and caches Format objects for simple argument types.
     * Patterns and their interpretation are specified in the
     * <a href="#patterns">class description</a>.
     *
     * @param pttrn
     *            the pattern for this message format
     * @throws IllegalArgumentException
     *             if the pattern is invalid
     */
    public void applyPattern(String pttrn) {
        try {
            if (msgPattern == null) {
                msgPattern = new MessagePattern(pttrn);
            } else {
                msgPattern.parse(pttrn);
            }
            // Cache the formats that are explicitly mentioned in the message pattern.
        } catch (RuntimeException e) {
            resetPattern();
            throw e;
        }
    }

    /**
     * {@icu} Sets the ApostropheMode and the pattern used by this message format.
     * Parses the pattern and caches Format objects for simple argument types.
     * Patterns and their interpretation are specified in the
     * <a href="#patterns">class description</a>.
     * <p>
     * This method is best used only once on a given object to avoid confusion about the mode,
     * and after constructing the object with an empty pattern string to minimize overhead.
     *
     * @param pattern
     *            the pattern for this message format
     * @param aposMode
     *            the new ApostropheMode
     * @throws IllegalArgumentException
     *             if the pattern is invalid
     * @see MessagePattern.ApostropheMode
     */
    public void applyPattern(String pattern, MessagePattern.ApostropheMode aposMode) {
        if (msgPattern == null) {
            msgPattern = new MessagePattern(aposMode);
        } else if (aposMode != msgPattern.getApostropheMode()) {
            msgPattern.clearPatternAndSetApostropheMode(aposMode);
        }
        applyPattern(pattern);
    }

    public final StringBuilder format(Object[] arguments, StringBuilder result,
            FieldPosition pos) {
        format(arguments, null, new AppendableWrapper(result), pos);
        return result;
    }

    public final StringBuilder format(Map<String, Object> arguments,
            StringBuilder result, FieldPosition pos) {
        format(null, arguments, new AppendableWrapper(result), pos);
        return result;
    }

    private void format(Object[] arguments, Map<String, Object> argsMap,
            AppendableWrapper dest, FieldPosition fp) {
        if (arguments != null && msgPattern.hasNamedArguments()) {
            throw new IllegalArgumentException(
                    "This method is not available in MessageFormat objects "
                            + "that use alphanumeric argument names.");
        }
        format(0, null, arguments, argsMap, dest, fp);
    }

    // *Important*: All fields must be declared *transient*.
    // See the longer comment above ulocale.

    /**
     * Formats the arguments and writes the result into the
     * AppendableWrapper, updates the field position.
     *
     * <p>
     * Exactly one of args and argsMap must be null, the other non-null.
     *
     * @param msgStart
     *            Index to msgPattern part to start formatting from.
     * @param pluralNumber
     *            null except when formatting a plural argument sub-message
     *            where a '#' is replaced by the format string for this number.
     * @param args
     *            The formattable objects array. Non-null iff numbered values are used.
     * @param argsMap
     *            The key-value map of formattable objects. Non-null iff named values are used.
     * @param dest
     *            Output parameter to receive the result.
     *            The result (string & attributes) is appended to existing contents.
     * @param fp
     *            Field position status.
     */
    private void format(int msgStart, PluralSelectorContext pluralNumber,
            Object[] args, Map<String, Object> argsMap,
            AppendableWrapper dest, FieldPosition fp) {
        String msgString = msgPattern.getPatternString();
        int prevIndex = msgPattern.getPart(msgStart).getLimit();
        int i = msgStart + 1;
        for (;; ++i) {
            Part part = msgPattern.getPart(i);
            Part.Type type = part.getType();
            int index = part.getIndex();
            dest.append(msgString, prevIndex, index);
            if (type == Part.Type.MSG_LIMIT) {
                return;
            }
            prevIndex = part.getLimit();
            if (type == Part.Type.REPLACE_NUMBER) {
                if (pluralNumber.forReplaceNumber) {
                    // number-offset was already formatted.
                    dest.formatAndAppend(pluralNumber.formatter,
                            pluralNumber.number, pluralNumber.numberString);
                } else {
                    I18nFactory factory = I18nFactory.getInstance();
                    NumberFormatting p = (NumberFormatting) factory.getFormattingInstance(NumberFormatting.class);
                    // dest.append(new NumberFormatting().formatNumber(pluralNumber.number, locale.toLanguageTag()));
                    dest.append(p.formatNumber(pluralNumber.number, locale));
                }
                continue;
            }
            if (type != Part.Type.ARG_START) {
                continue;
            }
            Map dataMap = formatArg(pluralNumber, args, argsMap, dest, fp, i);
            prevIndex = (int) dataMap.get("prevIndex");
            i = (int) dataMap.get("argLimit");
        }
    }

    private Map formatArg(PluralSelectorContext pluralNumber,
            Object[] args, Map<String, Object> argsMap,
            AppendableWrapper dest, FieldPosition fp, int i) {
        Map<String, Integer> dataMap = new HashMap<String, Integer>();
        int argLimit = msgPattern.getLimitPartIndex(i);
        Part part = msgPattern.getPart(++i);
        String argName = msgPattern.getSubstring(part);
        Map<String, Object> argMap = getArgValue(args, argsMap, dest, part);
        Object argId = argMap.get("argId");
        ++i;
        int prevDestLength = dest.length;
        format(pluralNumber, i, argName, argMap, args, argsMap,
                dest);
        updateMetaData(dest, prevDestLength, fp, argId);
        int prevIndex = msgPattern.getPart(argLimit).getLimit();
        dataMap.put("argLimit", argLimit);
        dataMap.put("prevIndex", prevIndex);
        return dataMap;
    }

    private Map<String, Object> getArgValue(Object[] args, Map<String, Object> argsMap,
            AppendableWrapper dest, Part part) {
        Map<String, Object> map = new HashMap<String, Object>();
        Object arg;
        boolean noArg = false;
        Object argId = null;
        String argName = msgPattern.getSubstring(part);
        if (args != null) {
            int argNumber = part.getValue(); // ARG_NUMBER
            if (dest.attributes != null) {
                // We only need argId if we add it into the attributes.
                argId = Integer.valueOf(argNumber);
            }
            if (0 <= argNumber && argNumber < args.length) {
                arg = args[argNumber];
            } else {
                arg = null;
                noArg = true;
            }
        } else {
            argId = argName;
            if (argsMap != null && argsMap.containsKey(argName)) {
                arg = argsMap.get(argName);
            } else {
                arg = null;
                noArg = true;
            }
        }
        map.put("noArg", noArg);
        map.put("argId", argId);
        map.put("arg", arg);
        return map;
    }

    private void format(PluralSelectorContext pluralNumber, int i, String argName, Map map, Object[] args,
            Map<String, Object> argsMap,
            AppendableWrapper dest) {
        Format formatter = null;
        Part part = msgPattern.getPart(i - 2);
        ArgType argType = part.getArgType();
        Object arg = map.get("arg");
        boolean noArg = (boolean) map.get("noArg");
        if (noArg) {
            dest.append("{" + argName + "}");
        } else if (arg == null) {
            dest.append("null");
        } else if (pluralNumber != null && pluralNumber.numberArgIndex == (i - 2)) {
            if (pluralNumber.offset == 0) {
                // The number was already formatted with this formatter.
                dest.formatAndAppend(pluralNumber.formatter, pluralNumber.number, pluralNumber.numberString);
            } else {
                // Do not use the formatted (number-offset) string for a named argument
                // that formats the number without subtracting the offset.
                dest.formatAndAppend(pluralNumber.formatter, arg);
            }
        } else if (argType == ArgType.NONE ||
                (cachedFormatters != null && cachedFormatters.containsKey(i - 2))) {
            dest.append(arg.toString());
        } else if (argType.hasPluralStyle()) {
            formatPluralOrSelectMsg(argType, i, argName, arg, args, argsMap, dest);
        } else {
            // This should never happen.
            throw new IllegalStateException("unexpected argType " + argType);
        }
    }

    private void formatPluralOrSelectMsg(ArgType argType, int i, String argName, Object arg, Object[] args,
            Map<String, Object> argsMap,
            AppendableWrapper dest) {
        if (!(arg instanceof Number)) {
            throw new IllegalArgumentException("'" + arg + "' is not a Number");
        }
        PluralSelectorProvider selector;
        if (argType == ArgType.PLURAL) {
            if (pluralProvider == null) {
                pluralProvider = new PluralSelectorProvider(this, PluralType.CARDINAL);
            }
            selector = pluralProvider;
        } else {
            if (ordinalProvider == null) {
                ordinalProvider = new PluralSelectorProvider(this, PluralType.ORDINAL);
            }
            selector = ordinalProvider;
        }
        Number number = (Number) arg;
        double offset = msgPattern.getPluralOffset(i);
        PluralSelectorContext context = new PluralSelectorContext(i, argName, number, offset);
        int subMsgStart = PluralFormat.findSubMessage(
                msgPattern, i, selector, context, number.doubleValue());
        formatComplexSubMessage(subMsgStart, context, args, argsMap, dest);
    }

    private void formatComplexSubMessage(
            int msgStart, PluralSelectorContext pluralNumber,
            Object[] args, Map<String, Object> argsMap,
            AppendableWrapper dest) {
        if (!msgPattern.jdkAposMode()) {
            format(msgStart, pluralNumber, args, argsMap, dest, null);
            return;
        }
    }

    private FieldPosition updateMetaData(AppendableWrapper dest,
            int prevLength, FieldPosition fp, Object argId) {
        if (dest.attributes != null && prevLength < dest.length) {
            dest.attributes.add(new AttributeAndPosition(argId, prevLength,
                    dest.length));
        }
        if (fp != null && Field2.ARGUMENT.equals(fp.getFieldAttribute())) {
            fp.setBeginIndex(prevLength);
            fp.setEndIndex(dest.length);
            return null;
        }
        return fp;
    }

    private void resetPattern() {
        if (msgPattern != null) {
            msgPattern.clear();
        }
        if (cachedFormatters != null) {
            cachedFormatters.clear();
        }
    }

    public static class Field2 extends Format.Field {

        private static final long serialVersionUID = 7510380454602616157L;

        /**
         * Create a <code>Field</code> with the specified name.
         *
         * @param name
         *            The name of the attribute
         */
        protected Field2(String name) {
            super(name);
        }

        /**
         * Resolves instances being deserialized to the predefined constants.
         *
         * @return resolved MessageFormat.Field constant
         * @throws InvalidObjectException
         *             if the constant could not be resolved.
         */
        @Override
        protected Object readResolve() throws InvalidObjectException {
            if (this.getClass() != MessageFormat.Field2.class) {
                throw new InvalidObjectException(
                        "A subclass of MessageFormat.Field must implement readResolve.");
            }
            if (this.getName().equals(ARGUMENT.getName())) {
                return ARGUMENT;
            } else {
                throw new InvalidObjectException("Unknown attribute name.");
            }
        }

        /**
         * Constant identifying a portion of a message that was generated
         * from an argument passed into <code>formatToCharacterIterator</code>.
         * The value associated with the key will be an <code>Integer</code>
         * indicating the index in the <code>arguments</code> array of the
         * argument from which the text was generated.
         */
        public static final Field2 ARGUMENT = new Field2("message argument field");
    }

    /**
     * Mutable input/output values for the PluralSelectorProvider.
     * Separate so that it is possible to make MessageFormat Freezable.
     */
    private static final class PluralSelectorContext {
        private PluralSelectorContext(int start, String name, Number num, double off) {
            startIndex = start;
            argName = name;
            // number needs to be set even when select() is not called.
            // Keep it as a Number/Formattable:
            // For format() methods, and to preserve information (e.g., BigDecimal).
            if (off == 0) {
                number = num;
            } else {
                number = num.doubleValue() - off;
            }
            offset = off;
        }

        @Override
        public String toString() {
            throw new AssertionError("PluralSelectorContext being formatted, rather than its number");
        }

        // Input values for plural selection with decimals.
        int     startIndex;
        String  argName;
        /** argument number - plural offset */
        Number  number;
        double  offset;
        // Output values for plural selection with decimals.
        /** -1 if REPLACE_NUMBER, 0 arg not found, >0 ARG_START index */
        int     numberArgIndex;
        Format  formatter;
        /** formatted argument number - plural offset */
        String  numberString;
        /** true if number-offset was formatted with the stock number formatter */
        boolean forReplaceNumber;
    }

    /**
     * This provider helps defer instantiation of a PluralRules object
     * until we actually need to select a keyword.
     * For example, if the number matches an explicit-value selector like "=1"
     * we do not need any PluralRules.
     */
    private static final class PluralSelectorProvider implements PluralFormat.PluralSelector {
        public PluralSelectorProvider(MessageFormat mf, PluralType type) {
            msgFormat = mf;
            this.type = type;
        }

        public String select(Object ctx, double number) {
            if (rules == null) {
                rules = PluralRules.forLocale(msgFormat.locale, type);
            }
            return rules.select(number);
        }

        private MessageFormat msgFormat;
        private PluralRules   rules;
        private PluralType    type;
    }

    /**
     * Convenience wrapper for Appendable, tracks the result string length.
     * Also, Appendable throws IOException, and we turn that into a RuntimeException
     * so that we need no throws clauses.
     */
    private static final class AppendableWrapper {
        public AppendableWrapper(StringBuilder sb) {
            app = sb;
            length = sb.length();
            attributes = null;
        }

        public void useAttributes() {
            attributes = new ArrayList<AttributeAndPosition>();
        }

        public void append(CharSequence s) {
            try {
                app.append(s);
                length += s.length();
            } catch (IOException e) {
                throw new VIPUncheckedIOException(e);
            }
        }

        public void append(CharSequence s, int start, int limit) {
            try {
                app.append(s, start, limit);
                length += limit - start;
            } catch (IOException e) {
                throw new VIPUncheckedIOException(e);
            }
        }

        public void append(CharacterIterator iterator) {
            length += append(app, iterator);
        }

        public static int append(Appendable result, CharacterIterator iterator) {
            try {
                int start = iterator.getBeginIndex();
                int limit = iterator.getEndIndex();
                int length = limit - start;
                if (start < limit) {
                    result.append(iterator.first());
                    while (++start < limit) {
                        result.append(iterator.next());
                    }
                }
                return length;
            } catch (IOException e) {
                throw new VIPUncheckedIOException(e);
            }
        }

        public void formatAndAppend(Format formatter, Object arg) {
            if (attributes == null) {
                append(formatter.format(arg));
            } else {
                AttributedCharacterIterator formattedArg = formatter.formatToCharacterIterator(arg);
                int prevLength = length;
                append(formattedArg);
                // Copy all of the attributes from formattedArg to our attributes list.
                formattedArg.first();
                int start = formattedArg.getIndex(); // Should be 0 but might not be.
                int limit = formattedArg.getEndIndex(); // == start + length - prevLength
                int offset = prevLength - start; // Adjust attribute indexes for the result string.
                while (start < limit) {
                    Map<Attribute, Object> map = formattedArg.getAttributes();
                    int runLimit = formattedArg.getRunLimit();
                    if (map.size() != 0) {
                        for (Map.Entry<Attribute, Object> entry : map.entrySet()) {
                            attributes.add(
                                    new AttributeAndPosition(
                                            entry.getKey(), entry.getValue(),
                                            offset + start, offset + runLimit));
                        }
                    }
                    start = runLimit;
                    formattedArg.setIndex(start);
                }
            }
        }

        public void formatAndAppend(Format formatter, Object arg, String argString) {
            if (attributes == null && argString != null) {
                append(argString);
            } else {
                formatAndAppend(formatter, arg);
            }
        }

        private Appendable                 app;
        private int                        length;
        private List<AttributeAndPosition> attributes;
    }

    private static final class AttributeAndPosition {
        /**
         * Defaults the field to Field.ARGUMENT.
         */
        public AttributeAndPosition(Object fieldValue, int startIndex, int limitIndex) {
            init(Field2.ARGUMENT, fieldValue, startIndex, limitIndex);
        }

        public AttributeAndPosition(Attribute field, Object fieldValue, int startIndex, int limitIndex) {
            init(field, fieldValue, startIndex, limitIndex);
        }

        public void init(Attribute field, Object fieldValue, int startIndex, int limitIndex) {
            key = field;
            value = fieldValue;
            start = startIndex;
            limit = limitIndex;
        }

        public void redundantMethod() {
            if (key == null)
                ;
            if (value == null)
                ;
            if (start == 0)
                ;
            if (limit == 0)
                ;
        }

        private Attribute key;
        private Object    value;
        private int       start;
        private int       limit;
    }
}
