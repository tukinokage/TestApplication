
import com.google.auto.service.AutoService
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeSpec
import com.squareup.javapoet.TypeSpec.classBuilder
import sun.tools.jconsole.Messages.CLASS_NAME
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
 * DESCRIBE 此处使用的是javapoet，并没有使用kpoet
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
            val collectionType = Object::class.java.simpleName

            var buildFunc = builderTypeFactory(Type.ARRAYLIST)//
            var methodBuilder = MethodSpec.methodBuilder("get$collectionName")
                .addModifiers(Modifier.PUBLIC)
                .returns(ArrayList::class.java)
               // .addStatement("ArrayList<$collectionType> list = new ArrayList()")
                .addStatement(buildFunc.getCreateCollectionCode(collectionType))//创建集合对象的代码

            for (typeElement in entry.value){//取出同一集合类型下的类的typeelement
                var className = typeElement.qualifiedName
                var objectName = "ref"
                /*var packPath = getPackPath(simpleName)
                Class.forName("$packPath.$simpleName")*/
                /**创建添加到集合中对象的代码*/
                methodBuilder.addStatement("%s %s = Class.forName(\"%s\").getConstructor().newInstance()", collectionType, objectName, className)
                methodBuilder.addStatement(buildFunc.getAddElementCode(objectName))
            }

            val tb: TypeSpec.Builder = classBuilder(CLASS_NAME).addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addJavadoc("").addMethod(methodBuilder.build())

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
    }


    /**
     * 用于对需要对部分代码进行过滤操作时
     * */
    class AddParamsDisposable(val func:BuildFunc):BuildFunc by func{
        override fun getCreateCollectionCode(vararg collectionEleType: String): String {
            func.getCreateCollectionCode(*collectionEleType)
        }
    }

    class ArrayListBuildFunc :BuildFunc{
        override fun getCreateCollectionCode(vararg collectionEleType:String): String {
            return "ArrayList<${collectionEleType[0]}> collection = new ArrayList()"
        }

        override fun getAddElementCode(vararg element: String): String {
            return "collection.add(${element[0]})"
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
        fun getCreateCollectionCode(vararg collectionEleType:String):String
        /**
         * @param element 添加到集合中元素的名称
         * */
        fun getAddElementCode(vararg element:String):String
    }
}