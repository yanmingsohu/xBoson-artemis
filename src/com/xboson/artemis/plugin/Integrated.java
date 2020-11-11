package com.xboson.artemis.plugin;


import org.apache.activemq.artemis.api.core.ActiveMQException;
import org.apache.activemq.artemis.core.server.ActiveMQServer;
import org.apache.activemq.artemis.core.server.impl.AddressInfo;
import org.apache.activemq.artemis.core.server.plugin.ActiveMQServerPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;


public class Integrated implements ActiveMQServerPlugin, IConst {


  private final Logger log = LoggerFactory.getLogger(Integrated.class);
  private Map<String, String> setting;
  private Database db;
  private Auth auth;
  private Device dev;


  @Override
  public void init(Map<String, String> properties) {
    setting = properties;
  }


  @Override
  public void registered(ActiveMQServer server) {
    db = new Database(setting);
    log.info("Connected to MongoDB {} {}", db.host, db.db_name);
    auth = new Auth(db);
    dev = new Device(db);
  }


  @Override
  public void unregistered(ActiveMQServer server) {
    db.close();
  }


  public void afterAddAddress(AddressInfo addressInfo, boolean reload)
          throws ActiveMQException {
    // 重启服务器时不创建设备
    if (!reload) {
      dev.createDevice(addressInfo.getName());
    }
  }
}
