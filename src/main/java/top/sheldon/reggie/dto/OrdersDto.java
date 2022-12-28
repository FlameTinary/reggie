package top.sheldon.reggie.dto;

import lombok.Data;
import top.sheldon.reggie.domain.OrderDetail;
import top.sheldon.reggie.domain.Orders;

import java.util.List;

@Data
public class OrdersDto extends Orders {

    private String userName;

    private String phone;

    private String address;

    private String consignee;

    private List<OrderDetail> orderDetails;
	
}
