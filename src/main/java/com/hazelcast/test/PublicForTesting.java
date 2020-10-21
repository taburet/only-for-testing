package com.hazelcast.test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@SuppressWarnings("unused")
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface PublicForTesting {

}
