package com.cloudtone31.auth;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER) // 파라미터에만 사용
@Retention(value = RetentionPolicy.RUNTIME) // 런타임까지 어노테이션 정보 유지
public @interface LoginUser {
}
