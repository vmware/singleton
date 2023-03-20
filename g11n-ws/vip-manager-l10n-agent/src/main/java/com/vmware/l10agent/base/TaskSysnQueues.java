/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10agent.base;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.vmware.l10agent.model.RecordModel;


/**
 * 
 *
 * @author shihu
 *
 */
public class TaskSysnQueues {
	public final static BlockingQueue<RecordModel> SendComponentTasks = new LinkedBlockingQueue<RecordModel>();
	public final static BlockingQueue<String> InstructTasks = new LinkedBlockingQueue<String>();

}
