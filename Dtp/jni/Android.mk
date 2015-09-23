LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
include /home/oriel/OpenCV-android-sdk/sdk/native/jni/OpenCV.mk
LOCAL_SRC_FILES := com_vinci_dtp_ChamLib.cpp
LOCAL_MODULE := com_vinci_dtp_ChamLib

OPENCV_INSTALL_MODULES:=on
OPENCV_CAMERA_MODULES:=off
OPENCV_LIB_TYPE:=STATIC

include $(BUILD_SHARED_LIBRARY)