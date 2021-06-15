# 刽子手 小游戏 
[![Build Status](https://travis-ci.com/Mr-Po/hang-man.svg?branch=master)](https://travis-ci.com/Mr-Po/hang-man)
[![Language grade: Java](https://img.shields.io/lgtm/grade/java/github/Mr-Po/hang-man?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/Mr-Po/hang-man/context:java)

[![Release](https://img.shields.io/github/v/release/Mr-Po/hang-man)](../../releases)
[![License](https://img.shields.io/github/license/Mr-Po/hang-man?color=blue)](LICENSE)


## 描述

随机选择一个单词，提示用户每次猜一个字母。单词中的每个字母以星号显示。用户猜对一个字母时，显示实际字母。当用户完成一个单词或猜错超过7次，则显示单词和猜错的次数，同时询问用户是否继续下一单词。

* 支持控制台和图形界面操作游戏，支持混合输入指令。
* 支持从外部读取wordlist.txt文件，作为待猜词库。

## 截图

[![2HJ0JI.jpg](https://z3.ax1x.com/2021/06/15/2HJ0JI.jpg)](https://imgtu.com/i/2HJ0JI)

[![2HJBWt.jpg](https://z3.ax1x.com/2021/06/15/2HJBWt.jpg)](https://imgtu.com/i/2HJBWt)

[![2HJrSP.jpg](https://z3.ax1x.com/2021/06/15/2HJrSP.jpg)](https://imgtu.com/i/2HJrSP)

[![2HJsQf.jpg](https://z3.ax1x.com/2021/06/15/2HJsQf.jpg)](https://imgtu.com/i/2HJsQf)

[![2HJyy8.gif](https://z3.ax1x.com/2021/06/15/2HJyy8.gif)](https://imgtu.com/i/2HJyy8)

## 下载

gitee：
```
git clone git@gitee.com:pomo/hang-man.git
```

github：
```
git clone git@github.com:Mr-Po/hang-man.git
```

## 运行

```
mvn compile exec:java
```