-- ==========================================
-- QQBot AI Agent 平台 - 数据库初始化脚本
-- ==========================================
-- 使用方式:
--   psql -h 127.0.0.1 -p 15432 -U postgres -f init.sql
--
-- 注意：首次执行前需要先创建数据库和用户
--   CREATE USER qqbot WITH PASSWORD 'qqbot123';
--   CREATE DATABASE qqbot OWNER qqbot;
--   GRANT ALL PRIVILEGES ON DATABASE qqbot TO qqbot;
-- ==========================================

-- 1. QQ 用户表
CREATE TABLE IF NOT EXISTS qq_user (
    id              BIGSERIAL       PRIMARY KEY,
    qq_id           BIGINT          NOT NULL UNIQUE,
    nickname        VARCHAR(128)    DEFAULT '',
    avatar          VARCHAR(512)    DEFAULT '',
    create_time     TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time     TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE  qq_user              IS 'QQ用户表';
COMMENT ON COLUMN qq_user.id           IS '主键ID';
COMMENT ON COLUMN qq_user.qq_id        IS 'QQ号（唯一）';
COMMENT ON COLUMN qq_user.nickname     IS 'QQ昵称';
COMMENT ON COLUMN qq_user.avatar       IS '头像URL';
COMMENT ON COLUMN qq_user.create_time  IS '创建时间';
COMMENT ON COLUMN qq_user.update_time  IS '更新时间';

-- 2. 消息记录表
CREATE TABLE IF NOT EXISTS message_record (
    id              BIGSERIAL       PRIMARY KEY,
    user_id         BIGINT          NOT NULL,
    qq_id           BIGINT          NOT NULL,
    nickname        VARCHAR(128)    DEFAULT '',
    role            VARCHAR(16)     NOT NULL,
    content         TEXT            NOT NULL,
    message_type    VARCHAR(16)     DEFAULT 'private',
    group_id        BIGINT,
    create_time     TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE  message_record               IS '消息记录表';
COMMENT ON COLUMN message_record.id            IS '主键ID';
COMMENT ON COLUMN message_record.user_id       IS '关联用户ID（qq_user.id）';
COMMENT ON COLUMN message_record.qq_id         IS 'QQ号（冗余字段）';
COMMENT ON COLUMN message_record.nickname      IS '用户昵称快照';
COMMENT ON COLUMN message_record.role          IS '消息角色：user/assistant';
COMMENT ON COLUMN message_record.content       IS '消息内容';
COMMENT ON COLUMN message_record.message_type  IS '消息类型：private/group';
COMMENT ON COLUMN message_record.group_id      IS '群号（群聊时有值）';
COMMENT ON COLUMN message_record.create_time   IS '创建时间';

-- 消息记录索引
CREATE INDEX IF NOT EXISTS idx_message_record_user_id    ON message_record(user_id);
CREATE INDEX IF NOT EXISTS idx_message_record_create_time ON message_record(create_time);
CREATE INDEX IF NOT EXISTS idx_message_record_user_time   ON message_record(user_id, create_time);

-- 3. AI 角色表
CREATE TABLE IF NOT EXISTS ai_character (
    id              BIGSERIAL       PRIMARY KEY,
    name            VARCHAR(64)     NOT NULL UNIQUE,
    description     VARCHAR(256)    DEFAULT '',
    system_prompt   TEXT            NOT NULL,
    is_default      BOOLEAN         NOT NULL DEFAULT FALSE,
    enabled         BOOLEAN         NOT NULL DEFAULT TRUE,
    create_time     TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time     TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE  ai_character                 IS 'AI角色/人格配置表';
COMMENT ON COLUMN ai_character.id              IS '主键ID';
COMMENT ON COLUMN ai_character.name            IS '角色名称（唯一）';
COMMENT ON COLUMN ai_character.description     IS '角色描述';
COMMENT ON COLUMN ai_character.system_prompt   IS 'System Prompt';
COMMENT ON COLUMN ai_character.is_default      IS '是否为默认角色';
COMMENT ON COLUMN ai_character.enabled         IS '是否启用';
COMMENT ON COLUMN ai_character.create_time     IS '创建时间';
COMMENT ON COLUMN ai_character.update_time     IS '更新时间';

-- 4. 插入一个默认角色
INSERT INTO ai_character (name, description, system_prompt, is_default, enabled)
VALUES (
    '默认助手',
    '默认的QQ聊天机器人角色，回复简洁自然',
    '你是一个QQ聊天机器人。

你的任务：
自然回复用户。

要求：
1. 回复简洁
2. 像真人聊天
3. 不暴露自己内部实现
4. 避免机械化回答',
    TRUE,
    TRUE
) ON CONFLICT (name) DO NOTHING;
