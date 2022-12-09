package com.masadora.processorjavalib;

import com.google.auto.common.MoreElements;
import com.google.auto.service.AutoService;
import com.masadora.processorjavalib.annotation.CollectionElement;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

import static com.squareup.javapoet.TypeSpec.classBuilder;

/**
 * PACK com.masadora.processorjavalib
 * CREATE BY Shay
 * DATE BY 2022/12/9 14:29 星期五
 * <p>
 * DESCRIBE
 * <p>
 */
// TODO:2022/12/9 

@AutoService(Processor.class)
public class CollectionElementProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        HashMap<String, List<TypeElement>> diffTypeElementMap = new HashMap();
        if (roundEnv != null) {
            //获取所有CollectionElement注解的元素
            Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(CollectionElement.class);
            for (Element it:elements) {
                //把有该注解的标注的元素转为可以获取注解类/域的信息的typeElement
                TypeElement typeElement = (TypeElement)it;

                //获取该注解元素下的CollectionElement注解的信息
                CollectionElement curClassElement = typeElement.getAnnotation(CollectionElement.class);
                //var className = typeElement.simpleName.toString()
                String collectionName = curClassElement.collectionName() == null? "Default":curClassElement.collectionName();
                if (!diffTypeElementMap.containsKey(collectionName)){//当前集合分类是否已存在
                    diffTypeElementMap.put(collectionName, new ArrayList<TypeElement>(){{add(typeElement);}});//归类为同一集合分类下的typeElement
                }else{
                   diffTypeElementMap.get(collectionName).add(typeElement);
                }
            }

            generateClass(diffTypeElementMap);
            return true;
        }
        return false;
    }

    private void generateClass(HashMap<String, List<TypeElement>> typeElementMap) {
        for (Map.Entry<String, List<TypeElement>> entry : typeElementMap.entrySet()){
            String collectionName = entry.getKey();
            String collectionTypeName = Object.class.getSimpleName();
            Class collectionType = Object.class;
            final String GEN_CLASS_NAME = "CollectionOf" + collectionName;

            BuildFunc buildFunc = CodeGeneratorKt.builderTypeFactory(Type.ARRAYLIST);//
            MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("get$collectionName")
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .returns(ArrayList.class)
            // .addStatement("ArrayList<$collectionType> list = new ArrayList()")
                .addStatement(buildFunc.getCreateCollectionCode(collectionTypeName));//创建集合对象的代码

            String packageName = "";
            String objectReferName = "ref";
            methodBuilder.addStatement(buildFunc.getCreateCollectionCode(objectReferName));
            for (TypeElement typeElement: entry.getValue()){//取出同一集合类型下的类的typeelement
                packageName = MoreElements.getPackage(typeElement).getQualifiedName().toString();
                String className = typeElement.getQualifiedName().toString();

                /*var packPath = getPackPath(simpleName)
                Class.forName("$packPath.$simpleName")*/
                /**创建添加到集合中对象的代码*/
                methodBuilder.addStatement("$T " + objectReferName +" = Class.forName($S).getConstructor().newInstance()", collectionType, className);
                methodBuilder.addStatement(buildFunc.getAddElementCode(objectReferName));
            }

            TypeSpec.Builder tb = classBuilder(GEN_CLASS_NAME)
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    /*.addJavadoc("")*/
                    .addMethod(methodBuilder.build());

            try {
                JavaFile javaFile = JavaFile.builder(packageName, tb.build())
                        .addFileComment(" This codes are generated automatically. Do not modify!")
                        .build();
                // write to file
                javaFile.writeTo(processingEnv.getFiler());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(CollectionElementProcessor.class.getCanonicalName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
