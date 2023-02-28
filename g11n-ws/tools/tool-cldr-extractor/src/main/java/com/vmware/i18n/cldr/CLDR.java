/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.i18n.cldr;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.vmware.i18n.utils.CommonUtil;

/**
 * This class is used for parsing locale
 * @author zhanghao
 */
public class CLDR {

    private String language;
    private String script;
    private String territory;
    private String variant;
    private String unicodeLocaleExtensions;
    private String unicodeLocaleExtensionsRaw = "";
    private String sep = "-";

    public CLDR(String locale) {
        this.init(locale);
    }

    private void init(String locale) {
        String[] subtags = this.coreSubtags(locale);
        if (subtags.length == 5) {
            this.unicodeLocaleExtensions = subtags[4];// get the last one
            this.unicodeLocaleExtensionsRaw = sep + "u" + sep + unicodeLocaleExtensions;
        }
        this.language = subtags[0];
        this.script = subtags[1];
        this.territory = subtags[2];
        this.variant = subtags[3];
    }

    private String[] coreSubtags(String locale) {
        String unicodeLanguageId;
        String[] subtags = new String[5];
        locale = locale.replaceFirst("_", "-");
        String aux[] = locale.split("-u-");
        if (aux.length > 1 && !CommonUtil.isEmpty(aux[1])) {
            String[] subAux = aux[1].toString().split("-t-");
            locale = aux[0].toString()
                    + (subAux.length > 2 && CommonUtil.isEmpty(subAux[1]) ? "-t-" + subAux[1] : "");
            // subtags[4] unicodeLocaleExtensions
            subtags[4] = subAux[0];
        }

        // normalize transformed extensions. Currently, skipped.
        // subtags[x] = locale.split("-t-")[1];
        unicodeLanguageId = locale.split("-t-")[0];
        aux = this.regex(unicodeLanguageId);
        if (aux.length == 0) {
            String result[] = { "und", "Zzzz", "ZZ" };
            return result;
        }
        // subtags[0]： language
        if (aux.length > 10 && !CommonUtil.isEmpty(aux[10])) {
            subtags[0] = aux[10].toString();
        } else if (aux.length > 2 && !CommonUtil.isEmpty(aux[2])) {
            subtags[0] = aux[2].toString();
        } else {
            subtags[0] = "und";
        }

        // subtags[1]： script
        if (aux.length > 4 && !CommonUtil.isEmpty(aux[4])) {
            subtags[1] = aux[4].toString();
        } else {
            subtags[1] = "Zzzz";
        }

        // subtags[2] territory
        if (aux.length > 6 && !CommonUtil.isEmpty(aux[6])) {
            subtags[2] = aux[6].toString();
        } else {
            subtags[2] = "ZZ";
        }

        // subtags[3] variant
        if (aux.length > 7 && !CommonUtil.isEmpty(aux[7])) {
            subtags[3] = aux[7].toString().substring(1);// remove leading "-"
        }

        // 0: language 1: script 2: territory (aka region) 3: variant 4: unicodeLocaleExtensions
        return subtags;
    }

    /**
     * unicode_language_id = "root" | unicode_language_subtag (sep unicode_script_subtag)? (sep
     * unicode_region_subtag)? (sep unicode_variant_subtag)*; Although unicode_language_subtag =
     * alpha{2,8}, I'm using alpha{2,3}. Because, there's no language on CLDR lengthier than 3
     * @param unicodeLanguageId
     * @return
     */
    private String[] regex(String unicodeLanguageId) {
        String regex = "(([a-z]{2,3})(-([A-Z][a-z]{3}))?(-([A-Z]{2}|[0-9]{3}))?)((-([a-zA-Z0-9]{5,8}|[0-9][a-zA-Z0-9]{3}))*)|(root)";
        Matcher matcher = Pattern.compile(regex).matcher(unicodeLanguageId);
        String arr[] = new String[11];
        int i = 0;
        while (matcher.find()) {
            for (int j = 0; j < matcher.groupCount(); j++) {
                arr[i] = matcher.group(j);
                i++;
            }
        }
        return arr;
    }

    public String getLanguage() {
        return language;
    }

    public String getScript() {
        return script;
    }

    public String getTerritory() {
        return territory;
    }

    public String getVariant() {
        return variant;
    }

    public String getUnicodeLocaleExtensions() {
        return unicodeLocaleExtensions;
    }

    public String getUnicodeLocaleExtensionsRaw() {
        return unicodeLocaleExtensionsRaw;
    }

}
