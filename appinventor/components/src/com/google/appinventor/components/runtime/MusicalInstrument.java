// -*- mode: java; c-basic-offset: 2; -*-
// Copyright 2009-2011 Google, All Rights reserved
// Copyright 2011-2012 MIT, All rights reserved
// Released under the MIT License https://raw.github.com/mit-cml/app-inventor/master/mitlicense.txt

package com.google.appinventor.components.runtime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.puredata.android.io.AudioParameters;
import org.puredata.android.io.PdAudio;
import org.puredata.android.utils.PdUiDispatcher;
import org.puredata.core.PdBase;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import com.google.appinventor.components.annotations.DesignerComponent;
import com.google.appinventor.components.annotations.DesignerProperty;
import com.google.appinventor.components.annotations.PropertyCategory;
import com.google.appinventor.components.annotations.SimpleEvent;
import com.google.appinventor.components.annotations.SimpleFunction;
import com.google.appinventor.components.annotations.SimpleObject;
import com.google.appinventor.components.annotations.SimpleProperty;
import com.google.appinventor.components.annotations.UsesLibraries;
import com.google.appinventor.components.annotations.UsesPermissions;
import com.google.appinventor.components.common.ComponentCategory;
import com.google.appinventor.components.common.PropertyTypeConstants;
import com.google.appinventor.components.common.YaVersion;
import com.google.appinventor.components.runtime.util.AsynchUtil;
import com.google.appinventor.components.runtime.util.ErrorMessages;

/**
 * Component for synthetizing sounds
 * 
 * @author biazon@usp.br (Leandro Biazon)
 */
@DesignerComponent(version = YaVersion.MUSICALINSTRUMENT_COMPONENT_VERSION, description = "<p>A non-visible component interfaces "
    + "with <a href=\"http://libpd.cc\" target=\"_blank\">libpd</a>. </p>", category = ComponentCategory.MEDIA, nonVisible = true, iconName = "images/musicalInstrument.png")
@SimpleObject
// @UsesPermissions(permissionNames = "android.permission.INTERNET")
@UsesLibraries(libraries = "pdcore.jar")
public final class MusicalInstrument extends AndroidNonvisibleComponent {

  // the following fields should only be accessed from the UI thread
  private String helloComponent = "";
  private final SharedPreferences sharedPreferences;
  private final ComponentContainer container;

  private PdUiDispatcher dispatcher;

  public MusicalInstrument(ComponentContainer container) {
    super(container.$form());
    this.container = container;

    sharedPreferences = container.$context().getSharedPreferences("MusicalInstrument",
        Context.MODE_PRIVATE);
  }

  /**
   * helloComponent property getter method.
   */
  @SimpleProperty(category = PropertyCategory.BEHAVIOR)
  public String HelloComponent() {
    return helloComponent;
  }

  /**
   * helloComponent property setter method.
   * 
   * @param helloComponent
   *          simple parameter for testing
   */
  @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_STRING, defaultValue = "")
  @SimpleProperty
  public void HelloComponent(String helloComponent) {
    this.helloComponent = helloComponent;
  }

  /**
   * Starts pure data engine
   */
  @SimpleFunction(description = "Starts pure data engine")
  public void initPd() {
    try {
      // configure the audio glue
      int sampleRate = AudioParameters.suggestSampleRate();
      PdAudio.initAudio(sampleRate, 0, 2, 8, true);
      
      // create and install the dispatcher
      dispatcher = new PdUiDispatcher();
      PdBase.setReceiver(dispatcher);
      
      form.dispatchErrorOccurredEvent(this, "initPd",
          ErrorMessages.ERROR_MUSICALINSTRUMENT, String.valueOf(sampleRate));
      
    } catch (IOException e) {
        form.dispatchErrorOccurredEvent(this, "initPd",
            ErrorMessages.ERROR_MUSICALINSTRUMENT, "FUUUUU");
    }
  }
  
  @SimpleFunction(description = "Checks if pd engine is running") 
  public void isPdRunning() {
    final String isRunning = String.valueOf(PdAudio.isRunning());
    form.dispatchErrorOccurredEvent(this, "isPdRunning",
        ErrorMessages.ERROR_MUSICALINSTRUMENT, isRunning);
  }
  
  @SimpleFunction(description = "Shows sample rate") 
  public void showSampleRate() {
    final String sampleRate = String.valueOf(AudioParameters.suggestSampleRate());
    form.dispatchErrorOccurredEvent(this, "showSampleRate",
        ErrorMessages.ERROR_MUSICALINSTRUMENT, sampleRate);
  }
}
