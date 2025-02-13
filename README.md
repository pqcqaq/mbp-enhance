# mbp-enhance

> 一个简洁但不简单的一个工具包框架~
>
> 中央仓库：[Maven Central: online.zust.qcqcqc.utils:mbp-enhance](https://central.sonatype.com/artifact/online.zust.qcqcqc.utils/mbp-enhance)

## 什么是Mbp-enhance

>  Mbp-enhance = MybatisPlus增强框架+自动类型转换工具

- 在mybatisplus的基础上增加多表关系自动查询，对象依赖关系检查等功能。
- 提供更加自由灵活的实体类类型转换工具。
- 贯彻基于注解的开发，极大程度上简化开发难度和降低侵入性

## 特性

- 与Mybatisplus完全兼容。
- 项目启动时自动配置。
- 开箱即用的自动化数据库查询操作。
- 完全注解式开发，低侵入性。
- 严格的实体类定义规范审查。



## 开始使用

- 在Maven中添加依赖

    - 上一版本是：1.4.2

    - Maven：

        - springboot2:

        - ```xml
            <dependency>
                <groupId>online.zust.qcqcqc.utils</groupId>
                <artifactId>mbp-enhance</artifactId>
                <version>1.4.3</version>
            </dependency>
            ```

        - springboot3:

        - 如果需要在springboot3中使用，请手动添加mp的springboot3依赖

        - ```xml
            <dependency>
                <groupId>online.zust.qcqcqc.utils</groupId>
                <artifactId>mbp-enhance</artifactId>
                <version>1.4.3</version>
                <exclusions>
                    <exclusion>
                        <groupId>com.baomidou</groupId>
                        <artifactId>mybatis-plus-boot-starter</artifactId>
                    </exclusion>
                </exclusions>
            </dependency>
            <dependency>
                <groupId>com.baomidou</groupId>
                <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
                <version>${mybatis-plus.version}</version>
            </dependency>
            ```

        - 

    - Gradle

        - ```
            implementation group: 'online.zust.qcqcqc.utils', name: 'mbp-enhance', version: '1.4.3'
            ```

    - 
