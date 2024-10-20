SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for clicks
-- ----------------------------
DROP TABLE IF EXISTS `clicks`;
CREATE TABLE `clicks`  (
  `impression_id` varchar(36) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL,
  `revenue` double NULL DEFAULT NULL,
  `id` int NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `impression_id`(`impression_id`) USING BTREE,
  CONSTRAINT `clicks_ibfk_1` FOREIGN KEY (`impression_id`) REFERENCES `impressions` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 96 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for impressions
-- ----------------------------
DROP TABLE IF EXISTS `impressions`;
CREATE TABLE `impressions`  (
  `id` varchar(36) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL,
  `app_id` int NULL DEFAULT NULL,
  `country_code` varchar(2) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL,
  `advertiser_id` int NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
