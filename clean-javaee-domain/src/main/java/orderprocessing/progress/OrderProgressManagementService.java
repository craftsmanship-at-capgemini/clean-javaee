package orderprocessing.progress;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.enterprise.event.Observes;

import orderprocessing.OrderEntity;
import orderprocessing.OrderKey;
import orderprocessing.OrderProgressManagementRemote;
import orderprocessing.OrderRepository;
import orderprocessing.scheduling.OrderSchedulerService;
import ordershipment.OrderDeliveredEvent;
import persistence.NotFoundException;

/**
 * {@link OrderProgressManagementService} manages second part of order
 * life-cycle. To see management of beginning of order life-cycle see
 * {@link OrderSchedulerService}.
 * <p>
 * Service implements {@link OrderProgressManagementRemote} and allows operators
 * to mark progress of order preparation.
 * <p>
 * Service observes events form shipment and delivery system and close delivered
 * orders.
 * <p>
 * Delivered orders are weekly deleted from database.
 * 
 * @see OrderSchedulerService
 * 
 * @author Michal Michaluk <michaluk.michal@gmail.com>
 */
@Stateless(mappedName="orderProgressManagement")
@LocalBean
public class OrderProgressManagementService implements OrderProgressManagementRemote {
    
    @EJB OrderRepository orderRepository;
    
    @Override
    public void orderDone(OrderKey orderKey) {
        try {
            OrderEntity order = orderRepository.findOrder(orderKey);
            // e.g. interact with delivery subsystem:
            // deliveryService.printShipmentLabel(orderKey, operatorPrinter)
            // deliveryService.orderReadyToShipment(orderKey)
            order.markAsProcessed();
        } catch (NotFoundException e) {
            // if order could by deleted in meanwhile and should not by
            // processed start emergency procedures e.g. send email to manager
        }
    }
    
    public void confirmationOfOrderDelivery(@Observes OrderDeliveredEvent orderDeliveredEvent) {
        OrderKey orderKey = orderDeliveredEvent.getOrderKey();
        try {
            OrderEntity order = orderRepository.findOrder(orderKey);
            order.markAsClosed();
        } catch (NotFoundException e) {
            // delivery of not managed order
        }
    }
    
    @Schedule(dayOfWeek = "Sat", hour = "22")
    public void deleteClosedOrders() {
        orderRepository.deleteClosedOrders();
    }
}
