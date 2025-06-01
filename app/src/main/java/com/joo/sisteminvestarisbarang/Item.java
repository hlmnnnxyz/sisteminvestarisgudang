package com.joo.sisteminvestarisbarang;

public class Item {
    private int id;
    private String name;
    private int type; // id kategori
    private String typeName; // nama kategori
    private String location;
    private int quantity;

    public Item(int id, String name, int type, String typeName, String location, int quantity) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.typeName = typeName;
        this.location = location;
        this.quantity = quantity;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public int getType() { return type; }
    public String getTypeName() { return typeName; }
    public String getLocation() { return location; }
    public int getQuantity() { return quantity; }

    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setType(int type) { this.type = type; }
    public void setTypeName(String typeName) { this.typeName = typeName; }
    public void setLocation(String location) { this.location = location; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
