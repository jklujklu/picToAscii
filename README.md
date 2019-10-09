# picToAscii
图片转换为字符画

## 说明
支持大部分图片类型，为求简单，本代码只允许部分常用格式: jpg, jpeg, png, bmp, gif
有兴趣的朋友可以自行添加其他格式，方法如下：
- 在FileUtil.read()函数中的过滤器中添加指定格式

```java
 FileNameExtensionFilter ff = new FileNameExtensionFilter("图片(jpg/gif/png)", "bmp","jpg","png", "jpeg", "gif");
```

- 在PicToAscii.regexFormat()函数中的正则表达式中添加指定格式
```java
String regex = "\\.(jpg|jpeg|png|gif|bmp)";
```

## 使用方法

```
cd java
javac PicToAscii.java
java PicToAscii
```
