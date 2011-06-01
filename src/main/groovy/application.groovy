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
 application {
	name "sdf"
	defaultPlatformConfig "ec2.groovy"
	services {
		service {
			serviceName "tomcat"
			recipeFile "tomcat.groovy"
			initialInstances 1
			minInstances 1
			maxInstances 5			 
		}
		service { 
			serviceName "cassandra"
			recipeFile "tomcat.groovy"
			initialInstances 2
			minInstances 1
			maxInstances 5
		} 		
	}

}
