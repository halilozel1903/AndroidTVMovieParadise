# Project-specific R8 rules. Retrofit, OkHttp, Glide, Dagger and RxJava ship their own
# consumer rules, so only what is unique to this app lives here.

# Readable stack traces in release builds.
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Gson reads these models reflectively: several fields rely on their Java name
# (no @SerializedName), so the fields must survive shrinking and keep their names.
-keepattributes Signature,*Annotation*
-keep class com.halil.ozel.movieparadise.data.models.** { *; }
