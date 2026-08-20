package com.nutricare.nutricare_api.archunit;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAnyPackage;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

class CleanArchitectureTest {

    private static JavaClasses classes;

    @BeforeAll
    static void importClasses() {
        classes = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("com.nutricare.nutricare_api");
    }

    @Test
    void dependency_rule_is_respected_across_all_layers() {
        ArchRule rule = layeredArchitecture()
                .consideringOnlyDependenciesInAnyPackage("com.nutricare.nutricare_api..")
                .withOptionalLayers(true)

                .layer("Domain").definedBy("com.nutricare.nutricare_api.core.domain..")
                .layer("Application").definedBy("com.nutricare.nutricare_api.core.application..")
                .layer("Adapter").definedBy("com.nutricare.nutricare_api.infrastructure.adapter..")
                .layer("Config").definedBy("com.nutricare.nutricare_api.infrastructure.config..")

                .whereLayer("Domain").mayNotAccessAnyLayer()
                .whereLayer("Domain").mayOnlyBeAccessedByLayers("Application", "Adapter", "Config")

                .whereLayer("Application").mayOnlyAccessLayers("Domain")
                .whereLayer("Application").mayOnlyBeAccessedByLayers("Adapter", "Config")

                .whereLayer("Adapter").mayOnlyAccessLayers("Domain", "Application")
                .whereLayer("Adapter").mayOnlyBeAccessedByLayers("Config")

                .whereLayer("Config").mayOnlyAccessLayers("Domain", "Application", "Adapter")
                .whereLayer("Config").mayNotBeAccessedByAnyLayer();

        rule.check(classes);
    }

    @Test
    void core_should_only_depend_on_jdk_or_allowed_utility_libraries() {
        DescribedPredicate<JavaClass> isCore = resideInAnyPackage(
                "com.nutricare.nutricare_api.core.domain..",
                "com.nutricare.nutricare_api.core.application..");

        DescribedPredicate<JavaClass> isJdkOrPrimitive = resideInAnyPackage(
                "", // primitives/void are reported with an empty package name by ArchUnit
                "java..",
                "javax..");

        // Compile-time-only or facade-only libraries: no runtime coupling to a concrete framework.
        DescribedPredicate<JavaClass> isAllowedCoreUtility = resideInAnyPackage(
                "org.slf4j..",
                "lombok..");

        classes()
                .that(isCore)
                .should()
                .onlyDependOnClassesThat(isCore.or(isJdkOrPrimitive).or(isAllowedCoreUtility))
                .allowEmptyShould(true)
                .check(classes);
    }
}
