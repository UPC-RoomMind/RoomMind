# 软硬件结合门禁签到签退系统方案

---

## 一、项目概述

### 1.1 背景

RoomAppoint 自习室预约系统目前采用纯软件方式进行预约管理，用户需要手动在手机或电脑上进行签到签退操作。为提升用户体验和管理效率，计划引入硬件设备实现自动化签到签退，实现真正的"软硬结合"。

### 1.2 目标

通过集成门禁设备（RFID读卡器、二维码扫码枪、人脸识别终端等），实现：

1. **自动签到**：用户进入自习室时自动识别并完成签到
2. **自动签退**：用户离开自习室时自动识别并完成签退
3. **门禁联动**：只有拥有有效预约的用户才能进入
4. **数据统计**：准确记录用户实际使用时长和行为数据

### 1.3 设计原则

| 原则 | 说明 |
| :--- | :--- |
| **分层解耦** | 硬件层、网关层、业务层相互独立，降低耦合 |
| **接口标准化** | API接口与硬件类型无关，支持多种验证方式 |
| **渐进式开发** | 先开发软件模拟版本，硬件到位后快速对接 |
| **可扩展性** | 预留扩展接口，支持未来接入更多设备类型 |

---

## 二、整体架构设计

### 2.1 三层架构

```
┌─────────────────────────────────────────────────────────────────────┐
│                          硬件层                                     │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────────────┐    │
│  │  RFID读卡器 │  │  二维码扫码枪 │  │  人脸识别终端 │  │  门禁控制器   │    │
│  └────┬─────┘  └────┬─────┘  └────┬─────┘  └────────┬─────────┘    │
└───────┼─────────────┼─────────────┼──────────────────┼──────────────┘
        │             │             │                  │
        ▼             ▼             ▼                  ▼
┌─────────────────────────────────────────────────────────────────────┐
│                          网关层                                     │
│  ┌──────────────────────────────────────────────────────────────┐   │
│  │                      IoT网关 / 串口服务器                      │   │
│  │  协议转换：TCP/IP <-> RS485/RS232  |  数据格式统一          │   │
│  └──────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────┘
        │
        ▼
┌─────────────────────────────────────────────────────────────────────┐
│                          服务层                                     │
│  ┌────────────┐  ┌─────────────┐  ┌──────────────────────────┐    │
│  │ 签到签退API │  │ 预约状态同步 │  │ 门禁控制指令（可选）      │    │
│  └──────┬─────┘  └──────┬──────┘  └────────────┬─────────────┘    │
│         │               │                      │                   │
│         ▼               ▼                      ▼                   │
│  ┌──────────────────────────────────────────────────────────────┐   │
│  │                   RoomAppoint 后端服务                        │   │
│  │  用户认证 | 预约验证 | 状态更新 | 积分计算 | 日志记录         │   │
│  └──────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────┘
```

### 2.2 架构说明

| 层级 | 职责 | 是否需要硬件 |
| :--- | :--- | :--- |
| **硬件层** | 采集用户身份信息（卡号、二维码、人脸） | ✅ |
| **网关层** | 协议转换、数据格式统一、设备管理 | ❌（可软件模拟） |
| **服务层** | 业务逻辑处理、数据库操作、API接口 | ❌ |

---

## 三、硬件选型方案

### 3.1 方案对比

| 方案 | 设备 | 优势 | 劣势 | 成本 |
| :--- | :--- | :--- | :--- | :--- |
| **RFID卡** | RFID读卡器 + IC卡 | 校园卡复用，无需额外卡片 | 需要携带卡片 | 低（800-1500元） |
| **二维码** | 扫码枪 | 用户出示手机即可，无需额外硬件 | 需要用户主动出示 | 中（300-800元） |
| **人脸识别** | 人脸终端 | 无接触，安全性高 | 设备成本高 | 高（1000-3000元） |

### 3.2 推荐方案：RFID卡

```
设备清单：
├── RFID读卡器：4台（每自习室1台）
├── 门禁控制器：4台
├── IC卡：按需采购（可复用校园卡）
└── 串口服务器：2台（RS485转TCP/IP）
```

---

## 四、数据库扩展设计

### 4.1 扩展预约记录表（appointrecord）

```sql
ALTER TABLE appointrecord 
ADD COLUMN SignInTime DATETIME NULL COMMENT '签到时间',
ADD COLUMN SignOutTime DATETIME NULL COMMENT '签退时间',
ADD COLUMN SignInDevice VARCHAR(50) NULL COMMENT '签到设备编号',
ADD COLUMN SignOutDevice VARCHAR(50) NULL COMMENT '签退设备编号',
ADD COLUMN IsSignedIn TINYINT(1) DEFAULT 0 COMMENT '是否已签到',
ADD COLUMN IsSignedOut TINYINT(1) DEFAULT 0 COMMENT '是否已签退',
ADD COLUMN ActualDuration INT NULL COMMENT '实际使用时长(分钟)';
```

### 4.2 新增设备管理表（device）

```sql
CREATE TABLE device (
    Id INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    DeviceNo VARCHAR(50) NOT NULL UNIQUE COMMENT '设备编号',
    DeviceName VARCHAR(100) COMMENT '设备名称',
    DeviceType INT NOT NULL COMMENT '设备类型(1=RFID,2=扫码枪,3=人脸终端)',
    RoomId INT COMMENT '所属自习室ID',
    IpAddress VARCHAR(50) COMMENT 'IP地址',
    Status TINYINT(1) DEFAULT 1 COMMENT '状态(0=离线,1=在线)',
    LastHeartbeat DATETIME COMMENT '最后心跳时间',
    CreationTime DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    CreatorId INT COMMENT '创建人ID'
);
```

### 4.3 新增签到日志表（sign_log）

```sql
CREATE TABLE sign_log (
    Id INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    UserId INT NOT NULL COMMENT '用户ID',
    RoomId INT NOT NULL COMMENT '自习室ID',
    SeatId INT COMMENT '座位ID',
    AppointRecordId INT COMMENT '关联预约记录ID',
    SignType INT NOT NULL COMMENT '签到类型(1=签到,2=签退)',
    DeviceNo VARCHAR(50) COMMENT '设备编号',
    DeviceType INT COMMENT '设备类型',
    VerifyMethod VARCHAR(50) COMMENT '验证方式(RFID/二维码/人脸)',
    CardNo VARCHAR(50) COMMENT '刷卡卡号',
    QrCodeContent VARCHAR(200) COMMENT '二维码内容',
    FaceImageUrl VARCHAR(255) COMMENT '人脸照片路径',
    Result INT NOT NULL COMMENT '结果(0=失败,1=成功)',
    ResultMsg VARCHAR(200) COMMENT '结果描述',
    CreationTime DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
);
```

---

## 五、API接口设计

### 5.1 签到接口

```
POST /Sign/SignIn
Content-Type: application/json
```

**请求参数**：

| 参数 | 类型 | 必填 | 说明 |
| :--- | :--- | :--- | :--- |
| DeviceNo | String | 是 | 设备编号 |
| CardNo | String | 否 | RFID卡号（RFID方式） |
| QrCode | String | 否 | 二维码内容（二维码方式） |
| FaceToken | String | 否 | 人脸比对Token（人脸方式） |

**成功响应**：

```json
{
    "Success": true,
    "Code": 200,
    "Msg": "签到成功",
    "Data": {
        "UserId": 1,
        "UserName": "张三",
        "RoomId": 1,
        "RoomName": "第一自习室",
        "SeatId": 101,
        "SeatNo": "A-01",
        "AppointRecordId": 1001,
        "SignInTime": "2026-07-08 08:30:00",
        "BeginTime": "2026-07-08 08:00:00",
        "EndTime": "2026-07-08 12:00:00",
        "AllowAccess": true,
        "Message": "欢迎进入，请前往A-01座位"
    }
}
```

### 5.2 签退接口

```
POST /Sign/SignOut
Content-Type: application/json
```

**请求参数**：

| 参数 | 类型 | 必填 | 说明 |
| :--- | :--- | :--- | :--- |
| DeviceNo | String | 是 | 设备编号 |
| CardNo | String | 否 | RFID卡号 |
| QrCode | String | 否 | 二维码内容 |
| FaceToken | String | 否 | 人脸比对Token |

**成功响应**：

```json
{
    "Success": true,
    "Code": 200,
    "Msg": "签退成功",
    "Data": {
        "UserId": 1,
        "RoomId": 1,
        "SeatId": 101,
        "AppointRecordId": 1001,
        "SignOutTime": "2026-07-08 11:45:00",
        "SignInTime": "2026-07-08 08:30:00",
        "ActualDuration": 195,
        "ExpectedDuration": 240,
        "IntegralChange": 5,
        "Message": "感谢使用，本次获得5积分"
    }
}
```

### 5.3 设备心跳接口

```
POST /Device/Heartbeat
Content-Type: application/json
```

**请求参数**：

| 参数 | 类型 | 必填 | 说明 |
| :--- | :--- | :--- | :--- |
| DeviceNo | String | 是 | 设备编号 |
| Status | Int | 是 | 状态(1=在线) |

### 5.4 查询用户当前状态

```
POST /Sign/GetUserStatus
Content-Type: application/json
```

**请求参数**：

| 参数 | 类型 | 必填 | 说明 |
| :--- | :--- | :--- | :--- |
| UserId | Int | 是 | 用户ID |

**响应**：

```json
{
    "Success": true,
    "Data": {
        "IsInRoom": true,
        "RoomId": 1,
        "RoomName": "第一自习室",
        "SeatId": 101,
        "SeatNo": "A-01",
        "SignInTime": "2026-07-08 08:30:00",
        "EndTime": "2026-07-08 12:00:00",
        "RemainingMinutes": 15
    }
}
```

---

## 六、业务流程设计

### 6.1 签到流程

```
用户刷卡/扫码/人脸识别
       │
       ▼
  获取用户标识(CardNo/QrCode/FaceToken)
       │
       ▼
  查询用户是否有有效预约
       │
       ├── 无有效预约 ──→ 返回"无预约，禁止进入"
       │
       ▼
  验证预约状态(必须是已预约未签到)
       │
       ├── 状态不符 ──→ 返回"预约状态异常"
       │
       ▼
  检查当前时间是否在预约时间段内
       │
       ├── 过早(提前>30分钟) ──→ 返回"预约时间未到"
       │
       ├── 迟到(超过预约时间>15分钟) ──→ 返回"已超时，预约已取消"
       │
       ▼
  更新预约记录：SignInTime=当前时间, IsSignedIn=1
       │
       ▼
  记录签到日志
       │
       ▼
  返回"签到成功，允许进入"
       │
       ▼
  门禁控制器开门
```

### 6.2 签退流程

```
用户刷卡/扫码/人脸识别
       │
       ▼
  获取用户标识
       │
       ▼
  查询用户是否有已签到未签退的记录
       │
       ├── 无签到记录 ──→ 返回"您当前没有在自习室"
       │
       ▼
  更新预约记录：SignOutTime=当前时间, IsSignedOut=1
       │
       ▼
  计算实际使用时长
       │
       ▼
  根据使用情况计算积分
       │
       ├── 准时签退 ──→ 加积分
       │
       ├── 提前离开 ──→ 正常积分
       │
       └── 超时离开 ──→ 扣积分/增加逾期次数
       │
       ▼
  更新预约状态为已完成
       │
       ▼
  记录签退日志
       │
       ▼
  返回"签退成功"
```

### 6.3 积分规则

| 情况 | 积分变化 | 说明 |
| :--- | :--- | :--- |
| 准时签到+准时签退 | +5分 | 正常完成预约 |
| 迟到≤15分钟 | +3分 | 迟到扣分 |
| 提前离开≥30分钟 | +2分 | 未充分利用 |
| 超时≤30分钟 | -2分 | 超时扣分 |
| 超时>30分钟 | -5分 + 逾期次数+1 | 严重超时 |

---

## 七、无硬件情况下的开发与测试方案

### 7.1 为什么不需要硬件也能开发

由于采用了分层解耦的架构设计，业务层与硬件层完全独立：

1. **API接口标准化**：签到签退API不依赖具体硬件类型
2. **数据格式统一**：所有硬件数据都转换为统一的JSON格式
3. **设备抽象层**：设备逻辑封装在独立服务中，可替换

### 7.2 三种模拟测试方法

#### 方法一：Mock模拟（推荐）

**原理**：直接调用API接口，传入模拟的硬件数据

**工具**：Postman / Swagger / curl

**示例**：

```bash
# 模拟签到（RFID方式）
curl -X POST http://localhost:7245/Sign/SignIn \
  -H "Content-Type: application/json" \
  -d '{
    "DeviceNo": "D001",
    "CardNo": "001234567890"
  }'

# 模拟签退（二维码方式）
curl -X POST http://localhost:7245/Sign/SignOut \
  -H "Content-Type: application/json" \
  -d '{
    "DeviceNo": "D002",
    "QrCode": "ROOMAPP://sign?uid=1&token=abc123"
  }'
```

**适用场景**：
- 快速验证API接口
- 单元测试
- 调试业务逻辑

#### 方法二：软件模拟器

**原理**：编写模拟程序，模拟硬件发送数据

**示例代码（Python）**：

```python
import requests
import time
import random

def simulate_sign_in():
    devices = ["D001", "D002", "D003", "D004"]
    card_nos = ["001234567890", "002345678901", "003456789012"]
    
    while True:
        device_no = random.choice(devices)
        card_no = random.choice(card_nos)
        
        data = {
            "DeviceNo": device_no,
            "CardNo": card_no
        }
        
        response = requests.post(
            "http://localhost:7245/Sign/SignIn",
            json=data
        )
        
        print(f"[{time.strftime('%Y-%m-%d %H:%M:%S')}] "
              f"设备:{device_no} 卡号:{card_no} "
              f"结果:{response.json()['Msg']}")
        
        time.sleep(5)

if __name__ == "__main__":
    simulate_sign_in()
```

**适用场景**：
- 并发测试
- 性能测试
- 自动化测试

#### 方法三：前端模拟页面

**原理**：在前端页面添加模拟按钮，触发签到签退API

**实现方案**：

```vue
<template>
  <div class="sign-simulation">
    <h2>签到签退模拟测试</h2>
    
    <el-select v-model="deviceNo" placeholder="选择设备">
      <el-option label="D001-第一自习室" value="D001"></el-option>
      <el-option label="D002-第二自习室" value="D002"></el-option>
      <el-option label="D003-第三自习室" value="D003"></el-option>
      <el-option label="D004-第四自习室" value="D004"></el-option>
    </el-select>
    
    <el-select v-model="verifyMethod" placeholder="验证方式">
      <el-option label="RFID刷卡" value="rfid"></el-option>
      <el-option label="二维码扫码" value="qr"></el-option>
    </el-select>
    
    <el-button type="success" @click="simulateSignIn">模拟签到</el-button>
    <el-button type="warning" @click="simulateSignOut">模拟签退</el-button>
    
    <div v-if="result" class="result">
      <pre>{{ JSON.stringify(result, null, 2) }}</pre>
    </div>
  </div>
</template>

<script>
export default {
  name: 'SignSimulation',
  data() {
    return {
      deviceNo: 'D001',
      verifyMethod: 'rfid',
      result: null
    }
  },
  methods: {
    async simulateSignIn() {
      const data = {
        DeviceNo: this.deviceNo,
        CardNo: this.verifyMethod === 'rfid' ? '001234567890' : undefined,
        QrCode: this.verifyMethod === 'qr' ? 'ROOMAPP://sign?uid=1&token=test' : undefined
      }
      
      const response = await this.$http.post('/Sign/SignIn', data)
      this.result = response.data
    },
    async simulateSignOut() {
      const data = {
        DeviceNo: this.deviceNo,
        CardNo: '001234567890'
      }
      
      const response = await this.$http.post('/Sign/SignOut', data)
      this.result = response.data
    }
  }
}
</script>

<style scoped>
.sign-simulation {
  padding: 20px;
}
.result {
  margin-top: 20px;
  padding: 15px;
  background: #f5f5f5;
  border-radius: 4px;
  max-height: 300px;
  overflow-y: auto;
}
</style>
```

**适用场景**：
- 可视化测试
- 非开发人员测试
- 用户体验验证

### 7.3 开发流程建议

| 阶段 | 是否需要硬件 | 完成内容 | 时间预估 |
| :--- | :--- | :--- | :--- |
| 第一阶段 | ❌ | 数据库设计、API接口开发、业务逻辑实现 | 1-2周 |
| 第二阶段 | ❌ | 单元测试、前端模拟页面、自动化测试脚本 | 1周 |
| 第三阶段 | ❌ | 异常场景测试（无预约、超时、重复签到等） | 1周 |
| 第四阶段 | ✅ | 硬件安装、协议适配、联调测试 | 1周 |
| 第五阶段 | ✅ | 优化调整、正式上线 | 1周 |

### 7.4 测试场景覆盖

#### 正常场景

| 场景 | 测试步骤 | 预期结果 |
| :--- | :--- | :--- |
| 准时签到 | 预约时间前30分钟内签到 | 签到成功，允许进入 |
| 准时签退 | 预约时间内签退 | 签退成功，计算积分 |
| 提前签到 | 预约时间前30分钟以上签到 | 返回"预约时间未到" |

#### 异常场景

| 场景 | 测试步骤 | 预期结果 |
| :--- | :--- | :--- |
| 无预约签到 | 没有预约记录时签到 | 返回"无预约，禁止进入" |
| 重复签到 | 已签到后再次签到 | 返回"您已签到" |
| 超时未签到 | 超过预约时间15分钟未签到 | 返回"已超时，预约已取消" |
| 无签到签退 | 没有签到记录时签退 | 返回"您当前没有在自习室" |
| 超时签退 | 超过预约结束时间签退 | 签退成功，扣积分 |

---

## 八、后端代码实现建议

### 8.1 新增SignController

```java
@RestController
@RequestMapping("/Sign")
public class SignController {

    @Autowired
    private SignService signService;

    @PostMapping("/SignIn")
    public ResultModel<SignResultDto> signIn(@RequestBody SignRequestDto request) {
        return signService.signIn(request);
    }

    @PostMapping("/SignOut")
    public ResultModel<SignResultDto> signOut(@RequestBody SignRequestDto request) {
        return signService.signOut(request);
    }

    @PostMapping("/GetUserStatus")
    public ResultModel<UserStatusDto> getUserStatus(@RequestBody UserStatusRequestDto request) {
        return signService.getUserStatus(request);
    }
}
```

### 8.2 新增SignService

```java
@Service
public class SignService {

    @Autowired
    private AppointRecordService appointRecordService;

    @Autowired
    private IntegralService integralService;

    @Autowired
    private SignLogService signLogService;

    public ResultModel<SignResultDto> signIn(SignRequestDto request) {
        AppUser user = getUserByIdentifier(request);
        
        AppointRecord record = appointRecordService.getValidAppointment(user.getId());
        
        if (!validateAppointment(record)) {
            return ResultModel.error("预约状态异常");
        }
        
        record.setSignInTime(LocalDateTime.now());
        record.setIsSignedIn(1);
        record.setSignInDevice(request.getDeviceNo());
        appointRecordService.updateById(record);
        
        signLogService.saveSignLog(user.getId(), record.getId(), 1, request);
        
        return ResultModel.success(buildSignInResult(user, record));
    }

    public ResultModel<SignResultDto> signOut(SignRequestDto request) {
        AppUser user = getUserByIdentifier(request);
        
        AppointRecord record = appointRecordService.getSignedInRecord(user.getId());
        
        record.setSignOutTime(LocalDateTime.now());
        record.setIsSignedOut(1);
        record.setSignOutDevice(request.getDeviceNo());
        
        int duration = calculateDuration(record.getSignInTime(), record.getSignOutTime());
        record.setActualDuration(duration);
        
        int integralChange = calculateIntegral(record);
        integralService.addIntegral(user.getId(), integralChange, "签退积分");
        
        record.setAppointStatus(3);
        appointRecordService.updateById(record);
        
        signLogService.saveSignLog(user.getId(), record.getId(), 2, request);
        
        return ResultModel.success(buildSignOutResult(user, record, integralChange));
    }
}
```

---

## 九、硬件对接方案

### 9.1 RFID读卡器对接

**硬件连接**：
```
RFID读卡器 → RS485 → 串口服务器 → 网络 → 后端API
```

**数据格式**：
```
设备上报格式：{ "DeviceNo": "D001", "CardNo": "001234567890", "Timestamp": "2026-07-08 08:30:00" }
```

### 9.2 二维码扫码枪对接

**硬件连接**：
```
扫码枪 → USB → 工控机/树莓派 → 网络 → 后端API
```

**二维码内容格式**：
```
ROOMAPP://sign?uid=1&token=abc123
```

### 9.3 人脸识别终端对接

**硬件连接**：
```
人脸终端 → 网络 → 后端API
```

**对接流程**：
1. 终端采集人脸图片
2. 调用 `/Face/Compare` 接口进行比对
3. 比对成功后调用签到/签退API

---

## 十、部署建议

### 10.1 服务器配置

| 服务 | 推荐配置 | 说明 |
| :--- | :--- | :--- |
| 主应用服务器 | 2核4G | RoomAppoint后端 |
| IoT网关服务器 | 1核2G | 处理硬件数据转发 |
| 数据库 | 2核4G | MySQL |

### 10.2 成本估算

| 项目 | 数量 | 单价 | 总计 |
| :--- | :--- | :--- | :--- |
| RFID读卡器 | 4台 | 200元 | 800元 |
| 门禁控制器 | 4台 | 300元 | 1200元 |
| IC卡 | 按需 | 2元 | 按需 |
| 串口服务器 | 2台 | 200元 | 400元 |
| **硬件合计** | - | - | **2400+元** |
| **开发工作量** | 约4-5周 | - | - |

---

## 十一、总结

### 11.1 关键要点

1. **分层解耦**是实现无硬件开发的核心，业务层与硬件层完全独立
2. **API接口标准化**使得硬件更换不影响业务逻辑
3. **渐进式开发**降低了项目风险，先完成软件模拟版本
4. **三种模拟测试方法**覆盖了不同的测试场景

### 11.2 实施建议

1. **立即开始开发**：不需要等待硬件，可以先完成业务逻辑和API接口
2. **优先实现模拟页面**：方便测试和演示
3. **硬件采购并行**：在开发的同时采购硬件，缩短项目周期
4. **接口文档先行**：先设计好API接口文档，硬件对接时只需适配协议

### 11.3 预期效果

完成后将实现：
- 用户刷卡即可自动签到/签退
- 门禁系统与预约系统联动
- 准确的使用时长统计
- 智能积分计算

---

**文档版本**：v1.0  
**适用项目**：RoomAppoint 自习室预约系统  
**最后更新**：2026年7月