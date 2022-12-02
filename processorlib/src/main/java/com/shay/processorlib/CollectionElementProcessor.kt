
import com.google.auto.service.AutoService
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import java.io.File
import java.util.logging.Logger
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement

/**
 * PACK com.masadoraandroid.site.annotation
 * CREATE BY Shay
 * DATE BY 2022/12/1 11:25 星期四
 * <p>
 * DESCRIBE
 * <p>
 */
// TODO:2022/12/1 
@AutoService(Processor::class)
class CollectionElementProcessor: AbstractProcessor() {
    override fun process(
        annotations: MutableSet<out TypeElement>?,
        roundEnv: RoundEnvironment?
    ): Boolean {
        val diffTypeElementMap:MutableMap<String, MutableList<TypeElement>> = mutableMapOf()
        if (roundEnv != null) {
            //获取所有CollectionElement注解的元素
            var elements =
                roundEnv.getElementsAnnotatedWith(CollectionElement::class.java)
            elements.forEach {
                //把有该注解的标注的元素转为可以获取注解类/域的信息的typeElement
                val typeElement = it as TypeElement

                //获取该注解元素下的CollectionElement注解的信息
                var curClassElement = typeElement.getAnnotation(CollectionElement::class.java)
                //var className = typeElement.simpleName.toString()
                val collectionName = curClassElement.collectionName
                if (!diffTypeElementMap.containsKey(collectionName)){//当前集合分类是否已存在
                    diffTypeElementMap[collectionName] = mutableListOf(typeElement)//归类为同一集合分类下的typeElement
                }else{
                    diffTypeElementMap[collectionName]?.add(typeElement)
                }
            }

            generateClass( diffTypeElementMap)
        }
    }

    fun generateClass(typeElementMap:MutableMap<String, MutableList<TypeElement>>){
        for (entry in typeElementMap.entries){
            val collectionName = entry.key
            MethodSpec.methodBuilder("get$collectionName").addModifiers(Modifier.PUBLIC)
        }
    }
}