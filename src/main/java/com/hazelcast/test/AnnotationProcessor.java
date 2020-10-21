package com.hazelcast.test;

import com.sun.source.util.Trees;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.Collections;
import java.util.Set;

@SuppressWarnings("unused")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({"com.hazelcast.test.OnlyForTesting", "com.hazelcast.test.PublicForTesting"})
public class AnnotationProcessor extends AbstractProcessor {

    private boolean release;

    @Override
    public Set<String> getSupportedOptions() {
        return Collections.singleton("only.for.testing.release");
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        release = processingEnv.getOptions().containsKey("only.for.testing.release");
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Trees trees = Trees.instance(processingEnv);

        if (release) {
            Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(OnlyForTesting.class);
            for (Element methodElement : elements) {
                Element classElement = methodElement.getEnclosingElement();

                JCTree.JCMethodDecl method = (JCTree.JCMethodDecl) trees.getTree(methodElement);
                JCTree.JCClassDecl class_ = (JCTree.JCClassDecl) trees.getTree(classElement);

                class_.defs = List.filter(class_.defs, method);
            }
        } else {
            Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(PublicForTesting.class);
            for (Element methodElement : elements) {
                JCTree.JCMethodDecl method = (JCTree.JCMethodDecl) trees.getTree(methodElement);
                method.mods.flags = method.mods.flags & ~0b111 | 1;
            }
        }

        return true;
    }

}
