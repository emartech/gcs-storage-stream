package com.emarsys.google.storage

import java.net.InetSocketAddress
import akka.actor.ActorSystem
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.auth.http.HttpTransportFactory
import com.google.cloud.HttpTransportOptions
import com.google.cloud.storage.StorageOptions


object GoogleStorageService {

  def apply(project: String)(implicit system: ActorSystem) = {
    StorageOptions.newBuilder()
      .setTransportOptions(createTransportOptions())
      .setCredentials(DefaultConfig(system).credentials).setProjectId(project)
      .build()
      .getService
  }

  private def createTransportOptions()(implicit system: ActorSystem): HttpTransportOptions = {
    val config = DefaultConfig(system)
    if (!config.useProxy) {
      HttpTransportOptions.newBuilder().build()
    } else {
      val httpTransportFactory = new HttpTransportFactory {
        override def create() = {
          val socketAddress = new InetSocketAddress(config.proxyHost, config.proxyPort)
          val proxy = new java.net.Proxy(java.net.Proxy.Type.HTTP, socketAddress)
          new NetHttpTransport.Builder().setProxy(proxy).build()
        }
      }
      HttpTransportOptions.newBuilder().setHttpTransportFactory(httpTransportFactory).build()
    }
  }

}
