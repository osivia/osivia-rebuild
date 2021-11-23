/*
 * (C) Copyright 2017 OSIVIA (http://www.osivia.com) 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 */
package org.osivia.portal.core.batch;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.batch.AbstractBatch;
import org.osivia.portal.api.locator.Locator;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Job used to control the execution of the batch
 * 
 * @author Loïc Billon
 *
 */
public class BatchJob implements Job {

	private final static Log logger = LogFactory.getLog("batch");

	public BatchJob() {
	}

	@SuppressWarnings("unchecked")
    @Override
	public void execute(JobExecutionContext context) throws JobExecutionException {

		// Get the instance of batch
		JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
		JobDataMap triggerDataMap = context.getTrigger().getJobDataMap();

		Object object = jobDataMap.get("instance");

		if (object instanceof AbstractBatch) {

			try {

				AbstractBatch b = (AbstractBatch) object;

				// Clustering, check if this batch can run in all nodes or only on master
				/*if (b.isRunningOnMasterOnly()) {

					HABatchDeployer haBean = Locator.findMBean(HABatchDeployer.class, HABatchDeployer.MBEAN_NAME);

					if (haBean.isMaster()) {

						logger.debug("We are on the master node, run the batch " + context.getJobDetail().getName());

						b.execute(triggerDataMap);

					} else {
						logger.debug("We are on a slave node, skip the batch " + context.getJobDetail().getName());

					}
				} else
				
				*/
				{
					b.execute(triggerDataMap);
				}

			} catch (PortalException e) {
				logger.error(e);
			}

		} else {
			throw new JobExecutionException("Job is not an instance of AbstractBatch.");
		}

	}

}
