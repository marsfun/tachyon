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

package tachyon.examples;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tachyon.Constants;
import tachyon.TachyonURI;
import tachyon.Version;
import tachyon.client.ClientContext;
import tachyon.client.ReadType;
import tachyon.client.WriteType;
import tachyon.client.file.FileInStream;
import tachyon.client.file.FileOutStream;
import tachyon.client.file.FileSystem;
import tachyon.client.file.options.CreateFileOptions;
import tachyon.client.file.options.OpenFileOptions;
import tachyon.exception.TachyonException;
import tachyon.util.CommonUtils;
import tachyon.util.FormatUtils;

/**
 * Example to show the basic operations of Tachyon.
 */
public class BasicOperations implements Callable<Boolean> {
  private static final Logger LOG = LoggerFactory.getLogger(Constants.LOGGER_TYPE);
  private final TachyonURI mMasterLocation;
  private final TachyonURI mFilePath;
  private final OpenFileOptions mReadOptions;
  private final CreateFileOptions mWriteOptions;
  private final int mNumbers = 20;

  /**
   * @param masterLocation the location of the master
   * @param filePath the path for the files
   * @param readType the {@link ReadType}
   * @param writeType the {@link WriteType}
   */
  public BasicOperations(TachyonURI masterLocation, TachyonURI filePath, ReadType readType,
      WriteType writeType) {
    mMasterLocation = masterLocation;
    mFilePath = filePath;
    mReadOptions = OpenFileOptions.defaults().setReadType(readType);
    mWriteOptions = CreateFileOptions.defaults().setWriteType(writeType);
  }

  @Override
  public Boolean call() throws Exception {
    ClientContext.getConf().set(Constants.MASTER_HOSTNAME, mMasterLocation.getHost());
    ClientContext.getConf().set(Constants.MASTER_RPC_PORT,
        Integer.toString(mMasterLocation.getPort()));
    FileSystem fs = FileSystem.Factory.get();
    writeFile(fs);
    return readFile(fs);
  }

  private void writeFile(FileSystem fileSystem)
    throws IOException, TachyonException {
    ByteBuffer buf = ByteBuffer.allocate(mNumbers * 4);
    buf.order(ByteOrder.nativeOrder());
    for (int k = 0; k < mNumbers; k ++) {
      buf.putInt(k);
    }
    LOG.debug("Writing data...");
    long startTimeMs = CommonUtils.getCurrentMs();
    FileOutStream os = fileSystem.createFile(mFilePath, mWriteOptions);
    os.write(buf.array());
    os.close();

    LOG.info(FormatUtils.formatTimeTakenMs(startTimeMs, "writeFile to file " + mFilePath));
  }

  private boolean readFile(FileSystem fileSystem)
      throws IOException, TachyonException {
    boolean pass = true;
    LOG.debug("Reading data...");
    final long startTimeMs = CommonUtils.getCurrentMs();
    FileInStream is = fileSystem.openFile(mFilePath, mReadOptions);
    ByteBuffer buf = ByteBuffer.allocate((int) is.remaining());
    is.read(buf.array());
    buf.order(ByteOrder.nativeOrder());
    for (int k = 0; k < mNumbers; k ++) {
      pass = pass && (buf.getInt() == k);
    }
    is.close();

    LOG.info(FormatUtils.formatTimeTakenMs(startTimeMs, "readFile file " + mFilePath));
    return pass;
  }

  /**
   * Usage:
   * {@code java -cp <TACHYON-VERSION> BasicOperations
   * <ReadType (CACHE_PROMOTE | CACHE | NO_CACHE)>
   * <WriteType (MUST_CACHE | CACHE_THROUGH | THROUGH | ASYNC_THROUGH)>}
   *
   * @param args the arguments for this example
   */
  public static void main(String[] args) {
    if (args.length != 4) {
      System.out.println("java -cp " + Version.TACHYON_JAR + " " + BasicOperations.class.getName()
          + " <ReadType (CACHE_PROMOTE | CACHE | NO_CACHE)> <WriteType (MUST_CACHE | CACHE_THROUGH"
          + " | THROUGH | ASYNC_THROUGH)>");
      System.exit(-1);
    }

    Utils.runExample(new BasicOperations(new TachyonURI(args[0]), new TachyonURI(args[1]),
        ReadType.valueOf(args[2]), WriteType.valueOf(args[3])));
  }
}
