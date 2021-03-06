/*
 * Licensed to the University of California, Berkeley under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package tachyon.shell.command;

import java.io.IOException;
import java.util.List;

import javax.annotation.concurrent.ThreadSafe;

import tachyon.TachyonURI;
import tachyon.client.file.FileSystem;
import tachyon.client.file.URIStatus;
import tachyon.conf.TachyonConf;
import tachyon.exception.TachyonException;

/**
 * Displays the size of a file or a directory specified by argv.
 */
@ThreadSafe
public final class DuCommand extends WithWildCardPathCommand {

  /**
   * @param conf the configuration for Tachyon
   * @param fs the filesystem of Tachyon
   */
  public DuCommand(TachyonConf conf, FileSystem fs) {
    super(conf, fs);
  }

  @Override
  public String getCommandName() {
    return "du";
  }

  @Override
  void runCommand(TachyonURI path) throws IOException {
    long sizeInBytes = getFileOrFolderSize(mFileSystem, path);
    System.out.println(path + " is " + sizeInBytes + " bytes");
  }

  /**
   * Calculates the size of a path (file or folder) specified by a {@link TachyonURI}.
   *
   * @param tachyonFS A {@link FileSystem}
   * @param path A {@link TachyonURI} denoting the path
   * @return total size of the specified path in byte
   * @throws IOException if a non-Tachyon related exception occurs
   */
  private long getFileOrFolderSize(FileSystem tachyonFS, TachyonURI path)
      throws IOException {
    long sizeInBytes = 0;
    List<URIStatus> statuses;
    try {
      statuses = tachyonFS.listStatus(path);
    } catch (TachyonException e) {
      throw new IOException(e.getMessage());
    }
    for (URIStatus status : statuses) {
      if (status.isFolder()) {
        TachyonURI subFolder = new TachyonURI(status.getPath());
        sizeInBytes += getFileOrFolderSize(tachyonFS, subFolder);
      } else {
        sizeInBytes += status.getLength();
      }
    }
    return sizeInBytes;
  }

  @Override
  public String getUsage() {
    return "du <path>";
  }

  @Override
  public String getDescription() {
    return "Displays the size of the specified file or directory.";
  }
}
