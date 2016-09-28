
package messaging.mqtt.android.common.model;

import java.io.Serializable;


public abstract class BaseInfo implements Serializable {

    public Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
