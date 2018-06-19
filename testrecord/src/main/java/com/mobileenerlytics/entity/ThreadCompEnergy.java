package com.mobileenerlytics.entity;

import java.util.Date;

public class ThreadCompEnergy {
    Date date;
    String threadId;
    double energy;
    String componentId;

    public ThreadCompEnergy(String threadId, String componentId, double energy, Date date) {
        // Mongo can't handle "." in the keys, replace the "." with "_"
        // https://stackoverflow.com/questions/30014243/mongoerror-the-dotted-field-is-not-valid-for-storage
        this.threadId = threadId.replace('.', '_');
        this.energy = energy;
        this.componentId = componentId;
        this.date = date;
    }

    public ThreadCompEnergy() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ThreadCompEnergy that = (ThreadCompEnergy) o;

        if (Double.compare(that.energy, energy) != 0) return false;
        if (date != null ? !date.equals(that.date) : that.date != null) return false;
        if (threadId != null ? !threadId.equals(that.threadId) : that.threadId != null) return false;
        return componentId != null ? componentId.equals(that.componentId) : that.componentId == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = date != null ? date.hashCode() : 0;
        result = 31 * result + (threadId != null ? threadId.hashCode() : 0);
        temp = Double.doubleToLongBits(energy);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (componentId != null ? componentId.hashCode() : 0);
        return result;
    }
}
