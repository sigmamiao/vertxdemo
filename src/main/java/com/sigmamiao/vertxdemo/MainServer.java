package com.sigmamiao.vertxdemo;


import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: Sigma Miao
 * @Create on: 2017/8/19 下午5:10
 */
public class MainServer {
    
    private static final Logger LOG = LoggerFactory.getLogger(MainServer.class);
    
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
    
        DeploymentOptions options = new DeploymentOptions();
        options.setWorker(true);
        
        vertx.deployVerticle(new MainVerticle(), options, result -> {
            if (result.succeeded()) {
                LOG.info("success");
            } else {
                LOG.error("server error {}", result.cause());
            }
        });
    }
}
