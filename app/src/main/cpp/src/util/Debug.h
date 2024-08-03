#include "CommonLog.h"

#define ASSERT(x) if(!(x)){LOG_F("fatal error !"); while(true){unsigned long *e = new unsigned long[0x0fffffff];}}