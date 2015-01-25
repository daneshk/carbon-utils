/*
 *  Copyright (c) 2005-2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */

package org.wso2.carbon.event.core.sharedmemory;

import org.wso2.carbon.event.core.delivery.MatchingManagerFactory;
import org.wso2.carbon.event.core.delivery.MatchingManager;
import org.wso2.carbon.event.core.exception.EventBrokerException;
import org.wso2.carbon.event.core.exception.EventBrokerConfigurationException;
import org.apache.axiom.om.OMElement;
import org.wso2.carbon.event.core.sharedmemory.util.SharedMemoryCacheUtil;

import javax.cache.Cache;

public class SharedMemoryMatchingManagerFactory implements MatchingManagerFactory {

    //In cluster environment, there should be single SharedMemoryMatchingManager instance.
    //use this is key to store the instance in shared cache.
    private static final int MATCHING_MANAGER_INSTANCE_KEY = 1;
	
    private static Cache<Integer, SharedMemoryMatchingManager> getInMemoryMatchingCache() {
        return SharedMemoryCacheUtil.getInMemoryMatchingCache();
    }

    public MatchingManager getMatchingManager(OMElement config) throws EventBrokerConfigurationException {
        SharedMemoryMatchingManager inMemoryMatchingManager = null ;

        if(getInMemoryMatchingCache().get(MATCHING_MANAGER_INSTANCE_KEY) == null) {
            inMemoryMatchingManager = new SharedMemoryMatchingManager();
            getInMemoryMatchingCache().put(MATCHING_MANAGER_INSTANCE_KEY, inMemoryMatchingManager);
        }

        try {
            //call initialize tenant for super tenant
            inMemoryMatchingManager.initializeTenant();
        } catch (EventBrokerException e) {
            throw new EventBrokerConfigurationException("Can not initialize the in memory matching manager", e);
        }
        return inMemoryMatchingManager;
    }
}
