-- =====================================================================
-- MES 安全环保检测（SET）模块 - P0 六表 DDL
-- 范围：SET-S-001 检测标准 / SET-S-002 检测计划 / SET-S-003 气体检测
--       SET-S-010 消防设施检测 / SET-S-011 危化品安全检测 / SET-E-006 危废台账
-- 依据：《MES安全环保检测-需求设计文档V1.0》§7.2 + §4/§5 功能详述
-- 说明：
--   1. 表名统一 mes_set_* 前缀（与全 MES 134 张表一致）
--   2. 枚举/状态/类型字段按文档 DDL 草图的 VARCHAR 代码存储（如 PASS/ACTIVE/GAS）
--      —— 不做 Integer+字典，减少骨架（若后续需下拉可加 system_dict_data，值不变）
--   3. 文档标 JSON 的配置字段（limits_config/trigger_config/applicable_area/photo_urls）
--      存 varchar 文本：Java 侧 DO 用 String 或 StringListTypeHandler（无 Map JSON handler）
--   4. 公共 7 列：creator/create_time/updater/update_time/deleted/tenant_id
--      tenant_id 由租户拦截器注入（BaseDO 非豁免），数据按租户隔离
-- =====================================================================

CREATE TABLE IF NOT EXISTS `mes_set_standard` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `standard_no` varchar(64) NOT NULL COMMENT '标准编号',
  `standard_name` varchar(200) NOT NULL COMMENT '标准名称',
  `domain` varchar(16) DEFAULT NULL COMMENT '检测域：SAFETY/ENV/HEALTH',
  `test_type` varchar(32) DEFAULT NULL COMMENT '检测类型：GAS/NOISE/DUST/RADIATION/ELECTRICAL/FIRE/CHEMICAL/PPE/PRESSURE等',
  `ref_standard` varchar(100) DEFAULT NULL COMMENT '引用国标编号（GBZ/GB/T）',
  `limits_config` varchar(4000) DEFAULT NULL COMMENT '限值配置(JSON文本，如{CO:{mac,pcTWA,unit}})',
  `method` varchar(500) DEFAULT NULL COMMENT '检测方法描述',
  `period_type` varchar(16) DEFAULT NULL COMMENT '周期类型：DAILY/WEEKLY/MONTHLY/QUARTERLY/YEARLY/EVENT',
  `trigger_config` varchar(1000) DEFAULT NULL COMMENT '事件触发配置(JSON文本)',
  `applicable_area` varchar(1000) DEFAULT NULL COMMENT '适用区域/工序(JSON文本)',
  `status` varchar(16) NOT NULL DEFAULT 'DRAFT' COMMENT '状态：DRAFT/ACTIVE/OBSOLETE',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_standard_no` (`standard_no`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='安全环保检测-检测标准';

CREATE TABLE IF NOT EXISTS `mes_set_plan` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `plan_no` varchar(64) NOT NULL COMMENT '计划编号',
  `plan_name` varchar(200) NOT NULL COMMENT '计划名称',
  `plan_type` varchar(16) NOT NULL DEFAULT 'PERIODIC' COMMENT '触发类型：PERIODIC(周期)/EVENT(事件)',
  `period_type` varchar(16) DEFAULT NULL COMMENT '周期类型(周期型)：DAILY/WEEKLY/MONTHLY/QUARTERLY/YEARLY',
  `start_date` date DEFAULT NULL COMMENT '生效开始日期',
  `end_date` date DEFAULT NULL COMMENT '生效结束日期',
  `machinery_id` bigint DEFAULT NULL COMMENT '关联设备编号（事件-设备型）',
  `operation_id` bigint DEFAULT NULL COMMENT '关联工序编号（事件-工单型）',
  `standard_id` bigint DEFAULT NULL COMMENT '关联检测标准编号',
  `assignee_id` bigint DEFAULT NULL COMMENT '责任人(执行人)编号',
  `status` varchar(16) NOT NULL DEFAULT 'DRAFT' COMMENT '状态：DRAFT/ACTIVE/STOPPED',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_plan_no` (`plan_no`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='安全环保检测-检测计划';

CREATE TABLE IF NOT EXISTS `mes_set_gas_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `record_no` varchar(64) NOT NULL COMMENT '记录编号',
  `plan_id` bigint DEFAULT NULL COMMENT '关联检测计划编号',
  `wo_id` bigint DEFAULT NULL COMMENT '关联工单编号（事件触发型）',
  `operation_id` bigint DEFAULT NULL COMMENT '关联工序编号',
  `permit_id` bigint DEFAULT NULL COMMENT '关联作业许可编号',
  `location` varchar(200) DEFAULT NULL COMMENT '检测位置',
  `gas_type` varchar(16) NOT NULL COMMENT '气体类型：CO/H2S/O2/LEL/VOC/NH3/CL2',
  `concentration` decimal(10,3) DEFAULT NULL COMMENT '检测浓度值',
  `unit` varchar(16) DEFAULT NULL COMMENT '单位：mg/m3 / % / %LEL',
  `limit_value` decimal(10,3) DEFAULT NULL COMMENT '限值',
  `result` varchar(8) DEFAULT NULL COMMENT '结果：PASS/FAIL',
  `collection_mode` varchar(16) NOT NULL DEFAULT 'MANUAL' COMMENT '采集方式：IOT_AUTO/MANUAL',
  `instrument_no` varchar(64) DEFAULT NULL COMMENT '检测仪器编号',
  `inspector` varchar(64) DEFAULT NULL COMMENT '检测人',
  `inspect_time` datetime NOT NULL COMMENT '检测时间',
  `photo_urls` varchar(2000) DEFAULT NULL COMMENT '检测照片URL（逗号分隔）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_record_no` (`record_no`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='安全环保检测-作业环境气体检测记录';

CREATE TABLE IF NOT EXISTS `mes_set_fire_check` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `record_no` varchar(64) NOT NULL COMMENT '记录编号',
  `plan_id` bigint DEFAULT NULL COMMENT '关联检测计划编号',
  `location` varchar(200) DEFAULT NULL COMMENT '区域/位置',
  `facility_name` varchar(200) NOT NULL COMMENT '设施名称（灭火器/消火栓/烟感/温感/应急照明/疏散指示等）',
  `facility_code` varchar(64) DEFAULT NULL COMMENT '设施编号(资产编号)',
  `check_time` datetime NOT NULL COMMENT '检测时间',
  `result` varchar(8) DEFAULT NULL COMMENT '结果：PASS/FAIL',
  `problem_desc` varchar(500) DEFAULT NULL COMMENT '异常/不合格描述',
  `inspector` varchar(64) DEFAULT NULL COMMENT '检测人',
  `photo_urls` varchar(2000) DEFAULT NULL COMMENT '检测照片URL（逗号分隔）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_record_no` (`record_no`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='安全环保检测-消防设施检测记录';

CREATE TABLE IF NOT EXISTS `mes_set_chemical_safety` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `record_no` varchar(64) NOT NULL COMMENT '记录编号',
  `plan_id` bigint DEFAULT NULL COMMENT '关联检测计划编号',
  `chemical_code` varchar(64) DEFAULT NULL COMMENT '危化品编码',
  `chemical_name` varchar(200) NOT NULL COMMENT '危化品名称',
  `storage_location` varchar(200) DEFAULT NULL COMMENT '存储地点',
  `label_ok` tinyint(1) NOT NULL DEFAULT 1 COMMENT '标识完整性：1是/0否',
  `msds_ok` tinyint(1) NOT NULL DEFAULT 1 COMMENT 'MSDS有效性：1是/0否',
  `storage_ok` tinyint(1) NOT NULL DEFAULT 1 COMMENT '储存条件(温湿度/通风)合格：1是/0否',
  `separation_ok` tinyint(1) NOT NULL DEFAULT 1 COMMENT '禁忌物分离合格：1是/0否',
  `result` varchar(8) DEFAULT NULL COMMENT '结果：PASS/FAIL',
  `problem_desc` varchar(500) DEFAULT NULL COMMENT '异常/不合格描述',
  `inspector` varchar(64) DEFAULT NULL COMMENT '检测人',
  `inspect_time` datetime NOT NULL COMMENT '检测时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_record_no` (`record_no`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='安全环保检测-危化品安全检测记录';

CREATE TABLE IF NOT EXISTS `mes_set_hazardous_waste` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `manifest_no` varchar(64) NOT NULL COMMENT '危废联单号',
  `waste_code` varchar(64) DEFAULT NULL COMMENT '危废代码（如 HW08）',
  `waste_name` varchar(200) NOT NULL COMMENT '危废名称',
  `quantity` decimal(12,3) DEFAULT NULL COMMENT '数量',
  `quantity_unit` varchar(16) DEFAULT NULL COMMENT '数量单位（吨等）',
  `stage` varchar(16) NOT NULL DEFAULT 'GENERATED' COMMENT '台账环节：GENERATED(产生)/STORED(贮存)/TRANSFERRED(转移)/DISPOSED(处置)',
  `storage_location` varchar(200) DEFAULT NULL COMMENT '贮存地点',
  `counterparty` varchar(200) DEFAULT NULL COMMENT '交接方/接收单位',
  `wo_id` bigint DEFAULT NULL COMMENT '来源工单编号',
  `handle_time` datetime DEFAULT NULL COMMENT '交接/处理时间',
  `handler` varchar(64) DEFAULT NULL COMMENT '经办人',
  `status` varchar(16) NOT NULL DEFAULT 'DRAFT' COMMENT '审批状态：DRAFT/APPROVED/REJECTED',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_manifest_no` (`manifest_no`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='安全环保检测-危废台账';

/*
 * =====================================================================
 * system_menu 登记 —— 延期至前端 phase（vben 会加载菜单 component 对应页面，
 * 本期无前端页面，注册会破坏运行中 UI 的菜单树；后端验证用 super_admin token
 * 已绕过权限检查，无需菜单行）。
 * 结构：MES 系统根目录 id=5100 下新增目录「安全环保检测」，其下 18 功能菜单+按钮。
 * 绑定：super_admin(role1) 自动拥有；tenant_admin(如华瀚 role 60185) 前端 phase 一并 INSERT。
 * =====================================================================
-- INSERT INTO system_menu (...) VALUES ...;
-- INSERT INTO system_role_menu (role_id, menu_id, ...) SELECT 60185, id, ... ;
 */
