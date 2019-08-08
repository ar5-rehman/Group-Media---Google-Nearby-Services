package com.example.groupmedia.modelClass;

public class ModelDataClass {
    String title;
    String path;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    String type;

    public ModelDataClass()
    {

    }


    public ModelDataClass(String title,String path,String type)
    {
        this.title = title;
        this.path = path;
        this.type = type;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}

