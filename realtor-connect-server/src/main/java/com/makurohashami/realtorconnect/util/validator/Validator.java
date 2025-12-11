package com.makurohashami.realtorconnect.util.validator;

import java.util.List;

@FunctionalInterface
public interface Validator<T> {

    List<String> validate(T target);

}
