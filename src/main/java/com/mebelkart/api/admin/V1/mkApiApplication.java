package com.mebelkart.api.admin.V1;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class mkApiApplication extends Application<mkApiConfiguration> {

    public static void main(final String[] args) throws Exception {
        new mkApiApplication().run(args);
    }

    @Override
    public String getName() {
        return "mkApi";
    }

    @Override
    public void initialize(final Bootstrap<mkApiConfiguration> bootstrap) {
        // TODO: application initialization
    }

    @Override
    public void run(final mkApiConfiguration configuration,
                    final Environment environment) {
        // TODO: implement application
    }

}
