# Trusted Web Activity: the launcher and trampoline classes are referenced from the
# manifest, so R8 keeps them automatically. Nothing app-specific needs keeping —
# this app has no reflection and no serialisation.
-keepattributes SourceFile,LineNumberTable
