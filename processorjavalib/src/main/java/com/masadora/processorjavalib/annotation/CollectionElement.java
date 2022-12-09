package com.masadora.processorjavalib.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * PACK com.masadora.processorjavalib.annotation
 * CREATE BY Shay
 * DATE BY 2022/12/9 14:27 星期五
 * <p>
 * DESCRIBE
 * <p>
 */
// TODO:2022/12/9 
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface CollectionElement {
     String collectionName();
}
