CREATE TABLE IF NOT EXISTS `AppUser` (
    `Id` INT AUTO_INCREMENT PRIMARY KEY,
    `UserName` VARCHAR(100) NOT NULL,
    `Password` VARCHAR(200) NOT NULL,
    `Email` VARCHAR(100),
    `ImageUrls` VARCHAR(500),
    `Name` VARCHAR(100),
    `PhoneNumber` VARCHAR(20),
    `Birth` DATETIME,
    `RoleType