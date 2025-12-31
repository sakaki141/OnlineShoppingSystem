# OnlineShoppingSystem  
该项目为华南理工大学网络应用架构设计与开发课程设计  
## 学生信息
姓名：黄炫宁  
学号：202330450711  
## 技术栈
前端：JavaScript+HTML+CSS  
后端：Java Springboot  
数据库：MySQL  
## 项目架构与代码功能说明  
*/java/com/example/onlineshoppingsystem：后端核心代码*  
/config:配置文件，用于存储JWT过滤器、密码加密程序与SpringSecurity的权限配置  
/controller：控制器类，用于提供API以实现前后端数据交互功能  
/dto：数据传输类，用于传输前端操作需要的类成员  
/entity：实体类，用于映射类与数据库表的数据  
/exceptions：用于异常处理与规范化结果返回  
/repository：仓库类，用于提供基本的查询方法  
/service：服务类，用于从对象中提取数据用于功能实现  
/util：工具类，内部的JWT工具用于token相关操作  
*/resources:数据库与前端页面代码*  
/sql：内含数据库的初始化建表代码  
/static：前端页面HTML与Javascript脚本
