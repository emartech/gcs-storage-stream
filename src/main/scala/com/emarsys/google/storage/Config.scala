package com.emarsys.google.storage

import java.io.ByteArrayInputStream

import akka.actor.ActorSystem
import com.google.api.services.storage.StorageScopes
import com.google.auth.oauth2.GoogleCredentials
import com.typesafe.config.ConfigRenderOptions


object DefaultConfig {

  def apply(s: ActorSystem) = {
    new DefaultConfig(s)
  }

}

class DefaultConfig(system: ActorSystem) {

  private val googleStorageConfig = system.settings.config getConfig "googleStorage"

  lazy val credentials: GoogleCredentials = GoogleCredentials.fromStream(new ByteArrayInputStream(DefaultConfig(system).configAsJson("secret").getBytes)).createScoped(StorageScopes.all())

  val useProxy = googleStorageConfig.hasPath("proxy-host")
  val proxyHost = configValue("proxy-host", "")
  val proxyPort = configValue("proxy-port", "0").toInt

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
