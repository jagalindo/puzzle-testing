#include <string>
#include "Picosat4J.h"
#include "picosat-916/picosat.h"
#include "picosat-916/config.h"


//void throwIOException(JNIEnv *env, const char *message) {
//  jclass cls = env->FindClass("TODO");
//  /* if cls is NULL, an exception has already been thrown */
//  if (cls != 0) {
//    env->ThrowNew(cls, message);
//  }
//  /* free the local ref */
//  env->DeleteLocalRef(cls);
//}


jstring newString(JNIEnv *env, const char *str) {
  jstring result;
  jbyteArray bytes = 0;
  int len;
  if (env->EnsureLocalCapacity(2) < 0) {
    return NULL; /* out of memory error */
  }
  len = strlen(str);
  bytes = env->NewByteArray(len);
  if (bytes != NULL) {
    env->SetByteArrayRegion(bytes, 0, len, (jbyte *) str);
    //result = (jstring)env->GetObjectArrayElement(bytes, len);
    jclass cls = env->FindClass("java/lang/String");
    result = (jstring) env->NewObject(cls, env->GetMethodID(cls, "<init>", "([B)V"), bytes);
    env->DeleteLocalRef(bytes);
    return result;
  } /* else fall through */
  return NULL;
}
 
/*
 * Class:     at_jku_sea_sat_implementation_picosat_Picosat
 * Method:    getVersion
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_at_jku_sea_sat_implementation_picosat_Picosat4J_version(JNIEnv *env, jclass) {
  return newString(env, picosat_version());
}

/*
 * Class:     at_jku_sea_sat_implementation_picosat_Picosat
 * Method:    getConfig
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_at_jku_sea_sat_implementation_picosat_Picosat4J_config(JNIEnv *env, jclass) {
  return newString(env, picosat_config());
}

/*
 * Class:     at_jku_sea_sat_implementation_picosat_Picosat
 * Method:    getCopyright
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_at_jku_sea_sat_implementation_picosat_Picosat4J_copyright(JNIEnv *env, jclass) {
  return newString(env, picosat_copyright());
}

/*
 * Class:     at_jku_sea_sat_implementation_picosat_Picosat4J
 * Method:    init
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_at_jku_sea_sat_implementation_picosat_Picosat4J_init(JNIEnv *env, jclass) {
  picosat_init();
}

/*
 * Class:     at_jku_sea_sat_implementation_picosat_Picosat4J
 * Method:    reset
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_at_jku_sea_sat_implementation_picosat_Picosat4J_reset(JNIEnv *env, jclass) {
  picosat_reset();
}

/*
 * Class:     at_jku_sea_sat_implementation_picosat_Picosat4J
 * Method:    set_global_default_phase
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_at_jku_sea_sat_implementation_picosat_Picosat4J_set_1global_1default_1phase(JNIEnv *, jclass, jint phase) {
  picosat_set_global_default_phase(phase);
}

/*
 * Class:     at_jku_sea_sat_implementation_picosat_Picosat4J
 * Method:    set_default_phase_lit
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_at_jku_sea_sat_implementation_picosat_Picosat4J_set_1default_1phase_1lit(JNIEnv *, jclass, jint lit, jint phase) {
  picosat_set_default_phase_lit(lit, phase);
}

/*
 * Class:     at_jku_sea_sat_implementation_picosat_Picosat4J
 * Method:    set_more_important_lit
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_at_jku_sea_sat_implementation_picosat_Picosat4J_set_1more_1important_1lit(JNIEnv *, jclass, jint lit) {
  picosat_set_more_important_lit(lit);
}

/*
 * Class:     at_jku_sea_sat_implementation_picosat_Picosat4J
 * Method:    enable_trace_generation
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_at_jku_sea_sat_implementation_picosat_Picosat4J_enable_1trace_1generation(JNIEnv *, jclass) {
  picosat_enable_trace_generation();
}


/*
 * Class:     at_jku_sea_sat_implementation_picosat_Picosat4J
 * Method:    inc_max_var
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_at_jku_sea_sat_implementation_picosat_Picosat4J_inc_1max_1var(JNIEnv *env, jclass) {
  return picosat_inc_max_var();
}

/*
 * Class:     at_jku_sea_sat_implementation_picosat_Picosat4J
 * Method:    adjust
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_at_jku_sea_sat_implementation_picosat_Picosat4J_adjust(JNIEnv *env, jclass, jint max_idx) {
  picosat_adjust(max_idx);
}

/*
 * Class:     at_jku_sea_sat_implementation_picosat_Picosat4J
 * Method:    variables
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_at_jku_sea_sat_implementation_picosat_Picosat4J_variables(JNIEnv *env, jclass) {
  return picosat_variables();
}

/*
 * Class:     at_jku_sea_sat_implementation_picosat_Picosat4J
 * Method:    added_original_clauses
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_at_jku_sea_sat_implementation_picosat_Picosat4J_added_1original_1clauses(JNIEnv *env, jclass) {
  return picosat_added_original_clauses();
}

/*
 * Class:     at_jku_sea_sat_implementation_picosat_Picosat4J
 * Method:    max_bytes_allocated
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_at_jku_sea_sat_implementation_picosat_Picosat4J_max_1bytes_1allocated(JNIEnv *env, jclass) {
  return picosat_max_bytes_allocated();
}

/*
 * Class:     at_jku_sea_sat_implementation_picosat_Picosat4J
 * Method:    add
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_at_jku_sea_sat_implementation_picosat_Picosat4J_add(JNIEnv *env, jclass, jint lit) {
  picosat_add(lit);
}

/*
 * Class:     at_jku_sea_sat_implementation_picosat_Picosat4J
 * Method:    assume
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_at_jku_sea_sat_implementation_picosat_Picosat4J_assume(JNIEnv *env, jclass, jint lit) {
  picosat_assume(lit);
}

/*
 * Class:     at_jku_sea_sat_implementation_picosat_Picosat4J
 * Method:    sat
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_at_jku_sea_sat_implementation_picosat_Picosat4J_sat(JNIEnv *env, jclass, jint decision_limit) {
  return picosat_sat(decision_limit);
}

/*
 * Class:     at_jku_sea_sat_implementation_picosat_Picosat4J
 * Method:    deref
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_at_jku_sea_sat_implementation_picosat_Picosat4J_deref(JNIEnv *env, jclass, jint lit) {
  return picosat_deref(lit);
}

/*
 * Class:     at_jku_sea_sat_implementation_picosat_Picosat4J
 * Method:    deref_toplevel
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_at_jku_sea_sat_implementation_picosat_Picosat4J_deref_1toplevel(JNIEnv *env, jclass, jint lit) {
  return picosat_deref_toplevel(lit);
}

/*
 * Class:     at_jku_sea_sat_implementation_picosat_Picosat4J
 * Method:    inconsistent
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_at_jku_sea_sat_implementation_picosat_Picosat4J_inconsistent(JNIEnv *, jclass) {
  return picosat_inconsistent();
}

/*
 * Class:     at_jku_sea_sat_implementation_picosat_Picosat4J
 * Method:    failed_assumption
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_at_jku_sea_sat_implementation_picosat_Picosat4J_failed_1assumption(JNIEnv *, jclass, jint lit) {
  return picosat_failed_assumption(lit);
}

/*
 * Class:     at_jku_sea_sat_implementation_picosat_Picosat4J
 * Method:    changed
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_at_jku_sea_sat_implementation_picosat_Picosat4J_changed(JNIEnv *, jclass) {
  return picosat_changed();
}

/*
 * Class:     at_jku_sea_sat_implementation_picosat_Picosat4J
 * Method:    coreclause
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_at_jku_sea_sat_implementation_picosat_Picosat4J_coreclause(JNIEnv *, jclass, jint i) {
  return picosat_coreclause(i);
}

/*
 * Class:     at_jku_sea_sat_implementation_picosat_Picosat4J
 * Method:    corelit
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_at_jku_sea_sat_implementation_picosat_Picosat4J_corelit(JNIEnv *, jclass, jint lit) {
  return picosat_corelit(lit);
}

/*
 * Class:     at_jku_sea_sat_implementation_picosat_Picosat4J
 * Method:    usedlit
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_at_jku_sea_sat_implementation_picosat_Picosat4J_usedlit(JNIEnv *, jclass, jint lit) {
  return picosat_usedlit(lit);
}
