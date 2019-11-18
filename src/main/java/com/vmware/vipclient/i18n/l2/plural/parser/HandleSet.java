/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.l2.plural.parser;

public class HandleSet {

    private int              len;
    private int[]            list;
    private static final int HIGH = 0x110000;

    /**
     * Quickly constructs a set from a set of ranges &lt;s0, e0, s1, e1, s2, e2,
     * ...
     * 
     * @param pairs
     *            pairs of character
     */
    public HandleSet(int... pairs) {
        if ((pairs.length & 1) != 0) {
            throw new IllegalArgumentException("Must have even number of integers");
        }
        list = new int[pairs.length + 1];
        len = list.length;
        int last = -1;
        int i = 0;
        while (i < pairs.length) {
            int start = pairs[i];
            if (last >= start) {
                throw new IllegalArgumentException("Must be monotonically increasing.");
            }
            list[i++] = last = start;
            int end = pairs[i] + 1;
            if (last >= end) {
                throw new IllegalArgumentException("Must be monotonically increasing.");
            }
            list[i++] = last = end;
        }
        list[i] = HIGH;
    }

    /**
     * Returns true if this set contains the given character.
     * 
     * @param c
     *            character to be checked for containment
     * @return true if the test condition is met
     */
    public boolean contains(int c) {
        int i = findCodePoint(c);
        return ((i & 1) != 0);
    }

    /**
     * Returns the smallest value i such that c < list[i].
     * 
     * @param c
     *            a character in the range MIN_VALUE..MAX_VALUE inclusive
     * @return the smallest integer i in the range 0..len-1, inclusive, such
     *         that c < list[i]
     */
    private final int findCodePoint(int c) {

        if (c < list[0])
            return 0;
        if (len >= 2 && c >= list[len - 2])
            return len - 1;
        int lo = 0;
        int hi = len - 1;
        for (;;) {
            int i = (lo + hi) >>> 1;
            if (i == lo)
                return hi;
            if (c < list[i]) {
                hi = i;
            } else {
                lo = i;
            }
        }
    }

    /**
     * Span a string using this UnicodeSet.
     * 
     * @param s
     *            The string to be spanned
     * @param start
     *            The start index that the span begins
     * @return the string index which ends the span
     */
    public int span(CharSequence s, int start) {
        int end = s.length();
        if (start < 0) {
            start = 0;
        } else if (start >= end) {
            return end;
        }
        return 0;
    }

}
