/*
 * Copyright 2019-2025 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.base.cache.persist;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;

import com.vmware.vipclient.i18n.base.cache.TranslationCacheManager;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DiskCacheLoader implements Loader {
    Logger logger = LoggerFactory.getLogger(DiskCacheLoader.class);

    public Map<String, String> load(String key) {
        CacheSnapshot c = TranslationCacheManager.getInstance()
                .getCacheSnapshot();
        String rootpath = c.getCacheRootPath();
        File file = new File(rootpath + File.separator + key);
        FileInputStream fis = null;
        InputStreamReader reader = null;
        Map map = null;
        try {
            if (file.exists()) {
                try {
                    fis = new FileInputStream(file);
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }
            reader = new InputStreamReader(fis, "UTF-8");
            JSONObject jsonObject = new JSONObject(new JSONTokener(reader));
            map = jsonObject.toMap();
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            try {
                if (fis != null)
                    fis.close();
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
            try {
                if (reader != null)
                    reader.close();
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
        return map;
    }

    public boolean updateOrInsert(String key, String content) {
        CacheSnapshot c = TranslationCacheManager.getInstance()
                .getCacheSnapshot();
        String rootpath = c.getCacheRootPath();
        FileWriter writer = null;
        try {
            writer = new FileWriter(rootpath + File.separator + key);
            writer.write(content);
        } catch (IOException e) {
            logger.error(e.getMessage());
            return false;
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    logger.error(e.getMessage());
                }
            }
        }
        return true;
    }

    public boolean delete(String key) {
        CacheSnapshot c = TranslationCacheManager.getInstance()
                .getCacheSnapshot();
        String rootpath = c.getCacheRootPath();
        boolean f = false;
        if (this.isExisting(key)) {
            f = new File(rootpath + File.separator + key).delete();
        }
        return f;
    }

    public boolean isExisting(String key) {
        CacheSnapshot c = TranslationCacheManager.getInstance()
                .getCacheSnapshot();
        String rootpath = c.getCacheRootPath();
        return new File(rootpath + File.separator + key).exists();
    }

    public boolean clear() {
        return false;
    }

    public boolean refreshCacheSnapshot(CacheSnapshot cacheSnapshot) {
        try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream("cacheSnapshot.ser"))) {
            os.writeObject(cacheSnapshot);
            return true;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            return false;
        }
    }

    public CacheSnapshot getCacheSnapshot() {
        CacheSnapshot c = null;
        try (FileInputStream fs = new FileInputStream("cacheSnapshot.ser");
                ObjectInputStream ins = new ObjectInputStream(fs);) {
            Object o = ins.readObject();
            if (o != null) {
                c = (CacheSnapshot) o;
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            return null;
        }
        return c;
    }
}
