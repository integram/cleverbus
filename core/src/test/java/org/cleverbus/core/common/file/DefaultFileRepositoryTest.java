/*
 * Copyright (C) 2015
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.cleverbus.core.common.file;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.cleverbus.api.file.FileContentTypeExtEnum;
import org.cleverbus.api.file.OutputStreamWriterCallback;
import org.cleverbus.core.AbstractCoreTest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 * Test suite for {@link DefaultFileRepository}.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public class DefaultFileRepositoryTest extends AbstractCoreTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    private DefaultFileRepository fileRepository = new DefaultFileRepository();

    @Before
    public void prepareData() throws Exception {
        setPrivateField(fileRepository, "tempDir", tempFolder.getRoot());
        setPrivateField(fileRepository, "fileRepoDir", tempFolder.getRoot());
    }

    @Test
    public void testSavingFile() throws Exception {
        fileRepository.checkDirs();

        // save temporary file firstly
        String fileId = fileRepository.saveTempFile(new OutputStreamWriterCallback() {
            @Override
            public void writeTo(OutputStream os) throws IOException {
                IOUtils.copy(new StringReader("text to copy"), os);
            }
        });

        File tempFile = new File(tempFolder.getRoot(), fileId);
        assertThat(FileUtils.directoryContains(tempFolder.getRoot(), tempFile), is(true));
        assertThat(fileRepository.isFileIdValid(fileId), is(true));
        assertThat(fileRepository.isFileIdValid(fileId + "sth"), is(false));

        // commit file
        List<String> subFolders = new ArrayList<String>();
        subFolders.add("customerNo");
        subFolders.add("accountNo");

        FileContentTypeExtEnum contentType = new FileContentTypeExtEnum() {
            @Override
            public String getContentType() {
                return "OBCANKA";
            }

            @Override
            public String getFilePrefix() {
                return "doc";
            }
        };

        fileRepository.commitFile(fileId, "orig.doc", contentType, subFolders);

        String fileName = StringUtils.join(subFolders, File.separator) + File.separator
                + contentType.getFilePrefix() + "_" + "orig.doc";
        File targetFile = new File(tempFolder.getRoot(), fileName);

        assertThat(tempFile.exists(), is(false));
        assertThat(targetFile.exists(), is(true));
        assertThat(targetFile.isDirectory(), is(false));
    }
}
