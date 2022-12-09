
import com.google.auto.common.MoreElements
import com.google.auto.service.AutoService
import com.squareup.javapoet.CodeBlock
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeSpec
import com.squareup.javapoet.TypeSpec.classBuilder
import java.io.IOException
import java.util.*
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import kotlin.collections.ArrayList

/**
 * PACK com.masadoraandroid.site.annotation
 * CREATE BY Shay
 * DATE BY 2022/12/1 11:25 星期四
 * <p>
 * DESCRIBE 此处使用的是javapoet，并没有使用kpoet
 * <p>
 */
// TODO:2022/12/1 
@AutoService(Processor::class)
public open class CollectionElementProcessor: AbstractProcessor() {
    override fun init(processingEnv: ProcessingEnvironment?) {
        super.init(processingEnv)
    }

    /**这里你必须指定，这个注解处理器是注册给哪个注解的。注意，它的返回值是一个字符串的集合，包含本处理器想要处理的注解类型的合法全称*/
    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(CollectionElement::class.java.canonicalName)
    }

    override fun process(
        annotations: MutableSet<out TypeElement>?,
        roundEnv: RoundEnvironment?
    ): Boolean {
        println("start processing....")
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
            return true
        }
        return false
    }

    fun generateClass(typeElementMap:MutableMap<String, MutableList<TypeElement>>){
        for (entry in typeElementMap.entries){
            val collectionName = entry.key
            val collectionTypeName = Object::class.java.simpleName
            val collectionType = Object::class.java
            val GEN_CLASS_NAME = "CollectionOf$collectionTypeName"

            var buildFunc = builderTypeFactory(Type.ARRAYLIST)//
            var methodBuilder = MethodSpec.methodBuilder("get$collectionName")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(ArrayList::class.java)
               // .addStatement("ArrayList<$collectionType> list = new ArrayList()")
                .addStatement(buildFunc.getCreateCollectionCode(collectionTypeName))//创建集合对象的代码

            var packageName = ""
            var objectReferName = "ref"
            methodBuilder.addStatement(buildFunc.getCreateCollectionCode(objectReferName))
            for (typeElement in entry.value){//取出同一集合类型下的类的typeelement
                packageName = MoreElements.getPackage(typeElement).qualifiedName.toString()
                var className = typeElement.qualifiedName.toString()

                /*var packPath = getPackPath(simpleName)
                Class.forName("$packPath.$simpleName")*/
                /**创建添加到集合中对象的代码*/
                methodBuilder.addStatement("\$T $objectReferName = Class.forName(\$S).getConstructor().newInstance()", collectionType, className)
                methodBuilder.addStatement(buildFunc.getAddElementCode(objectReferName))
            }

            val tb: TypeSpec.Builder = classBuilder(GEN_CLASS_NAME)
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    /*.addJavadoc("")*/
                    .addMethod(methodBuilder.build())

            try {
                val javaFile = JavaFile.builder(packageName, tb.build())
                    .addFileComment(" This codes are generated automatically. Do not modify!")
                    .build()
                // write to file
                javaFile.writeTo(processingEnv.filer)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    enum class Type{
        ARRAYLIST,
        HASHMAP
    }
    fun builderTypeFactory(type:Type):BuildFunc{
        var func:BuildFunc =
            when(type){
                Type.ARRAYLIST->{
                    return ArrayListBuildFunc()
                }

                /*Type.HASHMAP->{

                }*/
                else->{
                    return ArrayListBuildFunc()
                }
            }

           return AddParamsDisposable(func)
        }


    /**
     * 用于对需要对部分代码进行过滤操作时
     * */
    class AddParamsDisposable(val func:BuildFunc):BuildFunc by func{
        override fun getCreateCollectionCode(vararg collectionEleType: String): CodeBlock {
            /**
             * 此处可进行操作
             *
             * */
            return func.getCreateCollectionCode(*collectionEleType)
        }
    }

    class ArrayListBuildFunc :BuildFunc{
        override fun getCreateCollectionCode(vararg collectionEleType:String): CodeBlock {
            return CodeBlock.builder().addStatement("ArrayList<${collectionEleType[0]}> collection = new ArrayList()").build()
        }

        override fun getAddElementCode(vararg element: String): CodeBlock {
            return  CodeBlock.builder().addStatement("collection.add(${element[0]})").build()
        }
    }
    /**
     * @constructor
     * createCollectionCode 创建集合的代码
     * addElementCode 添加集合元素的代码
     * */
    interface BuildFunc{
      /*  var createCollectionCode:String
        var addElementCode:String*/
        /**
         * @param collectionEleType 添加到集合中元素的类型
         * */
        fun getCreateCollectionCode(vararg collectionEleType:String): CodeBlock
        /**
         * @param element 添加到集合中元素的名称
         * */
        fun getAddElementCode(vararg element:String):CodeBlock
    }
}