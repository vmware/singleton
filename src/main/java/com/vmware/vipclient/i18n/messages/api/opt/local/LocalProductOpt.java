/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.api.opt.local;

import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.messages.api.opt.ProductOpt;
import com.vmware.vipclient.i18n.messages.dto.BaseDTO;
import com.vmware.vipclient.i18n.util.ConstantsKeys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class LocalProductOpt implements ProductOpt {
    private Logger logger = LoggerFactory.getLogger(LocalLocaleOpt.class);
    private static final String BUNDLE_PREFIX = "messages_";
    private static final String BUNDLE_SUFFIX = ".json";

    private BaseDTO dto = null;

    public LocalProductOpt(BaseDTO dto) {
        this.dto = dto;
    }

    public List<String> getSupportedLocales() {
        List<String> supportedLocales = new ArrayList<String>();
        try {

            Path path = Paths.get(VIPCfg.getInstance().getOfflineResourcesBaseUrl());

            URI uri = Thread.currentThread().getContextClassLoader().
                    getResource(path.toString()).toURI();

            if (uri.getScheme().equals("jar")) {
                try (FileSystem fileSystem = FileSystems.newFileSystem(uri, Collections.<String, Object>emptyMap())) {
                    path = fileSystem.getPath(path.toString());
                    getSupportedLocales(path, supportedLocales);
                }
            } else {
                path = Paths.get(uri);
                getSupportedLocales(path, supportedLocales);
            }

        } catch (Exception e) {
            logger.debug(e.getMessage());
        }
        return supportedLocales;
    }

    public List<String> getComponents() {
        List<String> components = new ArrayList<String>();
        try {

            Path path = Paths.get(VIPCfg.getInstance().getOfflineResourcesBaseUrl());

            URI uri = Thread.currentThread().getContextClassLoader().
                    getResource(path.toString()).toURI();

            if (uri.getScheme().equals("jar")) {
                try (FileSystem fileSystem = FileSystems.newFileSystem(uri, Collections.<String, Object>emptyMap())) {
                    path = fileSystem.getPath(path.toString());
                    getComponents(path, components);
                }
            } else {
                path = Paths.get(uri);
                getComponents(path, components);
            }

        } catch (Exception e) {
            logger.debug(e.getMessage());
        }
        return components;
    }

    private void getSupportedLocales(Path path, List<String> supportedLocales) throws IOException {
        try (Stream<Path> listOfFiles = Files.walk(path).filter(p -> p.toFile().isFile())) {
            listOfFiles.map(file -> {
                String fileName = file.getFileName().toString();
                if(fileName.startsWith(BUNDLE_PREFIX) && fileName.endsWith(BUNDLE_SUFFIX)) {
                    return fileName.substring(BUNDLE_PREFIX.length(), fileName.indexOf('.'));
                }
                return "";
            }).forEach(language -> {
                if(language != null && !language.isEmpty() && !ConstantsKeys.SOURCE.equalsIgnoreCase(language)) {
                    supportedLocales.add(language);
                }
            });
        }
    }

    private void getComponents(Path path, List<String> components) throws IOException {
        try (Stream<Path> listOfFiles = Files.walk(path).filter(p -> p.toFile().isDirectory())) {
            listOfFiles.map(file -> {
                return file.getFileName().toString();
            }).forEach(component -> {
                if(component != null && !component.isEmpty()) {
                    components.add(component);
                }
            });
        }
    }
}
