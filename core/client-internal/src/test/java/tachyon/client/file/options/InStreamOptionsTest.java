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

package tachyon.client.file.options;

import org.junit.Assert;
import org.junit.Test;

import tachyon.Constants;
import tachyon.client.ClientContext;
import tachyon.client.ReadType;
import tachyon.client.TachyonStorageType;
import tachyon.client.file.policy.FileWriteLocationPolicy;
import tachyon.client.file.policy.RoundRobinPolicy;
import tachyon.client.util.ClientTestUtils;

/**
 * Tests for the {@link InStreamOptions} class.
 */
public class InStreamOptionsTest {
  /**
   * Tests that building an {@link InStreamOptions} with the defaults works.
   */
  @Test
  public void defaultsTest() {
    InStreamOptions options = InStreamOptions.defaults();
    Assert.assertEquals(TachyonStorageType.PROMOTE, options.getTachyonStorageType());
  }

  /**
   * Tests getting and setting fields.
   */
  @Test
  public void fieldsTest() {
    ReadType readType = ReadType.NO_CACHE;
    FileWriteLocationPolicy policy = new RoundRobinPolicy();

    InStreamOptions options = InStreamOptions.defaults();
    options.setReadType(readType);
    options.setLocationPolicy(policy);

    Assert.assertEquals(options.getTachyonStorageType(), readType.getTachyonStorageType());
    Assert.assertEquals(policy, options.getLocationPolicy());
  }

  /**
   * Tests that building a {@link InStreamOptions} with a modified configuration works.
   */
  @Test
  public void modifiedConfTest() {
    ClientContext.getConf().set(Constants.USER_FILE_READ_TYPE_DEFAULT,
        ReadType.NO_CACHE.toString());
    try {
      InStreamOptions options = InStreamOptions.defaults();
      Assert.assertEquals(ReadType.NO_CACHE.getTachyonStorageType(),
          options.getTachyonStorageType());
    } finally {
      ClientTestUtils.resetClientContext();
    }
  }
}
