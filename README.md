1、项目整体框架说明：

    整个项目的核心思想的是面向服务理念，主要使用 Spring4.3 + Dubbox2.8.4 + Mybatis3框架，以注解配置为主，xml配置为辅的方式进行搭建，
    而web项目则引入了springMVC作为服务器端框架，使用 BJUI + Vue 作为前端UI框架，同时使用freemarker作为模板引擎，另外，还引入Quartz作为
    定时任务框架，可实现动态管理定时任务，引入ActiveMQ作为消息中间件，使用Redis作为缓存框架，使用elasticsearch作为搜索引擎，所以，本项目是
    Spring + Dubbox + Mybatis + elasticserch + activemq + quartz + springMVC + freemarker + Vue + BJUI 的整合项目

2、demo各模块说明：
    demo包含的模块及各模块作用如下：
    
    base-common：公共模块，主要包含公用的实体类、工具类等，这个模块只会被其他模块引用，而不会引用其他模块，其中的MyBatisDao封装了Mybatis
                 对数据库常用的CRUD，而EsDao则是封装了对elasticsearch的常用的CRUD操作，两者在可以很大程度上降低对开发人员的技能要求
    base-facade：基础服务模块的接口，提供整个平台共用的服务如：账号(用户)、资金、店铺等等服务，虽然目前只提供了用户服务，这个模块只会被其他
                 模块引用而不会引用除base-common以外的模块，这是为了避免服务与服务之间依赖混乱，而导致再打包、部署时的混乱
    base-service：基础服务模块的实现类
    center-common-facade：公共中间件中心模块的接口，目前提供MQ消息、缓存、定时任务等服务的操作，理论上来说，这个模块应该尽量独立，只与各种
                          中间件耦合，而不会与其他业务模块耦合相关
    center-common-service：公共中心模块的实现类
    common-message-receive：公用的消息接收中心，就是用以接收公共中间件中心模块发送的消息，接收到消息之后再去调用各个业务模块的相关服务进行
                            业务处理，其实，本来这个功能可以放到各个业务模块自己去监听，但考虑到以后的消息中间可能被替换成RabbitMQ或其他消息
                            中间件，为了减少各业务模块的技术依赖，特意独立出一个模块，用以监听消息，并调用相关业务模块处理相应业务，起到一个
                            相当于消息路由的功能
    common-test：测试模块，开发时用来测试各业务模块提供的服务，可以用junit替代，可忽略本模块
    shop-facade：shop的业务模块接口，虽然是叫shop，但其实跟shop没有半毛钱关系，这是搭建项目最初是想做一个关于shop的demo的，但后来没有弄，名字也
                 没改，就沿用吧，不用理会名字，只需要理解为这是其中一个业务模块就好了
    shop-service：shop业务模块的实现模块
    shop-web：shop业务模块的web项目
    sso-web：单点系统，原本是想设计成单点登陆的一整套功能的，但现在只有登陆、注册功能，也没有一整套的单点概念实现
    
    从上面的模块功能可以看出，web项目就是页面展示的项目，也是服务的消费者，而facade就是接口和实体类，还有业务的Vo类，业务模块之间的相互调用
    就是引用此模块的，而service就是各个facade模块的实现类，处理业务逻辑、数据库操作、事务等，作为微服务的提供者(也可成为消费者)。
    另外，本demo项目的模块结构，在正式公司使用的时候是不建议这样的，因为这不利于项目管理，分支合并等，在正式使用时是建议每个模块独立一个项目，
    独立开发，独立打包，独立部署，或者可以把同一个业务模块的facade、service放在同一个项目里面，比如，建立一个shop项目，里面包含shop-facade、
    shop-service两个模块，这样方便团队分工，同时也利于项目的上线等
    
3、对于公共中间件中心模块的说明：

    3.1 消息中间件ActiveMQ
        (1) 使用ActiveMQ作为消息中间件，虽然性能并不怎么好，但是消息保障比较好，文档和案例也很多，适合作为demo来说明或者入门学习，在模块中，
            提供了MessageService来统一提供消息的发送，可以单条发送queue、topic，也可以批量发送queue、topic，其他业务模块只需要调用这些接口即
            可发送消息，而不用关系这些消息中间件的细节，以后也可以方便中间件的替换或升级。
        (2) ActiveMQ是结合了spring的JmsTemplate来实现发送消息的，使得发送消息更加简便和统一 
        (3) 在common-message-receive中是结合了spring的@JmsListener来实现消息监听的，只需要在需要监听消息的方法上加上@JmsListener注解，就可
            实现对指定消息的监听，还可以配置监听器工厂类的相关属性进行消息签收模式的设置，这对于消息保证要求比较高的需求来说是很好的机制。
            
    3.2 Quartz 定时器
        (1) 使用Quartz作为定时器中间件，并配套数据库来进行定时任务的增、删、改、查，TimerService提供了常用的定时任务的增改查接口，简化对
            Quartz的使用，开发人员只需要调用接口就可以，而不需要关注Quartz的具体配置和实现等
        (2) 因为配套了数据库，所以，可以在管理页面对定时任务进行动态的查询、添加、修改、删除、执行等操作，如果定时任务服务器宕机了，下次重启
            的时候还可以自动从数据库加载任务并添加到Quartz框架中
        (3) 定时任务被触发后是发送消息到消息中间件的，需要在common-message-receive增加相应的监听方法才能真正实现任务的执行
        
    3.3 缓存
        (1) 在CacheService提供了最基本的缓存接口，开发人员不需要知道使用的是哪种缓存中间件，只需要调用接口
        (2) 只提供了最基础的缓存操作，对于redis来说，其强大的功能绝非这几个接口所能涵盖，所以在base-common项目中，提供了
            com.cyf.base.common.component.Redisson 这个组件来操作redis，该组件是使用了org.redisson下的redisson项目，完全封装了对redis的操作，
            使用户可以完全以面向对象的形式来操作redis，不但如此，还在此基础上拓展了Rqueue、RList、RLock等分布式对象，使得很多分布式功能，
            可以像普通java对象一样使用
            
4、dubbo服务提供者和消费者：

    (1) 整个项目来说，base-service、center-common-service、shop-service都是服务提供者
    (2) sso-web、shop-web是单纯的服务消费者、而shop-service因为调用了base-service和center-common-service，所以它既是提供者，又是消费者，同时
        这个模块接入了elasticsearch搜索引擎，是为了提高数据的检索速度
            
5、web项目：

    (1) 对于shop-web项目，采用了SpringMVC的拦截器功能来实现用户登陆，以及基于RBAC的权限控制
    (2) shop-web项目的功能其实相当于平时企业中使用的运营管理后台
    (3) sso-web纯粹就是一个用户的注册、登陆系统，原本是想做一个独立的单点登陆系统，但现在并不完善，离真正的sso还很远，鉴于个人的时间和能力
        有限，不打算在这上面花费心思了
        
6、maven私服：

    因为dubbox项目是当当网维护的，并没有提交maven中央仓库，并且，dubbox2.8.4的内置依赖spring并不是4.3，所以需要作出一定的修改，并且，
    由于当前项目的模块比较多，也非常需要有一个maven私服来给团队开发解决项目间的依赖问题，我这里采用的是 Nexus OSS 3.2.1-01，可自行百度搭建
 
7、代码生成工具

    使用rapid-generator3来生成mybatis的Mapping文件，Dao、Model类、elasticsearch的mapping等，简化开发，地址：
    http://pan.baidu.com/s/1gfOjfFH 提取码：t1bm

8、demo所使用到的sql

    CREATE DATABASE `mytest` DEFAULT CHARACTER SET utf8;
    
    USE `mytest`;
    
    DROP TABLE IF EXISTS `auth_authority`;
    CREATE TABLE `auth_authority` (
      `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '节点分组id(主键)',
      `name` varchar(80) NOT NULL COMMENT '分组名称',
      `parent_id` bigint(20) unsigned NOT NULL COMMENT '父分组id(0=顶级分组)',
      `level` tinyint(1) NOT NULL COMMENT '分组层级',
      `check_level` tinyint(1) NOT NULL DEFAULT '3' COMMENT '权限检查等级(1=游客可访问 2=登陆可访问 3=授权可访问)',
      `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态(1=正常,2=禁用)',
      `create_time` datetime NOT NULL COMMENT '创建时间',
      `update_time` datetime NOT NULL COMMENT '更新时间',
      PRIMARY KEY (`id`)
    ) ENGINE=InnoDB AUTO_INCREMENT=201 DEFAULT CHARSET=utf8 COMMENT='权限表';
    
    DROP TABLE IF EXISTS `auth_authority_has_node`;
    CREATE TABLE `auth_authority_has_node` (
      `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '表id(主键)',
      `fk_authority_id` bigint(20) unsigned NOT NULL COMMENT '权限id(外键)',
      `fk_node_id` bigint(20) unsigned NOT NULL COMMENT '节点id(外键)',
      `create_time` datetime NOT NULL COMMENT '创建时间',
      `update_time` datetime NOT NULL COMMENT '更新时间',
      PRIMARY KEY (`id`)
    ) ENGINE=InnoDB AUTO_INCREMENT=1561 DEFAULT CHARSET=utf8 COMMENT='权限-节点关联表';
    
    DROP TABLE IF EXISTS `auth_node`;
    CREATE TABLE `auth_node` (
      `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '节点id(主键)',
      `type` tinyint(2) NOT NULL COMMENT '节点类型(1=菜单 2=元素,ajax等)',
      `name` varchar(80) NOT NULL COMMENT '节点名称',
      `url` varchar(150) DEFAULT '' COMMENT '节点访问的url',
      `icon` varchar(60) DEFAULT '' COMMENT '节点图标',
      `exclusive` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否独占(0=否 1=是,不允许分配给多个权限)',
      `class_attr` varchar(100) DEFAULT '' COMMENT '节点class属性',
      `parent_id` bigint(20) NOT NULL COMMENT '父节点id(0=顶级节点)',
      `ancestors_id` varchar(80) NOT NULL DEFAULT '' COMMENT '所有祖先节点的id(逗号分割)',
      `level` tinyint(1) NOT NULL COMMENT '节点层级',
      `sort` smallint(2) NOT NULL DEFAULT '0' COMMENT '排序(越大越优先)',
      `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态(1=正常,2=禁用)',
      `create_time` datetime NOT NULL COMMENT '创建时间',
      `update_time` datetime NOT NULL COMMENT '更新时间',
      PRIMARY KEY (`id`)
    ) ENGINE=InnoDB AUTO_INCREMENT=457 DEFAULT CHARSET=utf8 COMMENT='节点表';
    
    DROP TABLE IF EXISTS `auth_role`;
    CREATE TABLE `auth_role` (
      `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '角色id(主键)',
      `name` varchar(80) DEFAULT NULL COMMENT '角色名称',
      `type` tinyint(1) NOT NULL DEFAULT '1' COMMENT '角色类型(1=普通角色 2=超级管理员(只有一个))',
      `remark` varchar(300) DEFAULT '' COMMENT '备注',
      `status` tinyint(2) NOT NULL COMMENT '状态(1=正常,2=禁用)',
      `create_time` datetime NOT NULL COMMENT '创建时间',
      `update_time` datetime NOT NULL COMMENT '更新时间',
      PRIMARY KEY (`id`)
    ) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8 COMMENT='角色表';
    
    DROP TABLE IF EXISTS `auth_role_has_authority`;
    CREATE TABLE `auth_role_has_authority` (
      `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '权限id(主键)',
      `fk_role_id` bigint(20) unsigned NOT NULL COMMENT '角色id(外键)',
      `fk_authority_id` bigint(20) unsigned NOT NULL COMMENT '权限id(外键)',
      `create_time` datetime NOT NULL COMMENT '创建时间',
      `update_time` datetime NOT NULL COMMENT '更新时间',
      PRIMARY KEY (`id`),
      KEY `idx_role_id` (`fk_role_id`),
      KEY `idx_authority_id` (`fk_authority_id`)
    ) ENGINE=InnoDB AUTO_INCREMENT=1954 DEFAULT CHARSET=utf8 COMMENT='权限表(角色-权限关联表)';
    
    DROP TABLE IF EXISTS `auth_role_user`;
    CREATE TABLE `auth_role_user` (
      `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '角色-用户关联id(主键)',
      `fk_role_id` bigint(20) unsigned NOT NULL COMMENT '角色id(外键)',
      `fk_user_id` bigint(20) unsigned NOT NULL COMMENT '用户id(外键)',
      `create_time` datetime NOT NULL COMMENT '创建时间',
      `update_time` datetime NOT NULL COMMENT '更新时间',
      PRIMARY KEY (`id`),
      KEY `idx_role_id` (`fk_role_id`),
      KEY `idx_user_id` (`fk_user_id`)
    ) ENGINE=InnoDB AUTO_INCREMENT=94 DEFAULT CHARSET=utf8 COMMENT='角色-用户关联表';
    
    DROP TABLE IF EXISTS `goods`;
    CREATE TABLE `goods` (
      `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键id',
      `name` varchar(300) DEFAULT NULL COMMENT '商品名称',
      `price` decimal(9,3) DEFAULT '0.000' COMMENT '商品价格',
      `status` tinyint(1) DEFAULT NULL COMMENT '状态 1=有效 2=无效',
      `create_time` datetime DEFAULT NULL COMMENT '创建时间',
      `update_time` datetime DEFAULT NULL COMMENT '更新时间',
      PRIMARY KEY (`id`)
    ) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;
    
    DROP TABLE IF EXISTS `message_consume`;
    CREATE TABLE `message_consume` (
      `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
      `name` varchar(100) DEFAULT NULL COMMENT '消息名称',
      `type` tinyint(2) NOT NULL COMMENT '消息类型(1=queue 2=topic)',
      `payload` text COMMENT '消息内容',
      `consumer` varchar(80) DEFAULT NULL COMMENT '消费者名称',
      `messageDesc` varchar(300) DEFAULT NULL COMMENT '消息描述',
      `create_time` datetime DEFAULT NULL COMMENT '创建时间',
      `is_timer` tinyint(2) DEFAULT NULL COMMENT '是否定时任务消息(1=是 2=否)',
      PRIMARY KEY (`id`)
    ) ENGINE=InnoDB AUTO_INCREMENT=91970 DEFAULT CHARSET=utf8 COMMENT='消息消费';
    
    DROP TABLE IF EXISTS `timer_job`;
    CREATE TABLE `timer_job` (
      `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键id',
      `job_name` varchar(80) NOT NULL COMMENT '任务名称',
      `job_group` varchar(80) NOT NULL COMMENT '任务分组',
      `job_type` smallint(6) NOT NULL COMMENT '任务类型(1=cron任务 2=常规重复性任务)',
      `destination` varchar(200) NOT NULL COMMENT '任务触发后发送消息的目的地',
      `job_param_json` varchar(200) DEFAULT '' COMMENT '任务参数(json格式)',
      `cron_expression` varchar(80) DEFAULT NULL COMMENT 'cron表达式',
      `start_time` datetime DEFAULT NULL COMMENT '任务开始时间',
      `end_time` datetime DEFAULT NULL COMMENT '任务结束时间',
      `repeat_count` int(1) DEFAULT '0' COMMENT '重复的次数(等于-1则一直重复下去)',
      `intervals` int(1) DEFAULT '0' COMMENT '间隔时间(单位：秒)',
      `job_desc` varchar(300) DEFAULT '' COMMENT '任务描述',
      `job_status` tinyint(1) DEFAULT '1' COMMENT '任务状态(1=正常 2=暂停 3=缓停(次数或结束时间到达后停止))',
      `deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除(0=否 1=是)',
      `create_time` datetime DEFAULT NULL COMMENT '创建时间',
      `update_time` datetime DEFAULT NULL COMMENT '更新时间',
      PRIMARY KEY (`id`),
      UNIQUE KEY `unique_name_group` (`job_name`,`job_group`)
    ) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8;
    
    DROP TABLE IF EXISTS `user`;
    CREATE TABLE `user` (
      `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id(主键)',
      `phone` varchar(30) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '手机',
      `email` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '邮箱',
      `usercode` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '用户编码',
      `password` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '密码',
      `username` varchar(80) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '姓名',
      `gender` tinyint(1) NOT NULL DEFAULT '3' COMMENT '性别(1=男 2=女 3=保密)',
      `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态(1=正常 2=禁用)',
      `deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除(0=否 1=是)',
      `create_time` datetime NOT NULL COMMENT '创建时间',
      `update_time` datetime NOT NULL COMMENT '更新时间',
      PRIMARY KEY (`id`),
      UNIQUE KEY `uk_phone` (`phone`),
      UNIQUE KEY `uk_email` (`email`),
      UNIQUE KEY `uk_usercode` (`usercode`)
    ) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8 COMMENT='用户表';

9、意见和建议：
    
    由于个人知识和能力有限，而且这只是一个demo，纰漏和bug在所难免，欢迎各路大神拍砖指正，但请不要删库和跑路，个人邮箱：2380975530@qq.com，
    另外，这个项目有部署在个人服务器，需要体验项目的可以联系我