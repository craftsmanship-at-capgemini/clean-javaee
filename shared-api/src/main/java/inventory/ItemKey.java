package inventory;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.Size;

/**
 * 
 * @author Michal Michaluk <michaluk.michal@gmail.com>
 */
@Embeddable
public class ItemKey implements Serializable {
    
    private static final long serialVersionUID = -3565100156471149574L;
    
    @Size(max = 100) @Column(length = 100) private String itemKey;
    
    public ItemKey(String itemKey) {
        this.itemKey = itemKey;
    }
    
    protected ItemKey() {
    }
    
    public String getItemKey() {
        return itemKey;
    }
    
    public boolean isLike(String regexp) {
        return itemKey.matches(regexp);
    }
    
    protected void setItemKey(String itemKey) {
        this.itemKey = itemKey;
    }
    
    @Override
    public String toString() {
        return "" + itemKey;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (itemKey == null ? 0 : itemKey.hashCode());
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
        ItemKey other = (ItemKey) obj;
        if (itemKey == null) {
            if (other.itemKey != null) {
                return false;
            }
        } else if (!itemKey.equals(other.itemKey)) {
            return false;
        }
        return true;
    }
    
}
