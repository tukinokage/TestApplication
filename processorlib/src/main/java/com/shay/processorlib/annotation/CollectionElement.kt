


/**
 * PACK com.masadoraandroid.site.annotation
 * CREATE BY Shay
 * DATE BY 2022/11/30 18:45 ζζδΈ
 * <p>
 * DESCRIBE
 * <p>
 */
// TODO:2022/11/30
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
@MustBeDocumented
annotation class CollectionElement(val collectionName: String = "Default")
/*
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class CollectionType<T : Any?>(val collectionType:Class<in T> = Object::class.java)*/
