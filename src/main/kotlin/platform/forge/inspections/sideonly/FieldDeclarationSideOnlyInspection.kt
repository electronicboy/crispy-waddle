/*
 * Minecraft Dev for IntelliJ
 *
 * https://minecraftdev.org
 *
 * Copyright (c) 2022 minecraft-dev
 *
 * MIT License
 */

package com.demonwav.mcdev.platform.forge.inspections.sideonly

import com.intellij.psi.PsiAnnotation
import com.intellij.psi.PsiClassType
import com.intellij.psi.PsiField
import com.siyeh.ig.BaseInspection
import com.siyeh.ig.BaseInspectionVisitor
import com.siyeh.ig.InspectionGadgetsFix
import org.jetbrains.annotations.Nls

class FieldDeclarationSideOnlyInspection : BaseInspection() {

    @Nls
    override fun getDisplayName() = "Invalid usage of @SideOnly in field declaration"

    override fun buildErrorString(vararg infos: Any): String {
        val error = infos[0] as Error
        return error.getErrorString(*SideOnlyUtil.getSubArray(infos))
    }

    override fun getStaticDescription(): String? {
        return "A field in a class annotated for one side cannot be declared as being in the other side. " +
            "For example, a class which is annotated as @SideOnly(Side.SERVER) cannot contain a field which is " +
            "annotated as @SideOnly(Side.CLIENT). Since a class that is annotated with @SideOnly brings " +
            "everything with it, @SideOnly annotated fields are usually useless"
    }

    override fun buildFix(vararg infos: Any): InspectionGadgetsFix? {
        val annotation = infos[3] as PsiAnnotation

        return if (annotation.isWritable) {
            RemoveAnnotationInspectionGadgetsFix(annotation, "Remove @SideOnly annotation from field")
        } else {
            null
        }
    }

    override fun buildVisitor(): BaseInspectionVisitor {
        return object : BaseInspectionVisitor() {
            override fun visitField(field: PsiField) {
                val psiClass = field.containingClass ?: return

                if (!SideOnlyUtil.beginningCheck(field)) {
                    return
                }

                val (fieldAnnotation, fieldSide) = SideOnlyUtil.checkField(field)
                if (fieldAnnotation == null || fieldSide === Side.INVALID) {
                    return
                }

                val (classAnnotation, classSide) = SideOnlyUtil.getSideForClass(psiClass)

                if (fieldSide !== Side.NONE && fieldSide !== classSide) {
                    if (classAnnotation != null && classSide !== Side.NONE && classSide !== Side.INVALID) {
                        registerFieldError(
                            field,
                            Error.CLASS_CROSS_ANNOTATED,
                            fieldAnnotation.renderSide(fieldSide),
                            classAnnotation.renderSide(classSide),
                            field.getAnnotation(fieldAnnotation.annotationName)
                        )
                    } else if (classSide !== Side.NONE) {
                        registerFieldError(field, Error.CLASS_UNANNOTATED, fieldAnnotation, null, field)
                    }
                }

                if (fieldSide === Side.NONE) {
                    return
                }

                if (field.type !is PsiClassType) {
                    return
                }

                val type = field.type as PsiClassType
                val fieldClass = type.resolve() ?: return

                val (fieldClassAnnotation, fieldClassSide) = SideOnlyUtil.getSideForClass(fieldClass)

                if (fieldClassAnnotation == null || fieldClassSide === Side.NONE || fieldClassSide === Side.INVALID) {
                    return
                }

                if (fieldClassSide !== fieldSide) {
                    registerFieldError(
                        field,
                        Error.FIELD_CROSS_ANNOTATED,
                        fieldClassAnnotation.renderSide(fieldClassSide),
                        fieldAnnotation.renderSide(fieldSide),
                        field.getAnnotation(fieldAnnotation.annotationName)
                    )
                }
            }
        }
    }

    enum class Error {
        CLASS_UNANNOTATED {
            override fun getErrorString(vararg infos: Any): String {
                return "Field with type annotation ${infos[1]} cannot be declared in an un-annotated class"
            }
        },
        CLASS_CROSS_ANNOTATED {
            override fun getErrorString(vararg infos: Any): String {
                return "Field annotated with ${infos[0]} cannot be declared inside a class annotated with ${infos[1]}."
            }
        },
        FIELD_CROSS_ANNOTATED {
            override fun getErrorString(vararg infos: Any): String {
                return "Field with type annotation ${infos[0]} cannot be declared as ${infos[1]}."
            }
        };

        abstract fun getErrorString(vararg infos: Any): String
    }
}
