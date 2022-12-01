

import java.lang.annotation.RetentionPolicy

/**
 * PACK com.masadoraandroid.site.annotation
 * CREATE BY Shay
 * DATE BY 2022/11/30 18:45 星期三
 * <p>
 * DESCRIBE
 * <p>
 */
// TODO:2022/11/30 
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class  CollectionElement(val collectionName: String = "Default")