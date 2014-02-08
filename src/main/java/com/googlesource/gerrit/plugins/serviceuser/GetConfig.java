// Copyright (C) 2013 The Android Open Source Project
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

package com.googlesource.gerrit.plugins.serviceuser;

import com.google.common.base.Strings;
import com.google.gerrit.extensions.annotations.PluginName;
import com.google.gerrit.extensions.restapi.RestReadView;
import com.google.gerrit.server.config.ConfigResource;
import com.google.gerrit.server.config.PluginConfig;
import com.google.gerrit.server.config.PluginConfigFactory;
import com.google.inject.Inject;

public class GetConfig implements RestReadView<ConfigResource> {

  private final PluginConfig cfg;

  @Inject
  public GetConfig(PluginConfigFactory cfgFactory,
      @PluginName String pluginName) {
    this.cfg = cfgFactory.getFromGerritConfig(pluginName);
  }

  @Override
  public ConfigInfo apply(ConfigResource rsrc) {
    ConfigInfo info = new ConfigInfo();
    info.info = Strings.emptyToNull(cfg.getString("infoMessage"));
    info.onSuccess = Strings.emptyToNull(cfg.getString("onSuccessMessage"));
    info.allowEmail = cfg.getBoolean("allowEmail", false);
    if (!info.allowEmail) {
      info.allowEmail = null;
    }
    return info;
  }

  public class ConfigInfo {
    String info;
    String onSuccess;
    Boolean allowEmail;
  }
}