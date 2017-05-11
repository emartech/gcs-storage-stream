package com.emarsys.google.storage

import akka.actor.ActorSystem
import com.typesafe.config.ConfigRenderOptions


object DefaultConfig {

  def apply(s: ActorSystem) = {
    new DefaultConfig(s)
  }

}

class DefaultConfig(system: ActorSystem) {

  private val googleStorageConfig = system.settings.config getConfig "googleStorage"

  def configValue(properties: String, default: String) = {
    if (googleStorageConfig.hasPath(properties)) googleStorageConfig.getString(properties) else default
  }

  def configOfProject(properties: String) = {
    googleStorageConfig.getConfig("project").getString(properties)
  }

  def configAsJson(properties: String) = {
    googleStorageConfig.getValue(properties).render(ConfigRenderOptions.defaults().setJson(true).setOriginComments(false)).replace("\\\\","\\")
  }

}
