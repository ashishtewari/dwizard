package com.mebelkart.api.admin;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class mk_APIApplication extends Application<mk_APIConfiguration> {

    public static void main(final String[] args) throws Exception {
        new mk_APIApplication().run(args);
    }

    @Override
    public String getName() {
        return "mk_API";
    }

    @Override
    public void initialize(final Bootstrap<mk_APIConfiguration> bootstrap) {
        // TODO: application initialization
    }

    @Override
    public void run(final mk_APIConfiguration configuration,
                    final Environment environment) {
        // TODO: implement application
    }

}
