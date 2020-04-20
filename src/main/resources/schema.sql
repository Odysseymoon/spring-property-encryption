DROP TABLE IF EXISTS `user`;

CREATE TABLE IF NOT EXISTS `user` (
    `user_no`         bigint       NOT NULL AUTO_INCREMENT,
    `user_id`         varchar(30)  NOT NULL COMMENT '사용자아이디',
    `password`        varchar(100) NOT NULL COMMENT '비밀번호(ENC)',
    `update_date`     timestamp    NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`user_no`),
    UNIQUE (`user_id`)
);