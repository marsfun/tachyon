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

package tachyon.master.file.meta;

import org.powermock.reflect.Whitebox;

/**
 * Class which provides access to private state of {@link TtlBucket}.
 */
public final class TtlBucketPrivateAccess {

  /**
   * Sets the {@link TtlBucket#sTtlIntervalMs} variable for testing.
   *
   * @param intervalMs the interval in milliseconds
   */
  public static void setTtlIntervalMs(long intervalMs) {
    Whitebox.setInternalState(TtlBucket.class, "sTtlIntervalMs", intervalMs);
  }
}
