//
// Created by leixing on 2023/12/5.
//

#include "StringUtil.h"
#include "CommonLog.h"

namespace util {

    template<typename T>
    string vectorToString(vector<T> data) {
        stringstream ss;
        ss << "[";
        int size = data.size();
        if (size > 0) {
            ss << data[0];
        }
        for (int i = 0; i < size; i++) {
            ss << ", " << data[i];
        }
        ss << "]";
        return ss.str();
    }

    void replaceAll(std::string &s, const std::string &search, const std::string &replace) {
        size_t pos = 0;
        while ((pos = s.find(search, pos)) != std::string::npos) {
            s.replace(pos, search.length(), replace);
            pos += replace.length();
        }
    }

    string charVectorToString(vector<unsigned char> data) {
        stringstream ss;
        ss << "[";
        int size = data.size();
        if (size > 0) {
            ss << ((int) data[0]);
        }
        for (int i = 0; i < size; i++) {
            ss << ", " << ((int) data[i]);
        }
        ss << "]";
        return ss.str();
    }
}