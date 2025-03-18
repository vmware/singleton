/*
 * Copyright 2019-2025 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.l2.text;

import java.util.Calendar;

import org.json.JSONObject;

import com.vmware.vipclient.i18n.l2.text.parser.AMPMPatternParser;
import com.vmware.vipclient.i18n.l2.text.parser.DatePatternParser;
import com.vmware.vipclient.i18n.l2.text.parser.DateStrPatternParser;
import com.vmware.vipclient.i18n.l2.text.parser.DefaultPatternParser;
import com.vmware.vipclient.i18n.l2.text.parser.EraPatternParser;
import com.vmware.vipclient.i18n.l2.text.parser.PatternParser;
import com.vmware.vipclient.i18n.l2.text.parser.TimeZonePatternParser;

public class PatternItem {
    private char type;
    private int  length;

    public PatternItem(char type, int length) {
        this.type = type;
        this.length = length;
    }

    public int getLength() {
        return length;
    }

    public char getType() {
        return type;
    }

    // Map pattern character to index
    private static final int[] PATTERN_CHAR_TO_INDEX           = { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1,
            //
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            // ! " # $ % & ' ( ) * + , - . /
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            // 0 1 2 3 4 5 6 7 8 9 : ; < = > ?
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            // @ A B C D E F G H I J K L M N O
            -1, -1, -1, -1, -1, 8, -1, 11, 4, -1, -1, -1, 2, 1, -1, -1,
            // P Q R S T U V W X Y Z [ \ ] ^ _
            -1, -1, -1, 9, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            // ` a b c d e f g h i j k l m n o
            -1, 10, -1, -1, 3, -1, -1, -1, 5, -1, -1, -1, -1, 6, -1, -1,
            // p q r s t u v w x y z { | } ~
            -1, -1, -1, 7, -1, -1, -1, -1, -1, 0, 12, -1, -1, -1, -1, -1, };

    // Map pattern character index to Calendar field number
    private static final int[] PATTERN_INDEX_TO_CALENDAR_FIELD = {
            /* yML */Calendar.YEAR, Calendar.MONTH, Calendar.MONTH,
            /* dHh */Calendar.DAY_OF_MONTH, Calendar.HOUR_OF_DAY, Calendar.HOUR,
            /* msE */Calendar.MINUTE, Calendar.SECOND, Calendar.DAY_OF_WEEK,
            /* S */Calendar.MILLISECOND, Calendar.AM_PM, Calendar.ERA,
            /* z */Calendar.ZONE_OFFSET };

    private int getIndexFromChar() {
        return type < PATTERN_CHAR_TO_INDEX.length ? PATTERN_CHAR_TO_INDEX[type & 0xff] : -1;
    }

    public Integer patternCharToCalendarField() {
        int patternCharIndex = getIndexFromChar();
        if (patternCharIndex != -1) {
            return PATTERN_INDEX_TO_CALENDAR_FIELD[patternCharIndex];
        }
        return null;
    }

    protected PatternParser getParser(JSONObject formatData) {
        PatternParser parser = null;
        switch (getIndexFromChar()) {
        case 0: {
            // yyyy,yy,y
            boolean trim = false;
            if (length == 2) {
                trim = true;
            }
            parser = new DatePatternParser(0, trim, true);
            break;
        }
        case 1: {
            // MMMM,MMM,MM,M
            if (length > 2) {
                parser = new DateStrPatternParser((JSONObject) formatData.get("monthsFormat"));
            } else {
                parser = new DatePatternParser(1);
            }
            break;
        }
        case 2: {
            // LLLL
            parser = new DateStrPatternParser((JSONObject) formatData.get("monthsFormat"));
            break;
        }
        case 5: {
            // hh,h
            parser = new DatePatternParser(-12);
            break;
        }
        case 8: {
            // EEEE,EEE
            parser = new DateStrPatternParser((JSONObject) formatData);// .get("daysFormat")
            break;
        }
        case 10: {
            // a
            parser = new AMPMPatternParser((JSONObject) formatData.get("dayPeriodsFormat"));
            break;

        }
        case 11: {
            // GGGG,GGG,GG,G
            if (length == 4) {
                parser = new EraPatternParser((JSONObject) formatData.get("eras"), true);
            } else {
                parser = new EraPatternParser((JSONObject) formatData.get("eras"));
            }
            break;
        }
        case 12: {
            parser = new TimeZonePatternParser();
            break;
        }
        case 3:
            // d
        case 4:
            // H
        case 6:
            // m
        case 7:
            // s
        case 9: {
            // S
            parser = new DatePatternParser();
            break;
        }
        default: {
            parser = new DefaultPatternParser();
        }
        }
        return parser;
    }
}
