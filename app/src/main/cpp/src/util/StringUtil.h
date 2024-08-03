//
// Created by leixing on 2023/12/5.
//

#pragma once

#include <string>
#include <vector>
#include <sstream>

using namespace std;

namespace util {

    template<typename T>
    string vectorToString(vector<T> data);

    string charVectorToString(vector<unsigned char> data);

    void replaceAll(std::string &s, const std::string &search, const std::string &replace);

}