package com.rasa.pilotreceiver;

import jakarta.servlet.http.HttpServlet;

public class PilotConnector extends HttpServlet {
    String size;
    boolean row;

    // In order for Spring to serialize objects, we need
    // to define getter and setter methods for each attribute
    public String getSize() {
        return this.size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public void updateRow(boolean row) {
        this.row = row;
    }

    // we will use the toString method in later examples
    public String toString() {
        return "[" + size + "," + Boolean.toString(row) + "]";
    }
}
