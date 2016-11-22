/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.tools.profilers.network;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class HttpDataTest {
  @Test
  public void responseFieldsStringIsCorrectlySplitAndTrimmed() throws Exception {
    HttpData data = new HttpData();
    data.setHttpResponseFields("first=1 \n  second  = 2\n equation=x+y=10");

    assertThat(data.getHttpResponseField("first"), equalTo("1"));
    assertThat(data.getHttpResponseField("second"), equalTo("2"));
    assertThat(data.getHttpResponseField("equation"), equalTo("x+y=10"));
  }

  @Test
  public void urlNameParsedProperly() {
    String urlString = "www.google.com/l1/l2/test?query=1";
    assertThat(HttpData.getUrlName(urlString), equalTo("test"));
  }

  @Test
  public void urlNameParsedProperlyWithEndingSlash() {
    String urlString = "https://www.google.com/l1/l2/test/";
    assertThat(HttpData.getUrlName(urlString), equalTo("test"));
  }

  @Test
  public void urlNameParsedProperlyWithEmptyPath() {
    String urlString = "https://www.google.com";
    assertThat(HttpData.getUrlName(urlString), equalTo(""));
  }
}
