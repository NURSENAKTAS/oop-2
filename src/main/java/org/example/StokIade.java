package org.example;

import org.bson.types.ObjectId;

public class StokIade {
    private ObjectId id;
    private String si_not;
    private ObjectId urun_id;

    public StokIade(){}
    public StokIade(ObjectId id, String si_not, ObjectId urun_id) {
        this.id = id;
        this.si_not = si_not;
        this.urun_id = urun_id;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getSi_not() {
        return si_not;
    }

    public void setSi_not(String si_not) {
        this.si_not = si_not;
    }

    public ObjectId getUrun_id() {
        return urun_id;
    }

    public void setUrun_id(ObjectId urun_id) {
        this.urun_id = urun_id;
    }
}
