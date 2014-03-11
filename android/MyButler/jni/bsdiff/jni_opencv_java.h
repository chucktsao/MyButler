#ifndef _JNI_OPENCV_JAVA_H_
#define _JNI_OPENCV_JAVA_H_

#ifndef __ANDROID__
#define __ANDROID__
#endif
#define DEBUG

#ifdef __ANDROID__
#include <jni.h>
#include <android/log.h>
#define LOGE(...) ((void)__android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__))

#ifdef DEBUG
#define LOGD(...) ((void)__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__))
#else
#define LOGD(...)
#endif

#else  // __ANDROID__
#define LOGE(...)
#define LOGD(...)
#endif


#endif
