/*
 * Copyright 2019-2025 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.l2.text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.json.JSONObject;

public class RegionDateFormat extends DateFormat {

    private String     pattern;
    private JSONObject formatData;

    private String     language;
    private String     region;

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public RegionDateFormat(String pattern, JSONObject formatData, String language, String region) {
        // TODO Auto-generated constructor stub
        this.pattern = pattern;
        this.formatData = formatData;
        this.language = language;
        this.region = region;
    }

    public String format(Date date, String timeZoneStr) {
        StringBuffer text = new StringBuffer();
        // recalculate date using timeZone
        Calendar cal;

        if (null != timeZoneStr && !"".equalsIgnoreCase(timeZoneStr)) {
            TimeZone timeZone = TimeZone.getTimeZone(timeZoneStr);
            cal = Calendar.getInstance(timeZone);
        } else {
            TimeZone timeZone = TimeZone.getDefault();
            cal = Calendar.getInstance(timeZone);
        }
        cal.setTime(date);
        // validate if pattern is syntax
        List<Object> items = getPatternItems(pattern);
        for (Object item : items) {
            if (item instanceof PatternItem) {
                PatternItem patternItem = (PatternItem) item;
                text.append(patternItem.getParser(formatData).parse(patternItem, cal));
            } else {
                text.append(item);
            }
        }
        return text.toString();
    }

    public List<Object> getPatternItems(String pattern) {
        boolean isPrevQuote = false;
        boolean inQuote = false;
        StringBuilder text = new StringBuilder();
        char itemType = 0; // 0 for string literal, otherwise date/time pattern
                           // character
        int itemLength = 1;
        List<Object> items = new ArrayList<Object>();
        for (int i = 0; i < pattern.length(); i++) {
            char ch = pattern.charAt(i);
            if (ch == '\'') {
                if (isPrevQuote) {
                    text.append('\'');
                    isPrevQuote = false;
                } else {
                    isPrevQuote = true;
                    if (itemType != 0) {
                        items.add(new PatternItem(itemType, itemLength));
                        itemType = 0;
                    }
                }
                inQuote = !inQuote;
            } else {
                isPrevQuote = false;
                if (inQuote) {
                    text.append(ch);
                } else {
                    if (isSyntaxChar(ch)) {
                        // a date/time pattern character
                        if (ch == itemType) {
                            itemLength++;
                        } else {
                            if (itemType == 0) {
                                if (text.length() > 0) {
                                    items.add(text.toString());
                                    text.setLength(0);
                                }
                            } else {
                                items.add(new PatternItem(itemType, itemLength));
                            }
                            itemType = ch;
                            itemLength = 1;
                        }
                    } else {
                        // a string literal
                        if (itemType != 0) {
                            items.add(new PatternItem(itemType, itemLength));
                            itemType = 0;
                        }
                        text.append(ch);
                    }
                }
            }
        }
        // handle last item
        if (itemType == 0) {
            if (text.length() > 0) {
                items.add(text.toString());
                text.setLength(0);
            }
        } else {
            items.add(new PatternItem(itemType, itemLength));
        }
        return items;
    }

    /**
     * Check the char if syntax.
     */
    private static final boolean[] PATTERN_CHAR_IS_SYNTAX = {
            //
            false, false, false, false, false, false, false, false,
            //
            false, false, false, false, false, false, false, false,
            //
            false, false, false, false, false, false, false, false,
            //
            false, false, false, false, false, false, false, false,
            // ! " # $ % & '
            false, false, false, false, false, false, false, false,
            // ( ) * + , - . /
            false, false, false, false, false, false, false, false,
            // 0 1 2 3 4 5 6 7
            false, false, false, false, false, false, false, false,
            // 8 9 : ; < = > ?
            false, false, false, false, false, false, false, false,
            // @ A B C D E F G
            false, true, true, true, true, true, true, true,
            // H I J K L M N O
            true, true, true, true, true, true, true, true,
            // P Q R S T U V W
            true, true, true, true, true, true, true, true,
            // X Y Z [ \ ] ^ _
            true, true, true, false, false, false, false, false,
            // ` a b c d e f g
            false, true, true, true, true, true, true, true,
            // h i j k l m n o
            true, true, true, true, true, true, true, true,
            // p q r s t u v w
            true, true, true, true, true, true, true, true,
            // x y z { | } ~
            true, true, true, false, false, false, false, false, };

    /**
     * Tell if a character can be used to define a field in a format string.
     */
    private static boolean isSyntaxChar(char ch) {
        return ch < PATTERN_CHAR_IS_SYNTAX.length ? PATTERN_CHAR_IS_SYNTAX[ch & 0xff] : false;
    }
}
