package org.yah.meccanobuilder.server;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.yah.meccanobuilder.model.importer.csv.CsvImporter;
import org.yah.meccanobuilder.model.repository.PartsRepository;
import org.yah.meccanobuilder.server.assets.AssetsProxyBundle;
import org.yah.meccanobuilder.server.healthcheck.DatabaseHealthCheck;
import org.yah.meccanobuilder.server.metrics.PartsRepositoryMetricsFactory;

import java.io.IOException;

public class MBServer extends Application<MBServerConfiguration> {

    public static void main(String[] args) throws Exception {
        new MBServer().run(args);
    }

    @Override
    public void initialize(Bootstrap<MBServerConfiguration> bootstrap) {
        bootstrap.addBundle(AssetsProxyBundle.create(MBServerConfiguration::getAssetsServer));
    }

    @Override
    public void run(MBServerConfiguration configuration, Environment environment) throws IOException {
        final CsvImporter importer = new CsvImporter();
        final PartsRepository partsRepository = importer.importRepository();
        environment.jersey().register(new ServicesBinder(partsRepository));
        environment.jersey().packages("org.yah.meccanobuilder.server");
        environment.healthChecks().register("partsRepository", new DatabaseHealthCheck());
        final PartsRepositoryMetricsFactory metricsFactory = new PartsRepositoryMetricsFactory(partsRepository);
        metricsFactory.createMetrics(environment.metrics());
    }

    private static final class ServicesBinder extends AbstractBinder {
        private final PartsRepository partsRepository;

        public ServicesBinder(PartsRepository partsRepository) {
            this.partsRepository = partsRepository;
        }

        @Override
        protected void configure() {
            bind(partsRepository).named("partsRepository");
        }
    }
}
