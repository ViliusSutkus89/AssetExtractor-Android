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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class AssetExtractor {
    private static final String TAG = "AssetExtractor";

    private boolean m_overwrite;

    public AssetExtractor(boolean overwrite) {
        m_overwrite = overwrite;
    }

    public Boolean extract(@NonNull AssetManager assetManager, @NonNull File outputDir, @NonNull String source) {
        String[] assets;
        try {
            assets = assetManager.list(source);
        } catch (IOException e) {
            Log.e(TAG, "Failed to list asset: " + source);
            return false;
        }

        if (null == assets) {
            Log.e(TAG, "Null returned instead of assets: " + source);
            return false;
        }

        String nodeName = new File(source).getName();
        File output = new File(outputDir, nodeName);

        // Processing a file
        if (0 == assets.length) {
            if (!this.m_overwrite && output.exists()) {
                return true;
            }
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(assetManager.open(source)));
                BufferedWriter bw = new BufferedWriter(new FileWriter(output));
                String line;
                while (null != (line = br.readLine())) {
                    bw.write(line);
                }
                bw.close();
                br.close();
            } catch (IOException e) {
                Log.e(TAG, "Failed to extract asset: " + source);
                return false;
            }
        } else {
            // Processing a folder
            if (!output.exists() && !output.mkdirs()) {
                Log.e(TAG, "Failed to create output folder: " + output.getAbsolutePath());
                return false;
            }
            for (String asset : assets) {
                if (!extract(assetManager, output, asset)) {
                    return false;
                }
            }
        }
        return true;
    }
}
