/*
 * Copyright 2019-2025 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.l2.plural.parser;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Set;
import java.util.regex.Pattern;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vipclient.i18n.l2.common.PatternCategory;
import com.vmware.vipclient.i18n.l2.common.PatternKeys;
import com.vmware.vipclient.i18n.messages.service.PatternService;

public class PluralRules implements Serializable {
    static Logger                       logger           = LoggerFactory.getLogger(PluralRules.class);
    private static final long           serialVersionUID = 1;

    private final RuleList              rules;
    @SuppressWarnings("unused")
    private final transient Set<String> keywords;

    public static final String          KEYWORD_ZERO     = "zero";

    public static final String          KEYWORD_ONE      = "one";

    public static final String          KEYWORD_TWO      = "two";

    public static final String          KEYWORD_FEW      = "few";

    public static final String          KEYWORD_MANY     = "many";

    public static final String          KEYWORD_OTHER    = "other";

    public static final double          NO_UNIQUE_VALUE  = -0.00123456777;

    protected static final List<String> KEYWORD_LIST     = Arrays.asList(KEYWORD_ZERO, KEYWORD_ONE, KEYWORD_TWO,
            KEYWORD_FEW, KEYWORD_MANY, KEYWORD_OTHER);

    public enum PluralType {
        CARDINAL,
        ORDINAL
    };

    private static final Constraint NO_CONSTRAINT = new Constraint() {
                                                      private static final long serialVersionUID = 9163464945387899416L;

                                                      public boolean isFulfilled(IFixedDecimal n) {
                                                          return true;
                                                      }

                                                      public boolean isLimited(SampleType sampleType) {
                                                          return false;
                                                      }

                                                      @Override
                                                      public String toString() {
                                                          return "";
                                                      }
                                                  };

    private static final Rule       DEFAULT_RULE  = new Rule("other", NO_CONSTRAINT, null, null);

    /**
     * Parses a plural rules string and returns a PluralRules.
     * 
     * @param rule
     *            the rule string.
     * @throws ParseException.
     */
    public static PluralRules parse(String rule) throws ParseException {
        rule = rule.trim();
        return rule.length() == 0 ? DEFAULT : new PluralRules(parseRuleChain(rule));
    }

    public static PluralRules parse(Map<String, String> rules) throws ParseException {
        return rules == null || rules.size() == 0 ? DEFAULT : new PluralRules(parseRuleChain(rules));
    }

    /**
     * Creates a PluralRules from a rule if it is parsable, otherwise
     * returns null.
     * 
     * @param description
     *            the rule rule.
     * @return the PluralRules
     */
    public static PluralRules getInstance(String rule) {
        try {
            return parse(rule);
        } catch (Exception e) {
            return null;
        }
    }

    public static PluralRules getInstance(Map<String, String> rule) {
        try {
            return parse(rule);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * The default rules
     */
    public static final PluralRules DEFAULT = new PluralRules(new RuleList().addRule(DEFAULT_RULE));

    public static enum Operand {
        /**
         * The double value of the entire number.
         */
        n,

        /**
         * The integer value, with the fraction digits truncated off.
         */
        i,

        /**
         * All visible fraction digits as an integer, including trailing zeros.
         */
        f,

        /**
         * Visible fraction digits as an integer, not including trailing zeros.
         */
        t,

        /**
         * Number of visible fraction digits.
         */
        v,

        /**
         * Number of visible fraction digits, not including trailing zeros.
         */
        w,

        /**
         * THIS OPERAND IS DEPRECATED AND HAS BEEN REMOVED FROM THE SPEC.
         */
        j;
    }

    public static interface IFixedDecimal {
        public double getPluralOperand(Operand operand);

        public boolean isNaN();

        public boolean isInfinite();
    }

    public static class FixedDecimal extends Number implements Comparable<FixedDecimal>, IFixedDecimal {
        private static final long serialVersionUID = -4756200506571685661L;

        public final double       source;

        final int                 visibleDecimalDigitCount;

        final int                 visibleDecimalDigitCountWithoutTrailingZeros;

        final long                decimalDigits;

        final long                decimalDigitsWithoutTrailingZeros;

        final long                integerValue;

        final boolean             hasIntegerValue;

        public final boolean      isNegative;

        private final int         baseFactor;

        public double getSource() {
            return source;
        }

        public int getVisibleDecimalDigitCount() {
            return visibleDecimalDigitCount;
        }

        public int getVisibleDecimalDigitCountWithoutTrailingZeros() {
            return visibleDecimalDigitCountWithoutTrailingZeros;
        }

        public long getDecimalDigits() {
            return decimalDigits;
        }

        public long getDecimalDigitsWithoutTrailingZeros() {
            return decimalDigitsWithoutTrailingZeros;
        }

        public long getIntegerValue() {
            return integerValue;
        }

        public boolean isHasIntegerValue() {
            return hasIntegerValue;
        }

        public boolean isNegative() {
            return isNegative;
        }

        public int getBaseFactor() {
            return baseFactor;
        }

        static final long MAX = (long) 1E18;

        /**
         * @param n
         *            is the original number
         * @param v
         *            number of digits to the right of the decimal place. e.g
         *            1.00 = 2 25. = 0
         * @param f
         *            Corresponds to f in the plural rules grammar. The digits
         *            to the right of the decimal place as an integer. e.g 1.10
         *            = 10
         */
        public FixedDecimal(double n, int v, long f) {
            isNegative = n < 0;
            source = isNegative ? -n : n;
            visibleDecimalDigitCount = v;
            decimalDigits = f;
            integerValue = n > MAX ? MAX : (long) n;
            hasIntegerValue = source == integerValue;
            if (f == 0) {
                decimalDigitsWithoutTrailingZeros = 0;
                visibleDecimalDigitCountWithoutTrailingZeros = 0;
            } else {
                long fdwtz = f;
                int trimmedCount = v;
                while ((fdwtz % 10) == 0) {
                    fdwtz /= 10;
                    --trimmedCount;
                }
                decimalDigitsWithoutTrailingZeros = fdwtz;
                visibleDecimalDigitCountWithoutTrailingZeros = trimmedCount;
            }
            baseFactor = (int) Math.pow(10, v);
        }

        public FixedDecimal(double n, int v) {
            this(n, v, getFractionalDigits(n, v));
        }

        private static int getFractionalDigits(double n, int v) {
            if (v == 0) {
                return 0;
            } else {
                if (n < 0) {
                    n = -n;
                }
                int baseFactor = (int) Math.pow(10, v);
                long scaled = Math.round(n * baseFactor);
                return (int) (scaled % baseFactor);
            }
        }

        public FixedDecimal(double n) {
            this(n, decimals(n));
        }

        public FixedDecimal(long n) {
            this(n, 0);
        }

        private static final long MAX_INTEGER_PART = 1000000000;

        /**
         * Return a guess as to the number of decimals that would be displayed.
         * This is only a guess; callers should always supply the decimals
         * explicitly if possible.
         */
        public static int decimals(double n) {
            if (Double.isInfinite(n) || Double.isNaN(n)) {
                return 0;
            }
            if (n < 0) {
                n = -n;
            }
            if (n == Math.floor(n)) {
                return 0;
            }
            if (n < MAX_INTEGER_PART) {
                long temp = (long) (n * 1000000) % 1000000; // get 6 decimals
                for (int mask = 10, digits = 6; digits > 0; mask *= 10, --digits) {
                    if ((temp % mask) != 0) {
                        return digits;
                    }
                }
                return 0;
            } else {
                String buf = String.format(Locale.ENGLISH, "%1.15e", n);
                int ePos = buf.lastIndexOf('e');
                int expNumPos = ePos + 1;
                if (buf.charAt(expNumPos) == '+') {
                    expNumPos++;
                }
                String exponentStr = buf.substring(expNumPos);
                int exponent = Integer.parseInt(exponentStr);
                int numFractionDigits = ePos - 2 - exponent;
                if (numFractionDigits < 0) {
                    return 0;
                }
                for (int i = ePos - 1; numFractionDigits > 0; --i) {
                    if (buf.charAt(i) != '0') {
                        break;
                    }
                    --numFractionDigits;
                }
                return numFractionDigits;
            }
        }

        public FixedDecimal(String n) {
            this(Double.parseDouble(n), getVisibleFractionCount(n));
        }

        private static int getVisibleFractionCount(String value) {
            value = value.trim();
            int decimalPos = value.indexOf('.') + 1;
            if (decimalPos == 0) {
                return 0;
            } else {
                return value.length() - decimalPos;
            }
        }

        public double getPluralOperand(Operand operand) {
            switch (operand) {
            case n:
                return source;
            case i:
                return integerValue;
            case f:
                return decimalDigits;
            case t:
                return decimalDigitsWithoutTrailingZeros;
            case v:
                return visibleDecimalDigitCount;
            case w:
                return visibleDecimalDigitCountWithoutTrailingZeros;
            default:
                return source;
            }
        }

        public static Operand getOperand(String t) {
            return Operand.valueOf(t);
        }

        public int compareTo(FixedDecimal other) {
            if (integerValue != other.integerValue) {
                return integerValue < other.integerValue ? -1 : 1;
            }
            if (source != other.source) {
                return source < other.source ? -1 : 1;
            }
            if (visibleDecimalDigitCount != other.visibleDecimalDigitCount) {
                return visibleDecimalDigitCount < other.visibleDecimalDigitCount ? -1 : 1;
            }
            long diff = decimalDigits - other.decimalDigits;
            if (diff != 0) {
                return diff < 0 ? -1 : 1;
            }
            return 0;
        }

        @Override
        public boolean equals(Object arg0) {
            if (arg0 == null) {
                return false;
            }
            if (arg0 == this) {
                return true;
            }
            if (!(arg0 instanceof FixedDecimal)) {
                return false;
            }
            FixedDecimal other = (FixedDecimal) arg0;
            return source == other.source && visibleDecimalDigitCount == other.visibleDecimalDigitCount
                    && decimalDigits == other.decimalDigits;
        }

        @Override
        public int hashCode() {
            return (int) (decimalDigits + 37 * (visibleDecimalDigitCount + (int) (37 * source)));
        }

        @Override
        public String toString() {
            return String.format("%." + visibleDecimalDigitCount + "f", source);
        }

        public boolean hasIntegerValue() {
            return hasIntegerValue;
        }

        @Override
        public int intValue() {
            return (int) integerValue;
        }

        @Override
        public long longValue() {
            return integerValue;
        }

        @Override
        public float floatValue() {
            return (float) source;
        }

        @Override
        public double doubleValue() {
            return isNegative ? -source : source;
        }

        public long getShiftedValue() {
            return integerValue * baseFactor + decimalDigits;
        }

        private void writeObject(ObjectOutputStream out) throws IOException {
            throw new NotSerializableException();
        }

        private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
            throw new NotSerializableException();
        }

        public boolean isNaN() {
            return Double.isNaN(source);
        }

        public boolean isInfinite() {
            return Double.isInfinite(source);
        }
    }

    /**
     * Selection parameter for either integer-only or decimal-only.
     */
    public enum SampleType {
        INTEGER,
        DECIMAL
    }

    public static class FixedDecimalRange {
        public final FixedDecimal start;
        public final FixedDecimal end;

        public FixedDecimalRange(FixedDecimal start, FixedDecimal end) {
            if (start.visibleDecimalDigitCount != end.visibleDecimalDigitCount) {
                throw new IllegalArgumentException(
                        "Ranges must have the same number of visible decimals: " + start + "~" + end);
            }
            this.start = start;
            this.end = end;
        }

        @Override
        public String toString() {
            return start + (end == start ? "" : "~" + end);
        }
    }

    /**
     * A list of NumberInfo that includes all values with the same
     * visibleFractionDigitCount.
     */
    public static class FixedDecimalSamples {
        public final SampleType             sampleType;
        public final Set<FixedDecimalRange> samples;
        public final boolean                bounded;

        /**
         * The samples must be immutable.
         * 
         * @param sampleType
         * @param samples
         */
        private FixedDecimalSamples(SampleType sampleType, Set<FixedDecimalRange> samples, boolean bounded) {
            super();
            this.sampleType = sampleType;
            this.samples = samples;
            this.bounded = bounded;
        }

        static FixedDecimalSamples parse(String source) {
            SampleType sampleType2;
            boolean bounded2 = true;
            boolean haveBound = false;
            Set<FixedDecimalRange> samples2 = new LinkedHashSet<FixedDecimalRange>();

            if (source.startsWith("integer")) {
                sampleType2 = SampleType.INTEGER;
            } else if (source.startsWith("decimal")) {
                sampleType2 = SampleType.DECIMAL;
            } else {
                throw new IllegalArgumentException("Samples must start with 'integer' or 'decimal'");
            }
            source = source.substring(7).trim(); // remove both

            for (String range : COMMA_SEPARATED.split(source)) {
                if (range.equals("…") || range.equals("...")) {
                    bounded2 = false;
                    haveBound = true;
                    continue;
                }
                if (haveBound) {
                    throw new IllegalArgumentException("Can only have … at the end of samples: " + range);
                }
                String[] rangeParts = TILDE_SEPARATED.split(range);
                switch (rangeParts.length) {
                case 1:
                    FixedDecimal sample = new FixedDecimal(rangeParts[0]);
                    checkDecimal(sampleType2, sample);
                    samples2.add(new FixedDecimalRange(sample, sample));
                    break;
                case 2:
                    FixedDecimal start = new FixedDecimal(rangeParts[0]);
                    FixedDecimal end = new FixedDecimal(rangeParts[1]);
                    checkDecimal(sampleType2, start);
                    checkDecimal(sampleType2, end);
                    samples2.add(new FixedDecimalRange(start, end));
                    break;
                default:
                    throw new IllegalArgumentException("Ill-formed number range: " + range);
                }
            }
            return new FixedDecimalSamples(sampleType2, Collections.unmodifiableSet(samples2), bounded2);
        }

        private static void checkDecimal(SampleType sampleType2, FixedDecimal sample) {
            if ((sampleType2 == SampleType.INTEGER) != (sample.getVisibleDecimalDigitCount() == 0)) {
                throw new IllegalArgumentException("Ill-formed number range: " + sample);
            }
        }

        public Set<Double> addSamples(Set<Double> result) {
            for (FixedDecimalRange item : samples) {
                long startDouble = item.start.getShiftedValue();
                long endDouble = item.end.getShiftedValue();

                for (long d = startDouble; d <= endDouble; d += 1) {
                    result.add(d / (double) item.start.baseFactor);
                }
            }
            return result;
        }

        @Override
        public String toString() {
            StringBuilder b = new StringBuilder("@").append(sampleType.toString().toLowerCase(Locale.ENGLISH));
            boolean first = true;
            for (FixedDecimalRange item : samples) {
                if (first) {
                    first = false;
                } else {
                    b.append(",");
                }
                b.append(' ').append(item);
            }
            if (!bounded) {
                b.append(", …");
            }
            return b.toString();
        }

        public Set<FixedDecimalRange> getSamples() {
            return samples;
        }

        public void getStartEndSamples(Set<FixedDecimal> target) {
            for (FixedDecimalRange item : samples) {
                target.add(item.start);
                target.add(item.end);
            }
        }
    }

    private interface Constraint extends Serializable {
        /**
         * Returns true if the number fulfills the constraint.
         * 
         * @param n
         *            the number to test, >= 0.
         */
        boolean isFulfilled(IFixedDecimal n);

        /**
         * Returns false if an unlimited number of values fulfills the
         * constraint.
         */
        boolean isLimited(SampleType sampleType);
    }

    static class SimpleTokenizer {
        static final HandleSet BREAK_AND_IGNORE = new HandleSet(0x09, 0x0a, 0x0c, 0x0d, 0x20, 0x20);
        static final HandleSet BREAK_AND_KEEP   = new HandleSet('!', '!', '%', '%', ',', ',', '.', '.', '=', '=');

        static String[] split(String source) {
            int last = -1;
            List<String> result = new ArrayList<String>();
            for (int i = 0; i < source.length(); ++i) {
                char ch = source.charAt(i);
                if (BREAK_AND_IGNORE.contains(ch)) {
                    if (last >= 0) {
                        result.add(source.substring(last, i));
                        last = -1;
                    }
                } else if (BREAK_AND_KEEP.contains(ch)) {
                    if (last >= 0) {
                        result.add(source.substring(last, i));
                    }
                    result.add(source.substring(i, i + 1));
                    last = -1;
                } else if (last < 0) {
                    last = i;
                }
            }
            if (last >= 0) {
                result.add(source.substring(last));
            }
            return result.toArray(new String[result.size()]);
        }
    }

    /**
     * syntax: condition : or_condition and_condition or_condition :
     * and_condition 'or' condition and_condition : relation relation 'and'
     * relation relation : in_relation within_relation in_relation : not? expr
     * not? in not? range within_relation : not? expr not? 'within' not? range
     * not : 'not' '!' expr : 'n' 'n' mod value mod : 'mod' '%' in : 'in' 'is'
     * '=' '≠' value : digit+ digit : 0|1|2|3|4|5|6|7|8|9 range : value'..'value
     */
    private static Constraint parseConstraint(String description) throws ParseException {

        Constraint result = null;
        String[] or_together = OR_SEPARATED.split(description);
        for (int i = 0; i < or_together.length; ++i) {
            Constraint andConstraint = null;
            String[] and_together = AND_SEPARATED.split(or_together[i]);
            for (int j = 0; j < and_together.length; ++j) {
                Constraint newConstraint = NO_CONSTRAINT;

                String condition = and_together[j].trim();
                String[] tokens = SimpleTokenizer.split(condition);

                int mod = 0;
                boolean inRange = true;
                boolean integersOnly = true;
                double lowBound = Long.MAX_VALUE;
                double highBound = Long.MIN_VALUE;
                long[] vals = null;

                int x = 0;
                String t = tokens[x++];
                boolean hackForCompatibility = false;
                Operand operand;
                try {
                    operand = FixedDecimal.getOperand(t);
                } catch (Exception e) {
                    throw unexpected(t, condition);
                }
                if (x < tokens.length) {
                    t = tokens[x++];
                    if ("mod".equals(t) || "%".equals(t)) {
                        mod = Integer.parseInt(tokens[x++]);
                        t = nextToken(tokens, x++, condition);
                    }
                    if ("not".equals(t)) {
                        inRange = !inRange;
                        t = nextToken(tokens, x++, condition);
                        if ("=".equals(t)) {
                            throw unexpected(t, condition);
                        }
                    } else if ("!".equals(t)) {
                        inRange = !inRange;
                        t = nextToken(tokens, x++, condition);
                        if (!"=".equals(t)) {
                            throw unexpected(t, condition);
                        }
                    }
                    if ("is".equals(t) || "in".equals(t) || "=".equals(t)) {
                        hackForCompatibility = "is".equals(t);
                        if (hackForCompatibility && !inRange) {
                            throw unexpected(t, condition);
                        }
                        t = nextToken(tokens, x++, condition);
                    } else if ("within".equals(t)) {
                        integersOnly = false;
                        t = nextToken(tokens, x++, condition);
                    } else {
                        throw unexpected(t, condition);
                    }
                    if ("not".equals(t)) {
                        if (!hackForCompatibility && !inRange) {
                            throw unexpected(t, condition);
                        }
                        inRange = !inRange;
                        t = nextToken(tokens, x++, condition);
                    }

                    List<Long> valueList = new ArrayList<Long>();

                    while (true) {
                        long low = Long.parseLong(t);
                        long high = low;
                        if (x < tokens.length) {
                            t = nextToken(tokens, x++, condition);
                            if (t.equals(".")) {
                                t = nextToken(tokens, x++, condition);
                                if (!t.equals(".")) {
                                    throw unexpected(t, condition);
                                }
                                t = nextToken(tokens, x++, condition);
                                high = Long.parseLong(t);
                                if (x < tokens.length) {
                                    t = nextToken(tokens, x++, condition);
                                    if (!t.equals(",")) {
                                        throw unexpected(t, condition);
                                    }
                                }
                            } else if (!t.equals(",")) {
                                // no separator, fail
                                throw unexpected(t, condition);
                            }
                        }
                        if (low > high) {
                            throw unexpected(low + "~" + high, condition);
                        } else if (mod != 0 && high >= mod) {
                            throw unexpected(high + ">mod=" + mod, condition);
                        }
                        valueList.add(low);
                        valueList.add(high);
                        lowBound = Math.min(lowBound, low);
                        highBound = Math.max(highBound, high);
                        if (x >= tokens.length) {
                            break;
                        }
                        t = nextToken(tokens, x++, condition);
                    }

                    if (t.equals(",")) {
                        throw unexpected(t, condition);
                    }

                    if (valueList.size() == 2) {
                        vals = null;
                    } else {
                        vals = new long[valueList.size()];
                        for (int k = 0; k < vals.length; ++k) {
                            vals[k] = valueList.get(k);
                        }
                    }

                    if (lowBound != highBound && hackForCompatibility && !inRange) {
                        throw unexpected("is not <range>", condition);
                    }

                    newConstraint = new RangeConstraint(mod, inRange, operand, integersOnly, lowBound, highBound, vals);
                }

                if (andConstraint == null) {
                    andConstraint = newConstraint;
                } else {
                    andConstraint = new AndConstraint(andConstraint, newConstraint);
                }
            }

            if (result == null) {
                result = andConstraint;
            } else {
                result = new OrConstraint(result, andConstraint);
            }
        }
        return result;
    }

    static final Pattern AT_SEPARATED     = Pattern.compile("\\s*\\Q\\E@\\s*");
    static final Pattern OR_SEPARATED     = Pattern.compile("\\s*or\\s*");
    static final Pattern AND_SEPARATED    = Pattern.compile("\\s*and\\s*");
    static final Pattern COMMA_SEPARATED  = Pattern.compile("\\s*,\\s*");
    static final Pattern DOTDOT_SEPARATED = Pattern.compile("\\s*\\Q..\\E\\s*");
    static final Pattern TILDE_SEPARATED  = Pattern.compile("\\s*~\\s*");
    static final Pattern SEMI_SEPARATED   = Pattern.compile("\\s*;\\s*");

    private static ParseException unexpected(String token, String context) {
        return new ParseException("unexpected token '" + token + "' in '" + context + "'", -1);
    }

    private static String nextToken(String[] tokens, int x, String context) throws ParseException {
        if (x < tokens.length) {
            return tokens[x];
        }
        throw new ParseException("missing token at end of '" + context + "'", -1);
    }

    private static Rule parseRule(String description) throws ParseException {
        if (description.length() == 0) {
            return DEFAULT_RULE;
        }

        description = description.toLowerCase(Locale.ENGLISH);

        int x = description.indexOf(':');
        if (x == -1) {
            throw new ParseException("missing ':' in rule description '" + description + "'", 0);
        }

        // String keyword = description.substring(0, x).trim();
        String keyword = description.substring(description.lastIndexOf("-") + 1, x).trim();
        if (!isValidKeyword(keyword)) {
            throw new ParseException("keyword '" + keyword + " is not valid", 0);
        }

        description = description.substring(x + 1).trim();
        String[] constraintOrSamples = AT_SEPARATED.split(description);
        boolean sampleFailure = false;
        FixedDecimalSamples integerSamples = null, decimalSamples = null;
        switch (constraintOrSamples.length) {
        case 1:
            break;
        case 2:
            integerSamples = FixedDecimalSamples.parse(constraintOrSamples[1]);
            if (integerSamples.sampleType == SampleType.DECIMAL) {
                decimalSamples = integerSamples;
                integerSamples = null;
            }
            break;
        case 3:
            integerSamples = FixedDecimalSamples.parse(constraintOrSamples[1]);
            decimalSamples = FixedDecimalSamples.parse(constraintOrSamples[2]);
            if (integerSamples.sampleType != SampleType.INTEGER || decimalSamples.sampleType != SampleType.DECIMAL) {
                throw new IllegalArgumentException("Must have @integer then @decimal in " + description);
            }
            break;
        default:
            throw new IllegalArgumentException("Too many samples in " + description);
        }
        // if (sampleFailure) {
        // throw new IllegalArgumentException("Ill-formed samples—'@' characters.");
        // }

        boolean isOther = keyword.equals("other");
        if (isOther != (constraintOrSamples[0].length() == 0)) {
            throw new IllegalArgumentException("The keyword 'other' must have no constraints, just samples.");
        }

        Constraint constraint;
        if (isOther) {
            constraint = NO_CONSTRAINT;
        } else {
            constraint = parseConstraint(constraintOrSamples[0]);
        }
        return new Rule(keyword, constraint, integerSamples, decimalSamples);
    }

    /**
     * Syntax: rules : rule rule ';' rules
     */
    private static RuleList parseRuleChain(String description) throws ParseException {
        RuleList result = new RuleList();
        if (description.endsWith(";")) {
            description = description.substring(0, description.length() - 1);
        }
        String[] rules = SEMI_SEPARATED.split(description);
        for (int i = 0; i < rules.length; ++i) {
            Rule rule = parseRule(rules[i].trim());
            result.hasExplicitBoundingInfo |= rule.integerSamples != null || rule.decimalSamples != null;
            result.addRule(rule);
        }
        return result.finish();
    }

    private static RuleList parseRuleChain(Map<String, String> rules) throws ParseException {
        RuleList result = new RuleList();
        for (String keyWord : rules.keySet()) {
            Rule rule = parseRule((keyWord + ":" + rules.get(keyWord)).trim());
            result.hasExplicitBoundingInfo |= rule.integerSamples != null || rule.decimalSamples != null;
            result.addRule(rule);
        }
        return result.finish();
    }

    /**
     * An implementation of Constraint representing a modulus
     */
    private static class RangeConstraint implements Constraint, Serializable {
        private static final long serialVersionUID = 1;

        private final int         mod;
        private final boolean     inRange;
        private final boolean     integersOnly;
        private final double      lowerBound;
        private final double      upperBound;
        private final long[]      range_list;
        private final Operand     operand;

        RangeConstraint(int mod, boolean inRange, Operand operand, boolean integersOnly, double lowBound,
                double highBound, long[] vals) {
            this.mod = mod;
            this.inRange = inRange;
            this.integersOnly = integersOnly;
            this.lowerBound = lowBound;
            this.upperBound = highBound;
            this.range_list = vals;
            this.operand = operand;
        }

        public boolean isFulfilled(IFixedDecimal number) {
            double n = number.getPluralOperand(operand);
            if ((integersOnly && (n - (long) n) != 0.0
                    || operand == Operand.j && number.getPluralOperand(Operand.v) != 0)) {
                return !inRange;
            }
            if (mod != 0) {
                n = n % mod;
            }
            boolean test = n >= lowerBound && n <= upperBound;
            if (test && range_list != null) {
                test = false;
                for (int i = 0; !test && i < range_list.length; i += 2) {
                    test = n >= range_list[i] && n <= range_list[i + 1];
                }
            }
            return inRange == test;
        }

        public boolean isLimited(SampleType sampleType) {
            boolean valueIsZero = lowerBound == upperBound && lowerBound == 0d;
            boolean hasDecimals = (operand == Operand.v || operand == Operand.w || operand == Operand.f
                    || operand == Operand.t) && inRange != valueIsZero;
            switch (sampleType) {
            case INTEGER:
                return hasDecimals
                        || (operand == Operand.n || operand == Operand.i || operand == Operand.j) && mod == 0
                                && inRange;

            case DECIMAL:
                return (!hasDecimals || operand == Operand.n || operand == Operand.j)
                        && (integersOnly || lowerBound == upperBound) && mod == 0 && inRange;
            }
            return false;
        }

        @Override
        public String toString() {
            StringBuilder result = new StringBuilder();
            result.append(operand);
            if (mod != 0) {
                result.append(" % ").append(mod);
            }
            boolean isList = lowerBound != upperBound;
            result.append(!isList ? (inRange ? " = " : " != ")
                    : integersOnly ? (inRange ? " = " : " != ") : (inRange ? " within " : " not within "));
            if (range_list != null) {
                for (int i = 0; i < range_list.length; i += 2) {
                    addRange(result, range_list[i], range_list[i + 1], i != 0);
                }
            } else {
                addRange(result, lowerBound, upperBound, false);
            }
            return result.toString();
        }
    }

    private static void addRange(StringBuilder result, double lb, double ub, boolean addSeparator) {
        if (addSeparator) {
            result.append(",");
        }
        if (lb == ub) {
            result.append(format(lb));
        } else {
            result.append(format(lb) + ".." + format(ub));
        }
    }

    private static String format(double lb) {
        long lbi = (long) lb;
        return lb == lbi ? String.valueOf(lbi) : String.valueOf(lb);
    }

    private static abstract class BinaryConstraint implements Constraint, Serializable {
        private static final long  serialVersionUID = 1;
        protected final Constraint a;
        protected final Constraint b;

        protected BinaryConstraint(Constraint a, Constraint b) {
            this.a = a;
            this.b = b;
        }
    }

    private static class AndConstraint extends BinaryConstraint {
        private static final long serialVersionUID = 7766999779862263523L;

        AndConstraint(Constraint a, Constraint b) {
            super(a, b);
        }

        public boolean isFulfilled(IFixedDecimal n) {
            return a.isFulfilled(n) && b.isFulfilled(n);
        }

        public boolean isLimited(SampleType sampleType) {
            return a.isLimited(sampleType) || b.isLimited(sampleType);
        }

        @Override
        public String toString() {
            return a.toString() + " and " + b.toString();
        }
    }

    private static class OrConstraint extends BinaryConstraint {
        private static final long serialVersionUID = 1405488568664762222L;

        OrConstraint(Constraint a, Constraint b) {
            super(a, b);
        }

        public boolean isFulfilled(IFixedDecimal n) {
            return a.isFulfilled(n) || b.isFulfilled(n);
        }

        public boolean isLimited(SampleType sampleType) {
            return a.isLimited(sampleType) && b.isLimited(sampleType);
        }

        @Override
        public String toString() {
            return a.toString() + " or " + b.toString();
        }
    }

    /**
     * Implementation of Rule that uses a constraint. Provides 'and' and 'or' to
     * combine constraints. Immutable.
     */
    private static class Rule implements Serializable {
        private static final long         serialVersionUID = 1;
        private final String              keyword;
        private final Constraint          constraint;
        private final FixedDecimalSamples integerSamples;
        private final FixedDecimalSamples decimalSamples;

        public Rule(String keyword, Constraint constraint, FixedDecimalSamples integerSamples,
                FixedDecimalSamples decimalSamples) {
            this.keyword = keyword;
            this.constraint = constraint;
            this.integerSamples = integerSamples;
            this.decimalSamples = decimalSamples;
        }

        @SuppressWarnings("unused")
        public Rule and(Constraint c) {
            return new Rule(keyword, new AndConstraint(constraint, c), integerSamples, decimalSamples);
        }

        @SuppressWarnings("unused")
        public Rule or(Constraint c) {
            return new Rule(keyword, new OrConstraint(constraint, c), integerSamples, decimalSamples);
        }

        public String getKeyword() {
            return keyword;
        }

        public boolean appliesTo(IFixedDecimal n) {
            return constraint.isFulfilled(n);
        }

        @Override
        public String toString() {
            return keyword + ": " + constraint.toString()
                    + (integerSamples == null ? "" : " " + integerSamples.toString())
                    + (decimalSamples == null ? "" : " " + decimalSamples.toString());
        }

        @Override
        public boolean equals(Object arg0) {
            if (arg0 == null) {
                return false;
            }
            if (arg0 == this) {
                return true;
            }
            if (!(arg0 instanceof FixedDecimal)) {
                return false;
            }
            FixedDecimal other = (FixedDecimal) arg0;
            return this.hashCode() == other.hashCode();
        }

        @Override
        public int hashCode() {
            return keyword.hashCode() ^ constraint.hashCode();
        }

        public String getConstraint() {
            return constraint.toString();
        }
    }

    private static class RuleList implements Serializable {
        @SuppressWarnings("unused")
        private boolean           hasExplicitBoundingInfo = false;
        private static final long serialVersionUID        = 1;
        private final List<Rule>  rules                   = new ArrayList<Rule>();

        public RuleList addRule(Rule nextRule) {
            String keyword = nextRule.getKeyword();
            for (Rule rule : rules) {
                if (keyword.equals(rule.getKeyword())) {
                    throw new IllegalArgumentException("Duplicate keyword: " + keyword);
                }
            }
            rules.add(nextRule);
            return this;
        }

        public RuleList finish() throws ParseException {
            // make sure that 'other' is present, and at the end.
            Rule otherRule = null;
            for (Iterator<Rule> it = rules.iterator(); it.hasNext();) {
                Rule rule = it.next();
                if ("other".equals(rule.getKeyword())) {
                    otherRule = rule;
                    it.remove();
                }
            }
            if (otherRule == null) {
                otherRule = parseRule("other:");
            }
            rules.add(otherRule);
            return this;
        }

        private Rule selectRule(IFixedDecimal n) {
            for (Rule rule : rules) {
                if (rule.appliesTo(n)) {
                    return rule;
                }
            }
            return null;
        }

        public String select(IFixedDecimal n) {
            if (n.isInfinite() || n.isNaN()) {
                return KEYWORD_OTHER;
            }
            Rule r = selectRule(n);
            if (null != r) {
                return r.getKeyword();
            }
            return null;
        }

        public Set<String> getKeywords() {
            Set<String> result = new LinkedHashSet<String>();
            for (Rule rule : rules) {
                result.add(rule.getKeyword());
            }
            return result;
        }

        public String getRules(String keyword) {
            for (Rule rule : rules) {
                if (rule.getKeyword().equals(keyword)) {
                    return rule.getConstraint();
                }
            }
            return null;
        }

    }

    /**
     * Checks whether a token is a valid keyword.
     * 
     * @param token
     *            the token to be checked
     * @return true if the token is a valid keyword.
     */
    private static boolean isValidKeyword(String token) {
        return KEYWORD_LIST.contains(token);
    }

    private PluralRules(RuleList rules) {
        this.rules = rules;
        this.keywords = Collections.unmodifiableSet(rules.getKeywords());
    }

    /**
     * Given a number, returns the keyword of the first rule that applies to the
     * number.
     * 
     * @param number
     *            The number for which the rule has to be determined.
     * @return The keyword of the selected rule.
     */
    public String select(double number) {
        return rules.select(new FixedDecimal(number));
    }

    /**
     * @deprecated
     * @param number
     * @return
     */
    @Deprecated
    public String select(FixedDecimal number) {
        return rules.select(number);
    }

    public String getRules(String keyword) {
        return rules.getRules(keyword);
    }

    public static PluralRules forLocale(Locale locale, PluralType type) {
        JSONObject pluralRules = null;
        try {
            JSONObject pluralPattern = new PatternService().getPatternsByCategory(locale.toLanguageTag(),
                    PatternCategory.PLURALS.toString());
            pluralRules = (JSONObject) pluralPattern.get(PatternKeys.PLURALRULES);
        } catch (NullPointerException e) {
            logger.info("Lack plural pattern!");
        }
        return getInstance(pluralRules.toMap().entrySet().stream()
        	     .collect(Collectors.toMap(Map.Entry::getKey, e -> (String)e.getValue())));
    }

}
