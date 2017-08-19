package com.sigmamiao.vertxdemo;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: Sigma Miao
 * @Create on: 2017/8/19 下午5:04
 */
public class MainVerticle extends AbstractVerticle {
    
    private static final Logger LOG = LoggerFactory.getLogger(MainVerticle.class);
    
    @Override
    public void start(Future<Void> future) throws Exception {
        
        HttpServer server = vertx.createHttpServer();
        
        Router router = Router.router(vertx);
        
        router.route().handler(routeContext -> {
    
    
            HttpServerResponse response = routeContext.response();
            
            LOG.info("incoming");
            
            
            response.putHeader("context-type","text/plain");
            response.end("Hello World from Vert.x-Web!");
        });
        
        
        server.requestHandler(router::accept).listen(9090);
        
    }
}
