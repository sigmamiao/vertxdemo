package com.sigmamiao.vertxdemo;

import com.zaxxer.hikari.HikariDataSource;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: Sigma Miao
 * @Create on: 2017/8/19 下午5:04
 */
public class MainVerticle extends AbstractVerticle {
    
    private static final Logger LOG = LoggerFactory.getLogger(MainVerticle.class);
    
    private JDBCClient jdbcClient;
    
    @Override
    public void start(Future<Void> future) throws Exception {
        
        HttpServer server = vertx.createHttpServer();
        prepareDatabase();
        
        Router router = Router.router(vertx);
        
        router.route().handler(this::indexHandler);
        
        
        server.requestHandler(router::accept).listen(9090, ar -> {
            if (ar.succeeded()) {
                LOG.info("HTTP server running on port 9090");
                future.complete();
            } else {
                LOG.error("Could not start a HTTP server", ar.cause());
                future.fail(ar.cause());
            }
        });
        
    }
    
    
    private void indexHandler(RoutingContext context) {
        HttpServerResponse response = context.response();
        response.putHeader("context-type", "application/json");
        jdbcClient.getConnection(res -> {
            if (res.succeeded()) {
                SQLConnection connection = res.result();
                
                connection.query("select * from book where id = 1", rs -> {
                    LOG.info("query result: {}", rs.result().getRows().get(0));
                    response.end(rs.result().getRows().get(0).toString());
                });
                connection.close();
            }
        });
        
        
    }
    
    private Future<Void> prepareDatabase() {
        Future<Void> future = Future.future();
        //init datasource
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUsername("root");
        dataSource.setPassword("root");
        dataSource.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/test?characterEncoding=utf8&useSSL=true&autoReconnect=true&failOverReadOnly=false");
        dataSource.setMaximumPoolSize(10);
        
        jdbcClient = JDBCClient.create(vertx, dataSource);
        
        jdbcClient.getConnection(handle -> {
            if (handle.failed()) {
                LOG.error("Could not open a database connection {}", handle.cause());
                future.fail(handle.cause());
            } else {
                SQLConnection connection = handle.result();
                connection.execute("select 1", create -> {
                    connection.close();
                    if (create.failed()) {
                        LOG.error("check db failed {}", create.cause());
                        future.fail(create.cause());
                    } else {
                        future.complete();
                    }
                });
            }
        });
        return future;
    }
}
