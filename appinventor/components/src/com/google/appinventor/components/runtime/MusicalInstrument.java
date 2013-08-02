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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
public final class MusicalInstrument extends AndroidNonvisibleComponent implements Component {

  // the following fields should only be accessed from the UI thread
  private String helloComponent = "";
  private final SharedPreferences sharedPreferences;
  private final ComponentContainer container;
  private final Activity activity;
  private final int sampleRate;

  private PdUiDispatcher dispatcher;

  public MusicalInstrument(ComponentContainer container) {
    super(container.$form());
    this.container = container;
    this.activity = container.$context();
    this.sampleRate = AudioParameters.suggestSampleRate();
    this.dispatcher = new PdUiDispatcher();

    sharedPreferences = container.$context().getSharedPreferences("MusicalInstrument",
        Context.MODE_PRIVATE);
  }

  private void showNoticeAndDie(String message, String title, String buttonText){
    AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
    alertDialog.setTitle(title);
    // prevents the user from escaping the dialog by hitting the Back button
    alertDialog.setCancelable(false);
    alertDialog.setMessage(message);
    alertDialog.setButton(buttonText, new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int which) {
        activity.finish();
      }});
    alertDialog.show();
  }

  /**
   * helloComponent property getter method.
   */
  @SimpleProperty(category = PropertyCategory.BEHAVIOR)
  public String HelloComponent() {
    return helloComponent;
  }

  /**
   * sampleRate property getter method.
   */
  @SimpleProperty(category = PropertyCategory.BEHAVIOR)
  public String SampleRate() {
    return sampleRate;
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

  @SimpleFunction(description = "Set receiver")
  public void setReceiver() {
    try {
      PdBase.setReceiver(dispatcher);
    } catch (Exception e) {
      showNoticeAndDie(
          "The receiver died.",
          "libpd",
          "Ow man...");
    }
  }

  @SimpleFunction(description = "Init audio")
  public void initAudio() {
    try {
      PdAudio.initAudio(sampleRate, 0, 2, 8, true);
    } catch (Exception e) {
      showNoticeAndDie(
          "The audio do not want to start.",
          "libpd",
          "Ow man...");
    }
  }

  @SimpleFunction(description = "Init audio")
  public void TriggerNote(int note) {
    Log.d("App Inventor", "Note sent :" + note);
    Intent intent = new Intent();
    intent.setAction("AppInventor");
    intent.putExtra("note", note);
    activity.sendBroadcast(intent);
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

  /**
   * Starts pure data engine
   */
  /*
  @SimpleFunction(description = "Starts pure data engine")
  public void initPd() {
    try {
      PdAudio.initAudio(sampleRate, 0, 2, 8, true);
      PdBase.setReceiver(dispatcher);

      form.dispatchErrorOccurredEvent(this, "initPd",
          ErrorMessages.ERROR_MUSICALINSTRUMENT, String.valueOf(sampleRate));

    } catch (Exception e) {
        form.dispatchErrorOccurredEvent(this, "initPd",
            ErrorMessages.ERROR_MUSICALINSTRUMENT, "FUUUUU");
    }
  }
  */
}
