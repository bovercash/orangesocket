package com.mika.orange.camel;

import java.util.List;

import org.apache.log4j.Logger;

import org.apache.camel.CamelContext;
import org.apache.camel.Route;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

public class Camel {
	private static final Logger log = Logger.getLogger(Camel.class);
	private CamelContext context;
	private RouteBuilder[] routeBuilders;
	private boolean running;
	
	public Camel(RouteBuilder ... routeBuilders){
		this(new DefaultCamelContext(), routeBuilders);		
	}
	
	public Camel(CamelContext context, RouteBuilder ... routeBuilders){
		this.context = context;
		this.routeBuilders = routeBuilders;
	}
	
	public void start() throws Exception {
		if(running){
			throw new IllegalStateException("Camel is already running");
		}
		
		for(RouteBuilder routeBuilder : routeBuilders){
			context.addRoutes(routeBuilder);
		}
		
	    context.start();
	    running = true;
	    log.info("Camel routes have been started");
	}
	
	public void stop(){
		if(!running){
			throw new IllegalStateException("Camel is already stopped");
		}
		log.info("Begin stopping Camel routes");
		boolean atLeastOneError = false;
		List<Route> routes = context.getRoutes();
		for(Route route : routes){
			String routeId = route.getId();
			try{			
				context.stopRoute(routeId);
			}catch(Exception e){
				log.warn(String.format("Could Not Stop Route \"%s\"", routeId),e);
				atLeastOneError = true;
			}
		}
		if(atLeastOneError){
			log.info("Completed stopping Camel routes, but at least one route is still running");
		}else{
			running = false;
			log.info("Completed stopping Camel routes");
		}
	}

	public boolean isRunning(){
		return running;
	}
	
	public RouteBuilder[] getRouteBuilders() {
		return routeBuilders;
	}
	
	public CamelContext getCamelContext(){
		return this.context;
	}

}
