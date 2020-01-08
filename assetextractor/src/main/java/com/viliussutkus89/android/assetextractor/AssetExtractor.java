/*
 * AssetExtractor.java
 *
 * Copyright (C) 2019,2020 Vilius Sutkus'89 <ViliusSutkus89@gmail.com>
 *
 * Implementation inspired by https://gist.github.com/tylerchesley/6198074
 *
 * AssetExtractor-Android is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.viliussutkus89.android.assetextractor;

import android.content.res.AssetManager;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class AssetExtractor {
    private static final String TAG = "AssetExtractor";

    private AssetManager m_assetManager;
    private boolean m_overwrite = false;

    public AssetExtractor(@NonNull AssetManager assetManager) {
        this.m_assetManager = assetManager;
    }

    public AssetExtractor setOverwrite() {
        this.m_overwrite = true;
        return this;
    }

    public AssetExtractor setNoOverwrite() {
        this.m_overwrite = false;
        return this;
    }

    public File extract(@NonNull File outputDir, @NonNull String source) {
        String[] assets;
        try {
            assets = this.m_assetManager.list(source);
        } catch (IOException e) {
            Log.e(TAG, "Failed to list asset: " + source);
            return null;
        }

        if (null == assets) {
            Log.e(TAG, "Null returned instead of assets: " + source);
            return null;
        }

        if (!outputDir.exists() && !outputDir.mkdirs()) {
            Log.e(TAG, "Failed to create output folder: " + outputDir.getAbsolutePath());
            return null;
        }

        String nodeName = new File(source).getName();
        File output = new File(outputDir, nodeName);

        // Processing a file
        if (0 == assets.length) {
            if (!this.m_overwrite && output.exists()) {
                return output;
            }
            try {
                InputStream i = this.m_assetManager.open(source);
                try {
                    OutputStream o = new FileOutputStream(output);
                    try {
                        int bufSize = 1024 * 512;
                        byte[] buffer = new byte[bufSize];
                        int haveRead;
                        while (-1 != (haveRead = i.read(buffer))) {
                            o.write(buffer, 0, haveRead);
                        }
                    } finally {
                        o.close();
                    }
                } finally {
                    i.close();
                }
            } catch (IOException e) {
                Log.e(TAG, "Failed to extract asset: " + source);
                return null;
            }
        } else {
            // Processing a folder
            for (String asset: assets) {
                if (null == extract(output, asset)) {
                    return null;
                }
            }
        }
        return output;
    }
}
