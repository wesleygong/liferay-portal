/**
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
 *
 * This file is part of Liferay Social Office. Liferay Social Office is free
 * software: you can redistribute it and/or modify it under the terms of the GNU
 * Affero General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * Liferay Social Office is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License
 * for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Liferay Social Office. If not, see http://www.gnu.org/licenses/agpl-3.0.html.
 */

package com.liferay.util.bridges.alloy;

import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.NoSuchResourceActionException;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.security.permission.ResourceActionsUtil;
import com.liferay.portal.theme.PortletDisplay;
import com.liferay.portal.theme.ThemeDisplay;

/**
 * @author Ethan Bustad
 */
public class AlloyPermission {

	public static boolean contains(
			ThemeDisplay themeDisplay, String controller, String actionId)
		throws Exception {

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		actionId = actionId.toUpperCase() + StringPool.UNDERLINE +
			controller.toUpperCase();

		try {
			ResourceActionsUtil.checkAction(
				portletDisplay.getRootPortletId(), actionId);
		}
		catch (NoSuchResourceActionException e) {
			return true;
		}

		PermissionChecker permissionChecker =
			themeDisplay.getPermissionChecker();

		return permissionChecker.hasPermission(
			themeDisplay.getScopeGroupId(), portletDisplay.getRootPortletId(),
			portletDisplay.getResourcePK(), actionId);
	}

}