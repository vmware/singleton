package com.vmware.vip.remote.config.service;

import java.io.File;
import java.io.IOException;

public interface GitOSService {
    public File runOSGit(String sshRepo, String filterPath, String branch, File dir) throws IOException, InterruptedException;
}
