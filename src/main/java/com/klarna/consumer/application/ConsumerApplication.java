package com.klarna.consumer.application;

import com.klarna.consumer.configuration.WebAppConfig;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

public class ConsumerApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerApplication.class);

    private final int port;
    private final Server server;

    public ConsumerApplication(int port) {
        this.port = port;
        this.server = new Server(port);
    }

    public void run() {
        try {
            start();
            server.join();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to start application", e);
        }
    }

    public void start() {
        AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();
        applicationContext.register(WebAppConfig.class);

        ServletHolder servletHolder = new ServletHolder(new DispatcherServlet(applicationContext));
        ServletContextHandler context = new ServletContextHandler();
        context.setContextPath("/");
        context.addServlet(servletHolder, "/*");

        LOGGER.info("Starting server on port: {}.'", port);
        server.setHandler(context);
        try {
            server.start();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to start application", e);
        }
    }

    public void stop() {
        try {
            LOGGER.info("Stopping server");
            server.stop();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to stop application", e);

        }
    }

    public static void main(String[] args) {
        int port;
        if (args.length > 0) {
            port = Integer.valueOf(args[0]);
        } else {
            port = 8080;
        }
        new ConsumerApplication(port).run();
    }

}
