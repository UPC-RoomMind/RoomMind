CREATE DATABASE IF NOT EXISTS dev_roommind DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE dev_roommind;

CREATE TABLE IF NOT EXISTS appuser (
    Id INT AUTO_INCREMENT PRIMARY KEY,
    UserName VARCHAR(50) NOT NULL COMMENT '账号',
    Password VARCHAR(255) NOT NULL COMMENT '密码',
    Email VARCHAR(100) COMMENT '邮箱',
    ImageUrls VARCHAR(500) COMMENT '头像',
    Name VARCHAR(50) COMMENT '名称',
    PhoneNumber VARCHAR(20) COMMENT '手机号码',
    Birth DATETIME COMMENT '出生年月',
    RoleType INT DEFAULT 0 COMMENT '用户角色',
    OverdueTimes INT DEFAULT 0 COMMENT '逾期次数',
    CreationTime DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    CreatorId INT COMMENT '创建人'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

CREATE TABLE IF NOT EXISTS room (
    Id INT AUTO_INCREMENT PRIMARY KEY,
    Name VARCHAR(100) NOT NULL COMMENT '名称',
    Cover VARCHAR(500) COMMENT '封面',
    Address VARCHAR(500) COMMENT '地址',
    Content TEXT COMMENT '介绍',
    EveryMonCancelCount INT DEFAULT 3 COMMENT '每月可取消次数',
    CreationTime DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    CreatorId INT COMMENT '创建人'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='自习室表';

CREATE TABLE IF NOT EXISTS seat (
    Id INT AUTO_INCREMENT PRIMARY KEY,
    No VARCHAR(20) NOT NULL COMMENT '编号',
    SRow INT NOT NULL COMMENT '行',
    SCol INT NOT NULL COMMENT '列',
    IsMaintain TINYINT(1) DEFAULT 0 COMMENT '是否维修',
    RoomId INT NOT NULL COMMENT '自习室ID',
    CreationTime DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    CreatorId INT COMMENT '创建人',
    CONSTRAINT fk_seat_room FOREIGN KEY (RoomId) REFERENCES room(Id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='座位表';

CREATE TABLE IF NOT EXISTS appointrecord (
    Id INT AUTO_INCREMENT PRIMARY KEY,
    RoomId INT NOT NULL COMMENT '自习室ID',
    SeatId INT NOT NULL COMMENT '座位ID',
    UserId INT NOT NULL COMMENT '预约人ID',
    No VARCHAR(50) COMMENT '流水编号',
    Phone VARCHAR(20) COMMENT '手机号',
    Name VARCHAR(50) COMMENT '姓名',
    AppointDateType INT DEFAULT 0 COMMENT '预约时间段范围类型',
    AppointDate DATETIME COMMENT '预约日期',
    BeginTime DATETIME COMMENT '起始时间',
    EndTime DATETIME COMMENT '截至时间',
    CommentScore DOUBLE COMMENT '评分',
    SComment TEXT COMMENT '评论',
    AppointStatus INT DEFAULT 0 COMMENT '预约状态',
    CreationTime DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    CreatorId INT COMMENT '创建人',
    CONSTRAINT fk_appoint_room FOREIGN KEY (RoomId) REFERENCES room(Id) ON DELETE CASCADE,
    CONSTRAINT fk_appoint_seat FOREIGN KEY (SeatId) REFERENCES seat(Id) ON DELETE CASCADE,
    CONSTRAINT fk_appoint_user FOREIGN KEY (UserId) REFERENCES appuser(Id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预约记录表';

CREATE TABLE IF NOT EXISTS banner (
    Id INT AUTO_INCREMENT PRIMARY KEY,
    Cover VARCHAR(500) COMMENT '封面',
    Remark VARCHAR(500) COMMENT '备注',
    CreationTime DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    CreatorId INT COMMENT '创建人'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='封面表';

CREATE TABLE IF NOT EXISTS integral (
    Id INT AUTO_INCREMENT PRIMARY KEY,
    Title VARCHAR(100) COMMENT '标题',
    UserId INT NOT NULL COMMENT '用户ID',
    IntegralValue INT DEFAULT 0 COMMENT '积分值',
    Source VARCHAR(100) COMMENT '来源',
    RelativeCode VARCHAR(50) COMMENT '关联号',
    CreationTime DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    CreatorId INT COMMENT '创建人',
    CONSTRAINT fk_integral_user FOREIGN KEY (UserId) REFERENCES appuser(Id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='积分表';

CREATE TABLE IF NOT EXISTS vaildcode (
    Id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(100) NOT NULL COMMENT '邮箱',
    code VARCHAR(10) NOT NULL COMMENT '验证码'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='验证码表';

INSERT INTO appuser (UserName, Password, Email, Name, RoleType) VALUES 
('admin', '123456', 'admin@example.com', '管理员', 1);

INSERT INTO room (Name, Cover, Address, Content, EveryMonCancelCount) VALUES 
('阳光自习室', '/uploads/room/cover1.jpg', '教学楼A栋301', '宽敞明亮，配备空调和WiFi，适合全天学习', 3),
('静谧自习室', '/uploads/room/cover2.jpg', '图书馆B区2楼', '安静舒适，24小时开放', 3),
('VIP自习室', '/uploads/room/cover3.jpg', '综合楼501', '独立隔间，提供茶饮服务', 5);

INSERT INTO seat (No, SRow, SCol, IsMaintain, RoomId) VALUES 
('A1', 1, 1, 0, 1),
('A2', 1, 2, 0, 1),
('A3', 1, 3, 0, 1),
('B1', 2, 1, 0, 1),
('B2', 2, 2, 1, 1),
('B3', 2, 3, 0, 1),
('C1', 3, 1, 0, 1),
('C2', 3, 2, 0, 1),
('C3', 3, 3, 0, 1),
('D1', 1, 1, 0, 2),
('D2', 1, 2, 0, 2),
('D3', 1, 3, 0, 2),
('E1', 2, 1, 0, 2),
('E2', 2, 2, 0, 2),
('E3', 2, 3, 0, 2),
('F1', 1, 1, 0, 3),
('F2', 1, 2, 0, 3),
('F3', 1, 3, 0, 3),
('G1', 2, 1, 0, 3),
('G2', 2, 2, 0, 3);

INSERT INTO banner (Cover, Remark) VALUES 
('/uploads/banner/banner1.jpg', '首页轮播图1'),
('/uploads/banner/banner2.jpg', '首页轮播图2'),
('/uploads/banner/banner3.jpg', '首页轮播图3');