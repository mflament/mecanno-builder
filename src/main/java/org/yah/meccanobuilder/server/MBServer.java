package org.yah.meccanobuilder.server;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.yah.meccanobuilder.server.assets.AssetsProxyBundle;
import org.yah.meccanobuilder.server.healthcheck.DatabaseHealthCheck;

public class MBServer extends Application<MBServerConfiguration> {

    public static void main(String[] args) throws Exception {
        new MBServer().run(args);
    }

    @Override
    public void initialize(Bootstrap<MBServerConfiguration> bootstrap) {
        bootstrap.addBundle(AssetsProxyBundle.create(MBServerConfiguration::getAssetsServer));
    }

    @Override
    public void run(MBServerConfiguration configuration, Environment environment) {
        environment.jersey().packages("org.yah.meccanobuilder.server.resources");
        environment.jersey().register(new ServicesBinder(environment, configuration));
        environment.healthChecks().register("database", new DatabaseHealthCheck());
    }

    private static final class ServicesBinder extends AbstractBinder {
        private final Environment environment;
        private final MBServerConfiguration configuration;

        private ServicesBinder(Environment environment, MBServerConfiguration configuration) {
            this.environment = environment;
            this.configuration = configuration;
        }

        @Override
        protected void configure() {
        }
    }
}
