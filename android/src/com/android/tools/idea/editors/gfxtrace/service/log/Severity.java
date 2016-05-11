/*
 * Copyright (C) 2015 The Android Open Source Project
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
 *
 * THIS FILE WAS GENERATED BY codergen. EDIT WITH CARE.
 */
package com.android.tools.idea.editors.gfxtrace.service.log;

import com.android.tools.rpclib.binary.Decoder;
import com.android.tools.rpclib.binary.Encoder;
import com.google.common.collect.ImmutableMap;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public final class Severity {
  public static final Severity Emergency = new Severity(0, "Emergency");
  public static final int EmergencyValue = 0;
  public static final Severity Alert = new Severity(1, "Alert");
  public static final int AlertValue = 1;
  public static final Severity Critical = new Severity(2, "Critical");
  public static final int CriticalValue = 2;
  public static final Severity Error = new Severity(3, "Error");
  public static final int ErrorValue = 3;
  public static final Severity Warning = new Severity(4, "Warning");
  public static final int WarningValue = 4;
  public static final Severity Notice = new Severity(5, "Notice");
  public static final int NoticeValue = 5;
  public static final Severity Info = new Severity(6, "Info");
  public static final int InfoValue = 6;
  public static final Severity Debug = new Severity(7, "Debug");
  public static final int DebugValue = 7;

  private static final ImmutableMap<Integer, Severity> VALUES = ImmutableMap.<Integer, Severity>builder()
    .put(0, Emergency)
    .put(1, Alert)
    .put(2, Critical)
    .put(3, Error)
    .put(4, Warning)
    .put(5, Notice)
    .put(6, Info)
    .put(7, Debug)
    .build();

  private final int myValue;
  private final String myName;

  private Severity(int v, String n) {
    myValue = v;
    myName = n;
  }

  public int getValue() {
    return myValue;
  }

  public String getName() {
    return myName;
  }

  public void encode(@NotNull Encoder e) throws IOException {
    e.int32(myValue);
  }

  public static Severity decode(@NotNull Decoder d) throws IOException {
    return findOrCreate(d.int32());
  }

  public static Severity find(int value) {
    return VALUES.get(value);
  }

  public static Severity findOrCreate(int value) {
    Severity result = VALUES.get(value);
    return (result == null) ? new Severity(value, null) : result;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || !(o instanceof Severity)) return false;
    return myValue == ((Severity)o).myValue;
  }

  @Override
  public int hashCode() {
    return myValue;
  }

  @Override
  public String toString() {
    return (myName == null) ? "Severity(" + myValue + ")" : myName;
  }
}