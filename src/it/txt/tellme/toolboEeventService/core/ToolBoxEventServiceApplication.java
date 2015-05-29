/*******************************************************************************
 * Copyright (c) 2011 - 2012 TXT e-solutions SpA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * This work was performed within the IoT_at_Work Project
 * and partially funded by the European Commission's
 * 7th Framework Programme under the research area ICT-2009.1.3
 * Internet of Things and enterprise environments.
 *
 *
 * Authors:
 *     Sancesario Raffaele (TXT e-solutions SpA)
 *
 * Contributors:
 *      Domenico Rotondi (TXT e-solutions SpA)
 *******************************************************************************/

package it.txt.tellme.toolboEeventService.core;



import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Restlet;
import org.restlet.data.Protocol;
import org.restlet.routing.Router;


public class ToolBoxEventServiceApplication extends Application 
{
	
    
	public static void main(String[] args) throws Exception {
        Component c = new Component();
        c.getServers().add(Protocol.HTTP, 8182);
        c.getClients().add(Protocol.CLAP);
        c.getDefaultHost().attach(new ToolBoxEventServiceApplication());
        c.start();
  
    }
 
	
	
    public Restlet createInboundRoot() 
    {
        Router router = new Router(getContext());
    	
    	//osmose
        router.attach("/users", Users.class);
    	router.attach("/issues", Issues.class);
    	router.attach("/sessions", Sessions.class);
    	router.attach("/simulators", Simulators.class);
    	router.attach("/types", Types.class);
    	router.attach("/components", Components.class);
    	router.attach("/maintenances", Maintenances.class);
    	
    	return router;
    }


}
