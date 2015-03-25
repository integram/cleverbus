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

package org.cleverbus.api.file;

import java.util.List;


/**
 * Contract for storing files in the repository and manipulating with them.
 * <p/>
 * Supposed workflow:
 * <ol>
 *     <li>save file into temporary store (file will be in this store for limited time only)
 *     <li>commit saving file - the file will be moved from temporary folder to the target place
 * </ol>
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public interface FileRepository {

    /**
     * Saves temporary file in the repository.
     *
     * @param writerCallback the callback for writing file to specified output stream
     * @return new unique file identifier
     */
    String saveTempFile(OutputStreamWriterCallback writerCallback);

    /**
     * Is specified file ID valid identifier?
     * In other words is there file with specified identifier?
     *
     * @param fileId the file ID
     * @return {@code true} if file is valid otherwise {@code false}
     */
    boolean isFileIdValid(String fileId);

    /**
     * Commits saving file - the file will be moved from temporary folder to the target place.
     * If there is already the file with the same name in the target folder then it's replaced by the new one.
     *
     * @param fileId the file identifier from {@link #saveTempFile(OutputStreamWriterCallback)}
     * @param fileName the original file name
     * @param contentType the file content type
     * @param subFolders the collection of sub-folders which determine where is the target folder for moving the file
     */
    void commitFile(String fileId, String fileName, FileContentTypeExtEnum contentType, List<String> subFolders);
}
