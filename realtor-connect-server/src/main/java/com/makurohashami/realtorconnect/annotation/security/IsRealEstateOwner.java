package com.makurohashami.realtorconnect.annotation.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.security.access.prepost.PreAuthorize;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("@permissionService.isRealEstateOwner(#realEstateId) or hasAuthority('MANAGE_REALTOR_INFO')")
public @interface IsRealEstateOwner {
}
