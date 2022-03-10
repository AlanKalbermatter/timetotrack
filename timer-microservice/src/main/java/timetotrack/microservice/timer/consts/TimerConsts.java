/*
 * Copyright (c) 2022. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package timetotrack.microservice.timer.consts;

public interface TimerConsts {
    private static final String CREATE_STATEMENT = "CREATE TABLE IF NOT EXISTS `product` (\n" +
            "  `productId` VARCHAR(60) NOT NULL,\n" +
            "  `sellerId` varchar(30) NOT NULL,\n" +
            "  `name` varchar(255) NOT NULL,\n" +
            "  `price` double NOT NULL,\n" +
            "  `illustration` MEDIUMTEXT NOT NULL,\n" +
            "  `type` varchar(45) NOT NULL,\n" +
            "  PRIMARY KEY (`productId`),\n" +
            "  KEY `index_seller` (`sellerId`) )";
    private static final String INSERT_STATEMENT = "INSERT INTO product (`productId`, `sellerId`, `name`, `price`, `illustration`, `type`) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String FETCH_STATEMENT = "SELECT * FROM product WHERE productId = ?";
    private static final String FETCH_ALL_STATEMENT = "SELECT * FROM product";
    private static final String FETCH_WITH_PAGE_STATEMENT = "SELECT * FROM product LIMIT ?, ?";
    private static final String DELETE_STATEMENT = "DELETE FROM product WHERE productId = ?";
    private static final String DELETE_ALL_STATEMENT = "DELETE FROM product";
}
