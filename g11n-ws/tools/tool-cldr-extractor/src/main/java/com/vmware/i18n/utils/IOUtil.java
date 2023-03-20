/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.i18n.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.jar.JarFile;
import java.util.zip.ZipFile;

/**
 * IO utility class for IO operation
 *
 */
public class IOUtil {

    /**
     * close Writer
     * @param writer
     */
    public static void closeWriter(Writer writer) {
        try {
            if (writer != null) {
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * close Reader
     * @param reader
     */
    public static void closeReader(Reader reader) {
        try {
            if (reader != null) {
                reader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * close OutputStream
     * @param out
     */
    public static void closeOutputStream(OutputStream out) {
        try {
            if (out != null) {
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * close InputStream
     * @param input
     */
    public static void closeInputStream(InputStream input) {
        try {
            if (input != null) {
                input.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * close JarFile
     * @param jarFile
     */
    public static void closeJarFile(JarFile jarFile) {
        try {
            if (jarFile != null) {
                jarFile.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * close ZipFile
     * @param zf
     */
    public static void closeZipFile(ZipFile zf) {
        try {
            if (zf != null) {
                zf.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
