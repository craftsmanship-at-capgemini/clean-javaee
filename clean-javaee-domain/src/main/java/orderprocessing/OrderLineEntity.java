package orderprocessing;

import inventory.ItemKey;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * 
 * @author Michal Michaluk <michaluk.michal@gmail.com>
 */
@Entity
@Table(name = "OrderLines")
public class OrderLineEntity implements Serializable {
    
    private static final long serialVersionUID = 6618112548176628801L;
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE) protected long id;
    
    @ManyToOne protected OrderEntity order;
    @Embedded @NotNull @Valid protected ItemKey itemKey;
    @Column(nullable = false) protected int quantity;
    
    protected OrderLineEntity(OrderEntity order, ItemKey itemKey, int quantity) {
        this.order = order;
        this.itemKey = itemKey;
        this.quantity = quantity;
    }
    
    protected OrderLineEntity() {
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("OrderLineEntity [itemKey=");
        builder.append(itemKey);
        builder.append(", quantity=");
        builder.append(quantity);
        builder.append("]");
        return builder.toString();
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (itemKey == null ? 0 : itemKey.hashCode());
        result = prime * result + quantity;
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        OrderLineEntity other = (OrderLineEntity) obj;
        if (itemKey == null) {
            if (other.itemKey != null) {
                return false;
            }
        } else if (!itemKey.equals(other.itemKey)) {
            return false;
        }
        if (quantity != other.quantity) {
            return false;
        }
        return true;
    }
    
}
