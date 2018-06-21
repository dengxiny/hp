## 2018.6.21 增加webmagic代理模式
---------------------------------------
# hp 链家爬虫 
## 1 配置
### ·配置mysql
### ·配置redis
### ·执行house.sql 去生成对应表
## 2 运行方式 
### ·通过配置application.yml 配置启动参数(由于是爬取免费代理不建议使用代理模式)
### ·以jar形式 运行
### ·test 输入localhost:8082/hp/hpwork.do  
## 3 采用技术
### ·springboot mybatis quartz redis
### ·webmagic httpclient 
## 4 代理池
### ·目前没有配置常量代理池，通过西刺代理实现即时代理,并进行IP测试
### ·当异常可以实现代理切换和重新爬取，有效次数内仍旧不能获取到有效IP使用本地IP
## 5 定时爬取
### ·可通过配置参数配置执行时间
## 6 web界面
### 由于目前懒得做查询和数据分析界面 仅提供爬取数据功能 以后可以实现接口提供长期数据
# 效果图
![Image text](https://github.com/dengxiny/hp/blob/master/src/test/java/com/hp/data.png)
