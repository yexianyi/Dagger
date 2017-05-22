package net.yxy.dagger.rest;

import org.glassfish.jersey.server.ResourceConfig;

public class RestApplication extends ResourceConfig {
	 public RestApplication() {
	     // add rest service packages
	     packages("net.yxy.dagger.rest");
	 }
}
