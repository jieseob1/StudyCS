package studycs.javalab;

import studycs.javalab.examples.standard.ArraysUtilityExample;
import studycs.javalab.examples.standard.Base64UuidExample;
import studycs.javalab.examples.standard.BigDecimalExample;
import studycs.javalab.examples.standard.CollectionsUtilityExample;
import studycs.javalab.examples.standard.ComparatorExample;
import studycs.javalab.examples.standard.DateTimeExample;
import studycs.javalab.examples.standard.EnumUtilitiesExample;
import studycs.javalab.examples.standard.ObjectsUtilityExample;
import studycs.javalab.examples.standard.OptionalExample;
import studycs.javalab.examples.standard.PathUtilityExample;
import studycs.javalab.examples.standard.RegexExample;
import studycs.javalab.examples.standard.StringUtilitiesExample;

public final class StandardUtilityExamples {
    private StandardUtilityExamples() {
    }

    public static void runDemo() {
        System.out.println();
        System.out.println("== Java standard utility demo ==");

        StringUtilitiesExample.runDemo();
        ObjectsUtilityExample.runDemo();
        OptionalExample.runDemo();
        ArraysUtilityExample.runDemo();
        CollectionsUtilityExample.runDemo();
        ComparatorExample.runDemo();
        DateTimeExample.runDemo();
        RegexExample.runDemo();
        BigDecimalExample.runDemo();
        EnumUtilitiesExample.runDemo();
        Base64UuidExample.runDemo();
        PathUtilityExample.runDemo();
    }
}
