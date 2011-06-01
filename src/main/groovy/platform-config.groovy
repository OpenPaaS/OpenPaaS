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
 
platformConfig {  
  provisioningProperties {
    provider "EC2"
    providerProperties ["awsKey":"asdfasdfasdf", "awsSecretKey":"asdfasdfasdfa"]
	imageTypes ["bigAmazonImage" = {imageId = "ami345566677"; hardwareId = "m1.large", locationId="u1-east"}, 
	            "smallAmazonImage" = {imageId = "ami345566677"; hardwareId = "m1.small"},
				"mediumAmazonImage" = {imageId = "ami345566677"; minCores=2, minRam=1024.MB}]	



  }

  scalingUnits [
    small {
      memory = 256.MB
      cpuCores = 2.CpuCores
      diskSpace = 20.GB	  
      reservedDiskSpace = 5GB
      reservedCpuCores = 0.1
    },
    medium {
      memory = 512.MB
      cpuCores = 4.CpuCores
      diskSpace = 40.GB	  
    },
    large {
      memory = 1024.MB
      cpuCores = 8.CpuCores
      diskSpace = 80.GB	  
    }
  ]

}