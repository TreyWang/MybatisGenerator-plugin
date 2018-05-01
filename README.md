# MybatisGenerator-plugin

基于mybatis-generator-1.3.5版本

## 使用:
1.mybatis-generator-core修改的内容已经打包成jar下mybatis-generator-core-1.3.5.jar，直接替换使用，或者命令使用；
2.配置文件generatorConfig.xml放在config文件夹下；
3.为了方便使用，mysql的链接包在jar文件夹下。

## 增加如下功能：
1.类以及字段的中文注释；
2.作者；
3.实体增加实现序列化以及序列化ID；
4.用反射重写类的toString方法；
5.实体内枚举的生成；
6.修改Mapper类为DAO；
7.删除DAO中无用方法；
8.增加动态List查询，动态count查询方法；
9.增加批量增加、修改的方法；
10.mapper中增加动态SQl-WhereList；
11.实现Swagger的注解绑定。