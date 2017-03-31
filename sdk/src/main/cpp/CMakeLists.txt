cmake_minimum_required(VERSION 3.4.1)

project(eegeo-jni)

set(library_name eegeo-jni)
set(EEGEO_SDK_PACKAGE_DIR libs/eegeo)

MESSAGE("EEGEO_SDK_PACKAGE_DIR is ${EEGEO_SDK_PACKAGE_DIR}")

include(${EEGEO_SDK_PACKAGE_DIR}/CMakeLists.txt)

set (ANDROID_PREBUILT_LIBS_DIR ${EEGEO_SDK_PACKAGE_DIR}/prebuilt/android-${ANDROID_NDK_ABI_NAME})

MESSAGE("ANDROID_PREBUILT_LIBS_DIR is ${ANDROID_PREBUILT_LIBS_DIR}")

find_library(
	eegeo-sdk-lib 
	eegeo-sdk 
	PATHS ${ANDROID_PREBUILT_LIBS_DIR}
    CMAKE_FIND_ROOT_PATH_BOTH
)

link_directories(
	${ANDROID_PREBUILT_LIBS_DIR}
)

add_library(${library_name} SHARED jnimain.cpp)

target_include_directories(${library_name} PUBLIC ${headers})

target_link_libraries(
	${library_name}	
	eegeo-api-host
	eegeo-api
	${eegeo-sdk-lib}
	android
	curl
	crypto
	EGL
	GLESv2
	http-parser
	jpeg
	log
	m
	png
	ssl
	crypto
	turbojpeg
	uv
	z)


# copy lib .so and header to distribution
#set(distribution_DIR ${CMAKE_CURRENT_SOURCE_DIR}/../../../../distribution)
#set_target_properties(${library_name}
#                      PROPERTIES
#                      LIBRARY_OUTPUT_DIRECTORY
#                      "${distribution_DIR}/${library_name}/lib/${ANDROID_ABI}")
#add_custom_command(TARGET ${library_name} POST_BUILD
#                   COMMAND "${CMAKE_COMMAND}" -E
#                   copy "${CMAKE_CURRENT_SOURCE_DIR}/jnimain.h"
#                   "${distribution_DIR}/${library_name}/include/jnimain.h"
#                   COMMENT "Copying ${library_name} to output directory")
