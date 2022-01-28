/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.common.l10n.source.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.jar.JarFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * IO utility class for IO operation
 *
 */
public class IOUtil {
	private static Logger logger = LoggerFactory.getLogger(IOUtil.class);

    /**
     * close Writer
     * 
     * @param writer
     */
    public static void closeWriter(Writer writer) {
        try {
            if (writer != null) {
                writer.close();
            }
        } catch (IOException e) {
        	logger.error(e.getMessage(), e);
        }
    }

    /**
     * close Reader
     * 
     * @param reader
     */
    public static void closeReader(Reader reader) {
        try {
            if (reader != null) {
                reader.close();
            }
        } catch (IOException e) {
        	logger.error(e.getMessage(), e);
        }
    }

    /**
     * close OutputStream
     * 
     * @param out
     */
    public static void closeOutputStream(OutputStream out) {
        try {
            if (out != null) {
                out.close();
            }
        } catch (IOException e) {
        	logger.error(e.getMessage(), e);
        }
    }

    /**
     * close InputStream
     * 
     * @param input
     */
    public static void closeInputStream(InputStream input) {
        try {
            if (input != null) {
                input.close();
            }
        } catch (IOException e) {
        	logger.error(e.getMessage(), e);
        }
    }

    /**
     * close JarFile
     * 
     * @param jarFile
     */
    public static void closeJarFile(JarFile jarFile) {
        try {
            if (jarFile != null) {
                jarFile.close();
            }
        } catch (IOException e) {
        	logger.error(e.getMessage(), e);
        }
    }

}
