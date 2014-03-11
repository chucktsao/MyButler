/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
#define LOG_TAG "org_ct_jni_BsdiffJNI"


#include "jni_opencv_java.h"
#include <stdlib.h>

#include "bspatch.h"


/*
jint JNI_OnLoad(JavaVM *vm, void *reserved)
{
	LOGD("JNI_OnLoadd!");
    JNIEnv* env = NULL;
	jint result = -1;

	if ((*vm)->GetEnv(vm, (void**) &env, JNI_VERSION_1_6) != JNI_OK) {
		return -1;
	}
	result = JNI_VERSION_1_6;
    LOGD("JNI_OnLoadd %d",result);
	return result;
}
*/
jstring Java_org_ct_jni_BsdiffJNI_bsdiff(JNIEnv* env, jobject thiz,
		jstring oldFilePath, jstring newFilePath, jstring patchFilePath) {
			/*
	  char* oldFilePathChar = (*env)->GetStringUTFChars(env, oldFilePath,
			0);
	  char* newFilePathChar = (*env)->GetStringUTFChars(env, newFilePath,
			0);
	  char* patchFilePathChar = (*env)->GetStringUTFChars(env,
			patchFilePath, 0);
	 char* arg[4];
	arg[1]=oldFilePathChar;
	arg[2]=newFilePathChar;
	arg[3]=patchFilePathChar;
	bsdiff(4,arg);	*/
	return patchFilePath;
	
}


jstring Java_org_ct_jni_BsdiffJNI_bspatch(JNIEnv* env,
		jobject thiz, jstring oldFilePath, jstring newFilePath, jstring patchFilePath) {
		
	const  char* oldFilePathChar = (*env)->GetStringUTFChars(env, oldFilePath,
			0);
	const char* newFilePathChar = (*env)->GetStringUTFChars(env, newFilePath,
			0);
	const  char* patchFilePathChar = (*env)->GetStringUTFChars(env,
			patchFilePath, 0);
	LOGD("bspatch native start!");
	int result =bspatch(oldFilePathChar,newFilePathChar,patchFilePathChar);	
	if(result==1){
		LOGD("bspatch failed!");
		return NULL;
	}else {
		LOGD("bspatch succeed!");
		return newFilePath;
	}
		
	return newFilePath;
}
