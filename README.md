# NeoCommon
[![ownner](https://img.shields.io/badge/owner-neocross-green.svg)](http://www.neocorss.cn)
[![maven](https://img.shields.io/badge/maven-v1.0.0-ff69b4.svg)](https://bintray.com/neocross2017/maven/common)
[![license](https://img.shields.io/hexpm/l/plug.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)
[![download aar](https://img.shields.io/badge/Download-aar-yellowgreen.svg)](https://dl.bintray.com/neocross2017/maven/cn/neocross/libs/common/1.0.0/common-1.0.0.aar)

![logo](https://github.com/neocross/NeoCommon/blob/master/common/pom_icon.png)

跨界Android项目公共基础库，方便快速集成和调用。 

## Maven build settings
build.gradle
```gradle
dependencies {
  compile 'cn.neocross.libs:common:1.0.0'
}
```
or maven
```maven
<dependency>
  <groupId>cn.neocross.libs</groupId>
  <artifactId>common</artifactId>
  <version>1.0.0</version>
  <type>pom</type>
</dependency>
```
or lvy
```lvy
<dependency org='cn.neocross.libs' name='common' rev='1.0.0'>
  <artifact name='common' ext='pom' ></artifact>
</dependency>
```
## Include
- helper
- http
- router
- utils
- widget

## License

    Copyright 2017 neocross

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
