package com.emarsys.google.storage

import java.io.ByteArrayInputStream
import java.net.InetSocketAddress

import akka.actor.ActorSystem
import com.emarsys.google.storage.Config.GoogleConfig
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.services.storage.StorageScopes
import com.google.auth.http.HttpTransportFactory
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.http.HttpTransportOptions.DefaultHttpTransportFactory
import com.typesafe.config.{ConfigFactory, ConfigRenderOptions}

object Config {

  def apply(s: ActorSystem) = {
    new Config(s)
  }

  case class GoogleConfig(
      useWorkloadIdentityAuth: Boolean
  )
}

class Config(system: ActorSystem) {
  private val config       = ConfigFactory.load()
  private val googleConfig = config.getConfig("google")

  private val googleStorageConfig = system.settings.config.getConfig("googleStorage")

  lazy val google: GoogleConfig = GoogleConfig(
    useWorkloadIdentityAuth = googleConfig.getBoolean("use-workload-identity-auth")
  )

  lazy val credentials: GoogleCredentials = {
    val inputStream = new ByteArrayInputStream(Config(system).configAsJson("secret").getBytes)
    GoogleCredentials.fromStream(inputStream, httpTransportFactory).createScoped(StorageScopes.all())
  }

  val useProxy  = googleStorageConfig.hasPath("proxy-host")
  val proxyHost = configValue("proxy-host", "")
  val proxyPort = configValue("proxy-port", "0").toInt

  val httpTransportFactory = {
    if (!useProxy) {
      new DefaultHttpTransportFactory()
    } else {
      new HttpTransportFactory {
        override def create() = {
          val socketAddress = new InetSocketAddress(proxyHost, proxyPort)
          val proxy         = new java.net.Proxy(java.net.Proxy.Type.HTTP, socketAddress)
          new NetHttpTransport.Builder().setProxy(proxy).build()
        }
      }
    }
  }

  def configValue(properties: String, default: String) = {
    if (googleStorageConfig.hasPath(properties)) googleStorageConfig.getString(properties) else default
  }

  def configOfProject(properties: String) = {
    googleStorageConfig.getConfig("project").getString(properties)
  }

  def configAsJson(properties: String) = {
    googleStorageConfig
      .getValue(properties)
      .render(
        ConfigRenderOptions.defaults().setJson(true).setOriginComments(false)
      )
      .replace("\\\\", "\\")
  }
}
