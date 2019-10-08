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
在init()方法中，调用createAsciiPic()方法
```java
/*
 * multiple 为图片放大的倍率，即生成的Ascii字符画大小为原画的若干倍
 * degree   为Ascii字符的密集程度，数字越小越密集
 */
private void createAsciiPic(int multiple, int degree)
```
