package com.launcher.mango.util.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限请求注解
 * only use in Activity
 * 
 * example：
 *  @RequirePermission({Manifest.permission.READ_SMS,
 *  Manifest.permission.RECEIVE_MMS,
 *  Manifest.permission.READ_CONTACTS})
 *  class DemoActivity extend BaseActivity { ... }
 *
 * @author tic
 *         created on 18-6-27
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Security {
    String[] value();
}
