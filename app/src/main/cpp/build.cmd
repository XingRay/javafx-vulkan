mkdir build
cd build

rmdir /s /q .\Release

cmake .. -G "Visual Studio 17 2022" -A x64 -DCMAKE_BUILD_TYPE=Release -DPLATFORM=win64
cmake --build . --config Release

xcopy /y ".\Release\vulkan_renderer.dll" "../../../../../natives/vulkan-renderer/windows-x64"