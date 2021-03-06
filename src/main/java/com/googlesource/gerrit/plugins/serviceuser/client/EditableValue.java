// Copyright (C) 2014 The Android Open Source Project
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.googlesource.gerrit.plugins.serviceuser.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtexpui.globalkey.client.NpTextBox;

public abstract class EditableValue extends FlowPanel {
  private final Widget labelWidget;
  private final Image edit;
  private final NpTextBox input;
  private final Button save;
  private final Button cancel;
  private Image warning;

  EditableValue(String serviceUser, String value) {
    this(serviceUser, value, null);
  }

  EditableValue(final String serviceUser, String value, String href) {
    if (href != null) {
      labelWidget = new Anchor(value, href);
    } else {
      labelWidget = new InlineLabel(value);
    }
    edit = new Image(ServiceUserPlugin.RESOURCES.edit());
    edit.addStyleName("serviceuser-editButton");
    edit.setTitle("Edit");

    input = new NpTextBox();
    input.setVisibleLength(25);
    input.setValue(value);
    input.setVisible(false);
    save = new Button();
    save.setText("Save");
    save.setVisible(false);
    save.setEnabled(false);
    cancel = new Button();
    cancel.setText("Cancel");
    cancel.setVisible(false);

    OnEditEnabler e = new OnEditEnabler(save);
    e.listenTo(input);

    edit.addClickHandler(
        new ClickHandler() {
          @Override
          public void onClick(ClickEvent event) {
            labelWidget.setVisible(false);
            edit.setVisible(false);
            input.setVisible(true);
            input.setFocus(true);
            save.setVisible(true);
            if (warning != null) {
              warning.setVisible(true);
            }
            cancel.setVisible(true);
          }
        });
    save.addClickHandler(
        new ClickHandler() {
          @Override
          public void onClick(ClickEvent event) {
            save.setEnabled(false);
            save(serviceUser, input.getValue().trim());
          }
        });
    input.addKeyPressHandler(
        new KeyPressHandler() {
          @Override
          public void onKeyPress(KeyPressEvent event) {
            if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
              save.setEnabled(false);
              save(serviceUser, input.getValue().trim());
            } else if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ESCAPE) {
              cancel();
            }
          }
        });
    cancel.addClickHandler(
        new ClickHandler() {
          @Override
          public void onClick(ClickEvent event) {
            cancel();
          }
        });

    add(labelWidget);
    add(edit);
    add(input);
    add(save);
    add(cancel);
  }

  private void cancel() {
    labelWidget.setVisible(true);
    edit.setVisible(true);
    input.setVisible(false);
    if (labelWidget instanceof Label) {
      input.setValue(((Label) labelWidget).getText());
    } else {
      input.setValue(((Anchor) labelWidget).getText());
    }
    save.setVisible(false);
    save.setEnabled(false);
    if (warning != null) {
      warning.setVisible(false);
    }
    cancel.setVisible(false);
  }

  public void setWarning(String msg) {
    if (warning == null) {
      warning = new Image(ServiceUserPlugin.RESOURCES.warning());
      insert(warning, getWidgetIndex(save));
      warning.setVisible(save.isVisible());
    }
    warning.setTitle(msg);
  }

  protected void updateValue(String newValue) {
    if (labelWidget instanceof Label) {
      ((Label) labelWidget).setText(newValue);
    } else {
      ((Anchor) labelWidget).setText(newValue);
    }
    labelWidget.setVisible(true);
    edit.setVisible(true);
    input.setVisible(false);
    input.setValue(newValue);
    save.setVisible(false);
    if (warning != null) {
      warning.setVisible(false);
    }
    save.setEnabled(false);
    cancel.setVisible(false);
  }

  protected void updateHref(String newHref) {
    if (labelWidget instanceof Anchor) {
      ((Anchor) labelWidget).setHref(newHref);
    }
  }

  protected abstract void save(String serviceUser, final String newValue);
}
