/*
 * InstrumentedTests.java
 *
 * Copyright (C) 2020 Vilius Sutkus'89 <ViliusSutkus89@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class InstrumentedTests {

  private static final File m_cacheDir = new File(InstrumentationRegistry.getInstrumentation().getTargetContext().getCacheDir(), "tests");
  private final AssetExtractor m_ae = new AssetExtractor(InstrumentationRegistry.getInstrumentation().getContext().getAssets());

  private static void removeRecursively(File entry) {
    if (!entry.exists()) {
      return;
    }

    if (entry.isDirectory()) {
      for (File f : entry.listFiles()) {
        removeRecursively(f);
      }
    }
    entry.delete();
  }

  @BeforeClass
  @AfterClass
  public static void takeOutTheTrash() {
    removeRecursively(m_cacheDir);
  }

  private void checkFile(File extractedFile, File expectedPath, boolean expectedToBeEmpty) {
    assertNotNull(extractedFile);
    assertTrue("Extracted file does not exist!", extractedFile.exists());
    assertTrue("Extracted file is not a file!", extractedFile.isFile());
    assertEquals("Extracted file size is wrong!", expectedToBeEmpty, 0 == extractedFile.length());
    assertEquals("Extracted file path not as expected!", extractedFile, expectedPath);
  }

  private void checkDirectory(File extractedDirectory, File expectedPath, long expectedEntryCount) {
    assertNotNull(extractedDirectory);
    assertTrue("Extracted directory does not exist!", extractedDirectory.exists());
    assertTrue("Extracted directory is not a directory!", extractedDirectory.isDirectory());
    assertEquals("Extracted directory contains unexpected amount of entries!", expectedEntryCount, extractedDirectory.list().length);
    assertEquals("Extracted directory path not as expected!", extractedDirectory, expectedPath);
  }

  @Test
  public void extractEmptyFile() {
    File extractedFile = m_ae.extract(m_cacheDir, "emptyFile");
    checkFile(extractedFile, new File(m_cacheDir, "emptyFile"), true);
  }

  @Test
  public void extractFileWithContent() {
    File extractedFile = m_ae.extract(m_cacheDir, "fileWithContent");
    checkFile(extractedFile, new File(m_cacheDir, "fileWithContent"), false);
  }

  @Test
  public void extractDirectoryWithFiles() {
    File extractedDirectory = m_ae.extract(m_cacheDir, "directoryWithFiles");
    checkDirectory(extractedDirectory, new File(m_cacheDir, "directoryWithFiles"), 2);

    File subFile1 = new File(extractedDirectory, "nonEmptyFile1");
    checkFile(subFile1, new File(m_cacheDir, "directoryWithFiles/nonEmptyFile1"), false);

    File subFile2 = new File(extractedDirectory, "nonEmptyFile2");
    checkFile(subFile2, new File(m_cacheDir, "directoryWithFiles/nonEmptyFile2"), false);
  }

  @Test
  public void extractDirectoryWithEmptyFile() {
    File extractedDirectory = m_ae.extract(m_cacheDir, "directoryWithEmptyFile");
    checkDirectory(extractedDirectory, new File(m_cacheDir, "directoryWithEmptyFile"), 1);

    File subFile = new File(extractedDirectory, "emptyFile");
    checkFile(subFile, new File(m_cacheDir, "directoryWithEmptyFile/emptyFile"), true);
  }

  @Test
  public void extractEmptyDirectoryFails() {
    // To make sure that empty directories are not an expected thing in assets.
    // Since completely empty dir can't be nicely included in source control,
    // it is created by the Gradle task createEmptyFolderInTestAssetsForSpecificUnitTest
    File extractedDirectory = m_ae.extract(m_cacheDir, "emptyDirectory");
    assertNull("Empty directory unexpectedly found in assets", extractedDirectory);
  }

}
