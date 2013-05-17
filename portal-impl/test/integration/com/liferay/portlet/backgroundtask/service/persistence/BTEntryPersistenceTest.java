/**
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portlet.backgroundtask.service.persistence;

import com.liferay.portal.kernel.bean.PortalBeanLocatorUtil;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.test.ExecutionTestListeners;
import com.liferay.portal.kernel.util.IntegerWrapper;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.service.ServiceTestUtil;
import com.liferay.portal.service.persistence.BasePersistence;
import com.liferay.portal.service.persistence.PersistenceExecutionTestListener;
import com.liferay.portal.test.LiferayPersistenceIntegrationJUnitTestRunner;
import com.liferay.portal.test.persistence.TransactionalPersistenceAdvice;

import com.liferay.portlet.backgroundtask.NoSuchEntryException;
import com.liferay.portlet.backgroundtask.model.BTEntry;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import org.junit.runner.RunWith;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Brian Wing Shun Chan
 */
@ExecutionTestListeners(listeners =  {
	PersistenceExecutionTestListener.class})
@RunWith(LiferayPersistenceIntegrationJUnitTestRunner.class)
public class BTEntryPersistenceTest {
	@After
	public void tearDown() throws Exception {
		Map<Serializable, BasePersistence<?>> basePersistences = _transactionalPersistenceAdvice.getBasePersistences();

		Set<Serializable> primaryKeys = basePersistences.keySet();

		for (Serializable primaryKey : primaryKeys) {
			BasePersistence<?> basePersistence = basePersistences.get(primaryKey);

			try {
				basePersistence.remove(primaryKey);
			}
			catch (Exception e) {
				if (_log.isDebugEnabled()) {
					_log.debug("The model with primary key " + primaryKey +
						" was already deleted");
				}
			}
		}

		_transactionalPersistenceAdvice.reset();
	}

	@Test
	public void testCreate() throws Exception {
		long pk = ServiceTestUtil.nextLong();

		BTEntry btEntry = _persistence.create(pk);

		Assert.assertNotNull(btEntry);

		Assert.assertEquals(btEntry.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		BTEntry newBTEntry = addBTEntry();

		_persistence.remove(newBTEntry);

		BTEntry existingBTEntry = _persistence.fetchByPrimaryKey(newBTEntry.getPrimaryKey());

		Assert.assertNull(existingBTEntry);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addBTEntry();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = ServiceTestUtil.nextLong();

		BTEntry newBTEntry = _persistence.create(pk);

		newBTEntry.setGroupId(ServiceTestUtil.nextLong());

		newBTEntry.setCompanyId(ServiceTestUtil.nextLong());

		newBTEntry.setUserId(ServiceTestUtil.nextLong());

		newBTEntry.setUserName(ServiceTestUtil.randomString());

		newBTEntry.setCreateDate(ServiceTestUtil.nextDate());

		newBTEntry.setModifiedDate(ServiceTestUtil.nextDate());

		newBTEntry.setName(ServiceTestUtil.randomString());

		newBTEntry.setServletContextNames(ServiceTestUtil.randomString());

		newBTEntry.setTaskExecutorClassName(ServiceTestUtil.randomString());

		newBTEntry.setTaskContext(ServiceTestUtil.randomString());

		newBTEntry.setCompleted(ServiceTestUtil.randomBoolean());

		newBTEntry.setCompletionDate(ServiceTestUtil.nextDate());

		newBTEntry.setStatus(ServiceTestUtil.nextInt());

		_persistence.update(newBTEntry);

		BTEntry existingBTEntry = _persistence.findByPrimaryKey(newBTEntry.getPrimaryKey());

		Assert.assertEquals(existingBTEntry.getBtEntryId(),
			newBTEntry.getBtEntryId());
		Assert.assertEquals(existingBTEntry.getGroupId(),
			newBTEntry.getGroupId());
		Assert.assertEquals(existingBTEntry.getCompanyId(),
			newBTEntry.getCompanyId());
		Assert.assertEquals(existingBTEntry.getUserId(), newBTEntry.getUserId());
		Assert.assertEquals(existingBTEntry.getUserName(),
			newBTEntry.getUserName());
		Assert.assertEquals(Time.getShortTimestamp(
				existingBTEntry.getCreateDate()),
			Time.getShortTimestamp(newBTEntry.getCreateDate()));
		Assert.assertEquals(Time.getShortTimestamp(
				existingBTEntry.getModifiedDate()),
			Time.getShortTimestamp(newBTEntry.getModifiedDate()));
		Assert.assertEquals(existingBTEntry.getName(), newBTEntry.getName());
		Assert.assertEquals(existingBTEntry.getServletContextNames(),
			newBTEntry.getServletContextNames());
		Assert.assertEquals(existingBTEntry.getTaskExecutorClassName(),
			newBTEntry.getTaskExecutorClassName());
		Assert.assertEquals(existingBTEntry.getTaskContext(),
			newBTEntry.getTaskContext());
		Assert.assertEquals(existingBTEntry.getCompleted(),
			newBTEntry.getCompleted());
		Assert.assertEquals(Time.getShortTimestamp(
				existingBTEntry.getCompletionDate()),
			Time.getShortTimestamp(newBTEntry.getCompletionDate()));
		Assert.assertEquals(existingBTEntry.getStatus(), newBTEntry.getStatus());
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		BTEntry newBTEntry = addBTEntry();

		BTEntry existingBTEntry = _persistence.findByPrimaryKey(newBTEntry.getPrimaryKey());

		Assert.assertEquals(existingBTEntry, newBTEntry);
	}

	@Test
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = ServiceTestUtil.nextLong();

		try {
			_persistence.findByPrimaryKey(pk);

			Assert.fail("Missing entity did not throw NoSuchEntryException");
		}
		catch (NoSuchEntryException nsee) {
		}
	}

	@Test
	public void testFindAll() throws Exception {
		try {
			_persistence.findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				getOrderByComparator());
		}
		catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	protected OrderByComparator getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create("BTEntry", "btEntryId",
			true, "groupId", true, "companyId", true, "userId", true,
			"userName", true, "createDate", true, "modifiedDate", true, "name",
			true, "servletContextNames", true, "taskExecutorClassName", true,
			"taskContext", true, "completed", true, "completionDate", true,
			"status", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		BTEntry newBTEntry = addBTEntry();

		BTEntry existingBTEntry = _persistence.fetchByPrimaryKey(newBTEntry.getPrimaryKey());

		Assert.assertEquals(existingBTEntry, newBTEntry);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = ServiceTestUtil.nextLong();

		BTEntry missingBTEntry = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingBTEntry);
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery = new BTEntryActionableDynamicQuery() {
				@Override
				protected void performAction(Object object) {
					BTEntry btEntry = (BTEntry)object;

					Assert.assertNotNull(btEntry);

					count.increment();
				}
			};

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting()
		throws Exception {
		BTEntry newBTEntry = addBTEntry();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(BTEntry.class,
				BTEntry.class.getClassLoader());

		dynamicQuery.add(RestrictionsFactoryUtil.eq("btEntryId",
				newBTEntry.getBtEntryId()));

		List<BTEntry> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		BTEntry existingBTEntry = result.get(0);

		Assert.assertEquals(existingBTEntry, newBTEntry);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(BTEntry.class,
				BTEntry.class.getClassLoader());

		dynamicQuery.add(RestrictionsFactoryUtil.eq("btEntryId",
				ServiceTestUtil.nextLong()));

		List<BTEntry> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting()
		throws Exception {
		BTEntry newBTEntry = addBTEntry();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(BTEntry.class,
				BTEntry.class.getClassLoader());

		dynamicQuery.setProjection(ProjectionFactoryUtil.property("btEntryId"));

		Object newBtEntryId = newBTEntry.getBtEntryId();

		dynamicQuery.add(RestrictionsFactoryUtil.in("btEntryId",
				new Object[] { newBtEntryId }));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingBtEntryId = result.get(0);

		Assert.assertEquals(existingBtEntryId, newBtEntryId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(BTEntry.class,
				BTEntry.class.getClassLoader());

		dynamicQuery.setProjection(ProjectionFactoryUtil.property("btEntryId"));

		dynamicQuery.add(RestrictionsFactoryUtil.in("btEntryId",
				new Object[] { ServiceTestUtil.nextLong() }));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	protected BTEntry addBTEntry() throws Exception {
		long pk = ServiceTestUtil.nextLong();

		BTEntry btEntry = _persistence.create(pk);

		btEntry.setGroupId(ServiceTestUtil.nextLong());

		btEntry.setCompanyId(ServiceTestUtil.nextLong());

		btEntry.setUserId(ServiceTestUtil.nextLong());

		btEntry.setUserName(ServiceTestUtil.randomString());

		btEntry.setCreateDate(ServiceTestUtil.nextDate());

		btEntry.setModifiedDate(ServiceTestUtil.nextDate());

		btEntry.setName(ServiceTestUtil.randomString());

		btEntry.setServletContextNames(ServiceTestUtil.randomString());

		btEntry.setTaskExecutorClassName(ServiceTestUtil.randomString());

		btEntry.setTaskContext(ServiceTestUtil.randomString());

		btEntry.setCompleted(ServiceTestUtil.randomBoolean());

		btEntry.setCompletionDate(ServiceTestUtil.nextDate());

		btEntry.setStatus(ServiceTestUtil.nextInt());

		_persistence.update(btEntry);

		return btEntry;
	}

	private static Log _log = LogFactoryUtil.getLog(BTEntryPersistenceTest.class);
	private BTEntryPersistence _persistence = (BTEntryPersistence)PortalBeanLocatorUtil.locate(BTEntryPersistence.class.getName());
	private TransactionalPersistenceAdvice _transactionalPersistenceAdvice = (TransactionalPersistenceAdvice)PortalBeanLocatorUtil.locate(TransactionalPersistenceAdvice.class.getName());
}