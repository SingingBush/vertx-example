package com.singingbush.example;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.config.MeterFilter;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.micrometer.MicrometerMetricsOptions;
import io.vertx.micrometer.VertxPrometheusOptions;
import io.vertx.micrometer.backends.BackendRegistries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppMain {

  private static final Logger log = LoggerFactory.getLogger(AppMain.class);

  /*
   * This main method is purely for setting up Prometheus
   */
  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx(new VertxOptions()
      .setMetricsOptions(new MicrometerMetricsOptions()
        .setEnabled(true)
        .setPrometheusOptions(new VertxPrometheusOptions()
          .setEnabled(true)
        )
      )
    );

    /*
     After the Vert.x instance has been created,
     we can configure the metrics registry to enable histogram buckets
     for percentile approximations.
     */
    log.info("Configuring Prometheus metrics");

    PrometheusMeterRegistry registry = (PrometheusMeterRegistry) BackendRegistries.getDefaultNow();
    registry.config().meterFilter(
      new MeterFilter() {
        @Override
        public DistributionStatisticConfig configure(Meter.Id id, DistributionStatisticConfig config) {
          return DistributionStatisticConfig.builder()
            .percentilesHistogram(true)
            .build()
            .merge(config);
        }
      });

    vertx.deployVerticle(new MainVerticle());
  }

}
