# MES 安全环保检测（SET）模块 交付说明

日期：2026-09-02 ｜ 交付范围：**P0(6) + P1(12) = 18 功能 / 18 张 `mes_set_*` 表**（后端 + DB 先行，前端/菜单延后）

## 1. 数据库

| 批次 | SQL 文件 | 表数 |
|---|---|---|
| P0 | `sql/00-sql文件/set-module-20260902-p0.sql` | 6 |
| P1 | `sql/00-sql文件/set-module-20260902-p1.sql`（已应用） | 12 |

18 张表均已 `CREATE TABLE IF NOT EXISTS` 应用。统一尾部列：`creator/create_time/updater/update_time/deleted/tenant_id`，业务唯一键：`uk_record_no`（排放口 `uk_outlet_code`、碳排放 `uk_calc_no`、排污许可 `uk_permit_no`、环保报告 `uk_report_no`）。

## 2. Java（12 个 P1 功能，各 8 文件 = 96 文件）

根：`txgy-module-mes-server/src/main/java/cn/iocoder/txgy/module/mes/`
每个功能 = controller/admin/set/{pkg}/(+vo) + service/set/{pkg} + dal/dataobject/set/{pkg} + dal/mysql/set/{pkg}，1 DO + 1 Mapper + 3 VO + 1 Service + 1 ServiceImpl + 1 Controller。

| 功能 | pkg | 接口前缀 `/mes/safety-env/` | 错误码 |
|---|---|---|---|
| 噪声检测 | noiserecord | noise-record | 1_040_806_xxx |
| 粉尘浓度 | dustrecord | dust-record | 1_040_807_xxx |
| 电气安全 | electricalrecord | electrical-record | 1_040_808_xxx |
| 压力容器 | pressurevessel | pressure-vessel | 1_040_809_xxx |
| PPE 防护 | ppecheck | ppe-check | 1_040_810_xxx |
| 排放口管理 | emissionoutlet | emission-outlet | 1_040_811_xxx |
| 废气排放 | exhaustgas | exhaust-gas | 1_040_812_xxx |
| 废水排放 | wastewater | wastewater | 1_040_813_xxx |
| 碳排放核算 | carbonemission | carbon-emission | 1_040_814_xxx |
| 排污许可 | pollutionpermit | pollution-permit | 1_040_815_xxx |
| 环保报告 | envreport | env-report | 1_040_816_xxx |
| 职业病危害 | occupationalhazard | occupational-hazard | 1_040_817_xxx |

每功能错误码 2 个：`SET_<XX>_NOT_EXISTS` / `SET_<XX>_NO_DUPLICATE`，追加于 `txgy-module-mes-api/.../enums/ErrorCodeConstants.java`（806→817 段）。

## 3. 权限前缀（供前端菜单 SQL 阶段使用）

每功能 4 个按钮：`mes:set-<slug>:create/update/delete/query`，其中 slug 为表 2 的“接口前缀”。放菜单时 type=3 按钮（子节点）挂在父级 type=1 目录下即可，前端根据接口名自动识别（本 fork 权限经 Redis `permission_menu_ids:...` 缓存，直接 SQL 插菜单后须清除对应 key）。

**18 功能全量菜单+按钮 SQL 已生成并应用**：`sql/00-sql文件/set-module-20260902-menu.sql`
- 结构：MES(5100) > 新目录 34000「安全环保检测」(/mes/safetyEnv, type1) > 18 功能菜单(type2, id 34001~34018) > 各 4 按钮(type3, id 34101~34172)。
- 预留 ID 段 34000~34200，顶部含防重 DELETE；91 条 INSERT。
- **前端页面契约已落实**：`txgy-ui-admin-vben/apps/web-antd/src/views/mes/safetyEnv/<camel>/` 下 18 套页面（index.vue + data.ts + modules/form.vue + api/mes/safetyEnv/<camel>/index.ts）已建并通过 `pnpm -F @vben/web-antd typecheck`。`component` 路径 `mes/safetyEnv/<camel>/index` 与 `component_name` 均已匹配。
- 应用后已清 Redis（`menu_role_ids:* / permission_menu_ids:* / role:* / user_role_ids:*`），需重新登录刷新菜单。

## 4. 构建/运行现状

- JDK25 + Maven 3.9.16；编译命令（mes-server 运行中禁止 `clean`）：
  `mvn -P '!jdk-17' -DskipTests -pl :txgy-module-mes-server -am compile|package`
- 打包需带 `-am`（兄弟 SNAPSHOT 未装入本地仓库）。
- `application-local.yaml` 主从库密码已修正为 `root`（不再需要启动参数覆盖）；当前运行 jar 为重新打包版本，密码已内嵌。
- mes-server 已重启于 gateway 48080 / system 48081 / mes 48091 / Nacos 8848 体系内。

## 5. 冒烟记录

- 方法：tenant1 超级管理员（admin/admin123）经网关调用 12 功能 `POST /create` + `GET /page`。
- 结果：12/12 均 `code:0`，创建返回 id，分页 `total:1` 且字段回显正确（LocalDateTime 按 epoch-ms、LocalDate 按 `yyyy-MM-dd`、BigDecimal 正常）。
- 12 条冒烟数据已通过 `DELETE /delete` 全部清理，表内无残留。
- 多租户隔离沿用 P0 已验证结论（BaseDO 租户拦截），P1 同机制未重复验证。

## 6. 前端阶段进展（2026-09-02）

1. ✅ 18 套 vben 页面已建（web-antd flavor），样板 pressureVessel 手写锁型，其余 17 套由 `~/.claude/jobs/…/tmp/gen_set_pages.py` 按后端 VO 生成（字段映射精确），全量 `pnpm -F @vben/web-antd typecheck` EXIT=0。
2. ✅ 菜单 SQL `set-module-20260902-menu.sql` 已应用（91 行，id 34000~34200），并已清 Redis 菜单/权限缓存；`/system/menu/list` 已能返回新菜单。
3. ⏳ 尚未做：浏览器 dev 冒烟（`pnpm dev:antd` → 登录 → 展开「安全环保检测」逐页试增删查）；`system_role_menu` 绑定租户普通角色（super_admin 自动全有）；数据字典化（噪声 sourceType/粉尘 dustType/污染物 pollutantCode/报告类型 reportType 等，现为页内静态选项 + 字符串码，枚举码见各 VO Schema 注释）。
