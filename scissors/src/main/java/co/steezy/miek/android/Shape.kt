package co.steezy.miek.android

/** Corresponds to the values in @see [co.steezy.miek.android.R.attr.cropviewShape] */

@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.LOCAL_VARIABLE, AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
annotation class Shape {
    companion object {
        const val RECTANGLE = 0
        const val OVAL = 1
    }
}