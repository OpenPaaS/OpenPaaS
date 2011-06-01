/*
 * Copyright 20010-2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 service {
	//name of the service 
	name 'tomcat'
	//the template within the platform configuration file 
	imageTemplate 'big'
	//specifies if the service can be dynamically scaled out or in 
	isElastic true
	//profiles - to support multiple runtime environment 
	profiles  {
		profile {
			profileName 'dev'
			scalingUnit {
				memory 256.MB 
				cpuCores 1.CpuCores 
				diskSpace 1.GB
			}  
			env (['port':autoport(), 'maxMemory':1.GB, 'dbUrl':service['otherser'].anyInstance.adgress])		
		}
		profile {
			profileName 'prod'
			scalingUnit {
				memory 256.MB
				cpuCores 2.CpuCores
				diskSpace 20.GB
			}
			env (['port':80, 'maxMemory':256.MB, 'dbUrl':service['otherser'].anyInstance.adgress])
		}			
	}		

    lifecycle {
	//life cycle events can be shell scripts or groovy closures that receive the service context as a parameter
	//here you can implement logic to download binaries from any repository or file server

		install {serviceContext ->
	  		//inject runtime parameters to a config file / script using a utility class
			serviceContext.templateReplacer.replace('server.xml', serviceContext.env)
			'install.sh'.execute();
		}
		uninstall 'uninstall.sh'
		relocate 'relocate.sh'
		preStart 'pre-start.sh'
		start (['win':'np.bat', 'linux':'np.sh'])
		postStart ({ServiceContext serviceContext ->
			serviceContext.serviceInstance.localHostAddress.hostName;
			TomcatDeployer.deployModule {
				contextPath 'asdfasdf'
				warFile 'myApp.war'
			}
		})
		preShutdown "pre-shutdown.sh"
		shutdown "shutdown.sh"
		postShutdown "post-shutdown.sh"
		//custom commands to be invoked from a shell or another management interface 
		customCommands {
			command {
				name 'deployWebApp'
				execute {serviceContext, warFile, contextPath ->
					def localWarFile = getFromS3(warFile)
					TomcatDeployer.deploy(warFile)
				}
			}
			command {
				name 'undeployWebApp'
				//passed as script vars
				paramNames ["warFile", "param2"]
				execute "undeploy.groovy"
			}
			command {
				name = "someOtherCommand"
				//if it's a shell script, passed as env vars
				paramNames ["someotherParam"]
				execute "someothercommand.sh"
			}
		}
    }
			
    monitoringPlugins {
		  plugin {
			pluginClass "org.openspaces.usm.JmxMetricsCollector"
			scope serviceInstance //can also be 'service' which means it runs once across the entire system not on every instance
			config {
			  jmxOperations = ["org.apache.tomcat:SomeGroup:SomeMBean:SomeProperty": "Avg Request Latency",
							   "org.apache.tomcat:SomeGroup:SomeMBean:SomeProperty2": "Live Sessions",
							   "org.apache.tomcat:SomeGroup:SomeMBean:SomeProperty3": "Request Queue"]
			  jmxHost "127.0.0.1"
			  jmxPort 8080
			}
		  }
	      //example for JDBC metrics collector 
		  plugin {
			pluginClass "org.openspaces.usm.JdbcStatementCollector"
			config {
			  driverClass = "..."
			  url = "..."
			  metrics = ["select count(*) from TABLE1":"TABLE1 count",
						 "select count(*) from TABLE2":"TABLE2 count"]
			}
		  }
    }

    

	sla {
	
		scalingRules {
			
			rule {
			  priority = 0
			  metricName = "cpu"
			  statistic = "min"  //supported values min, max, avg. This is across all service instances
			  lowerThreshold = 20
			  upperThreshold= 80
			  duration = 1000.ms // also possible - 2.sec, 1.min, consider how it affects polling
			  lowerBreachIncrement = -1 // remove one scaling unit 
			  upperBreachIncrement = 2 // add two scaling units 
			  cooldownPeriod = 3.minutes
			  //executes after scaling process has been completed 
			  executeAfter = "updateLoadBalancer.groovy"
			}
		}
		alerts {
			when {thisSerice().anyInstace."CPU Utilization".greaterThan(60.Percent).for(1.Minute)}
			raiseAlert {
				severity SEVERE
				description "service intance $serviceNode raised alert"
				//other alert details
		  }
		}
	}
}