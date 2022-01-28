/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.common.utils;

import java.io.StringWriter;

/**
 * This class is used extend the StringWriter supported pretty JSON formatter
 * 
 */
public class JsonPrettyWriter extends StringWriter {
    private int indent = 0;

    /**
     * write the char
     *
     */
    @Override
    public void write(int c) {
        if (((char) c) == '[' || ((char) c) == '{') {
            super.write(c);
            super.write('\n');
            indent++;
            writeIndentation();
        } else if (((char) c) == ',') {
            super.write(c);
            super.write('\n');
            writeIndentation();
        } else if (((char) c) == ']' || ((char) c) == '}') {
            super.write('\n');
            indent--;
            writeIndentation();
            super.write(c);
        } else {
            super.write(c);
        }
    }

    /**
     * write the indentation
     *
     */
    private void writeIndentation() {
        for (int i = 0; i < indent; i++) {
            super.write("   ");
        }
    }
}
