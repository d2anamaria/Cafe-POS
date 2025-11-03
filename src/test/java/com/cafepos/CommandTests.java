package com.cafepos;

import com.cafepos.command.*;
import com.cafepos.order.Order;
import com.cafepos.order.OrderIds;
import com.cafepos.payment.CardPayment;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CommandTests {

    @Test
    void add_and_undo_item_command() {
        Order order = new Order(OrderIds.next());
        OrderService service = new OrderService(order);

        Command addLatte = new AddItemCommand(service, "LAT+L", 2);
        addLatte.execute();
        assertEquals(1, order.items().size(), "Order should contain one line after add");

        addLatte.undo();
        assertEquals(0, order.items().size(), "Undo should remove last line item");
    }

    @Test
    void macro_command_executes_and_undoes_in_reverse_order() {
        Order order = new Order(OrderIds.next());
        OrderService service = new OrderService(order);

        Command addEsp = new AddItemCommand(service, "ESP", 1);
        Command addLat = new AddItemCommand(service, "LAT", 1);
        Command macro = new MacroCommand(addEsp, addLat);

        macro.execute();
        assertEquals(2, order.items().size(), "Two items added by macro");

        macro.undo();
        assertEquals(0, order.items().size(), "Undo should revert both adds");
    }

    @Test
    void remote_executes_commands_and_supports_undo() {
        Order order = new Order(OrderIds.next());
        OrderService service = new OrderService(order);
        PosRemote remote = new PosRemote(3);

        remote.setSlot(0, new AddItemCommand(service, "ESP+SHOT+OAT", 1));
        remote.setSlot(1, new AddItemCommand(service, "LAT+L", 2));
        remote.setSlot(2, new PayOrderCommand(service, new CardPayment("1234567890123456"), 10));

        remote.press(0);
        remote.press(1);
        assertEquals(2, order.items().size());

        remote.undo();
        assertEquals(1, order.items().size(), "Undo should remove last added item");

        remote.press(2);
    }
}
