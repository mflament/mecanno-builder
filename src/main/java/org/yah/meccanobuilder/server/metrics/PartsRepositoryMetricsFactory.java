package org.yah.meccanobuilder.server.metrics;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import org.yah.meccanobuilder.model.repository.PartsRepository;

public class PartsRepositoryMetricsFactory {

    private final PartsRepository partsRepository;

    public PartsRepositoryMetricsFactory(PartsRepository partsRepository) {
        this.partsRepository = partsRepository;
    }

    public void createMetrics(MetricRegistry metricRegistry){
        metricRegistry.gauge("parts.repository.sets", this::sets);
        metricRegistry.gauge("parts.repository.parts", this::parts);
        metricRegistry.gauge("parts.repository.colors", this::colors);
        metricRegistry.gauge("parts.repository.materials", this::materials);
        metricRegistry.gauge("parts.repository.setparts", this::setParts);
    }

    public Gauge<Integer> sets() {
        return () -> partsRepository.getSets().size();
    }

    public Gauge<Integer> parts() {
        return () -> partsRepository.getParts().size();
    }

    public Gauge<Integer> colors() {
        return () -> partsRepository.getColors().size();
    }

    public Gauge<Integer> materials() {
        return () -> partsRepository.getMaterials().size();
    }

    public Gauge<Integer> setParts() {
        return () -> partsRepository.getSetParts().size();
    }
}
