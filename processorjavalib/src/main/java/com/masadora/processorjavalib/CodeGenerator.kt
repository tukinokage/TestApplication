package com.masadora.processorjavalib

import com.squareup.javapoet.CodeBlock

/**
 * PACK com.masadora.processorjavalib
 * CREATE BY Shay
 * DATE BY 2022/12/9 14:52 星期五
 * <p>
 * DESCRIBE
 * <p>
 */
// TODO:2022/12/9
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
        fun getAddElementCode(vararg element:String): CodeBlock
    }
