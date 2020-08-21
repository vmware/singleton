package com.vmware.vip.test.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD) //can use in method only.
public @interface TestCase {
	public enum Priority {
		P0, P1, P2, P3
	}
	public enum Type {
		API, UI
	}


	public String id();
	public String name();
	public String feature() default "client";
	public Type type() default Type.API;
	public Priority priority() default Priority.P1;
	public String description() default "";
}