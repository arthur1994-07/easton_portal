package com.common.easton_portal.constants;

import com.common.easton_portal.permission.LoginRightAnnotation;
import com.common.easton_portal.permission.RightType;

public class PermissionConstant {
    @LoginRightAnnotation(displayKey = "fullAccessPermission", type = RightType.SuperUser)
    public final static String FULL_ACCESS_PERMISSION = "full_access_permission_superuser";
    @LoginRightAnnotation(displayKey = "editUser", type = RightType.Normal)
    public final static String EDIT_USER = "edit_user";
}