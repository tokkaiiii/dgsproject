package com.sahara.dgsproject

import org.jooq.codegen.DefaultGeneratorStrategy
import org.jooq.codegen.GeneratorStrategy
import org.jooq.codegen.GeneratorStrategy.Mode.*
import org.jooq.meta.Definition

class PrefixGeneratorStrategy : DefaultGeneratorStrategy() {
    override fun getJavaClassName(definition: Definition?, mode: GeneratorStrategy.Mode?): String {
        return when(mode){
            DEFAULT -> super.getJavaClassName(definition, mode) + "_"
            else -> super.getJavaClassName(definition, mode)
        }
    }
}