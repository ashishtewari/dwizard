package com.mebelkart.api.util.cronTasks.Tasks;

import de.spinscale.dropwizard.jobs.Job;
import de.spinscale.dropwizard.jobs.annotations.OnApplicationStart;

/**
 * Created by vinitpayal on 01/04/16.
 */
@OnApplicationStart
public class ManufacturerIndex extends Job{

    @Override
    public void doJob() {
        System.out.println("Manufacturer indexing started");



    }
}
