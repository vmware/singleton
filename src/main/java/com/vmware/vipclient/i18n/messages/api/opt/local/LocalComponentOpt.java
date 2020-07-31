/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.api.opt.local;

import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.messages.api.opt.ComponentOpt;
import com.vmware.vipclient.i18n.messages.dto.BaseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalComponentOpt implements ComponentOpt {
    private Logger logger = LoggerFactory.getLogger(LocalComponentOpt.class);
    private BaseDTO dto;
    public LocalComponentOpt(BaseDTO dto) {
        this.dto = dto;
    }

    @Override
    public List<String> getComponents() {
        List<String> components = new LinkedList<String>();
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

    private void getComponents(Path path, List<String> components) throws IOException {
        try (Stream<Path> listOfFiles = Files.list(path).filter(p -> !p.toFile().isFile())) {
            listOfFiles.forEach(s -> components.add(s.getFileName().toString()));
        }
    }
}
