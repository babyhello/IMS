package com.example.yujhaochen.ims;

/**
 * Created by yujhaochen on 2016/10/21.
 */
public class Project_Item {

    String Image;

    String Name;

    String ModelID;

    String CloseRate;

    String FocusType;

    String Read;

    public Project_Item(String ModelID,String Name,String Image,String CloseRate,String FocusType,String Read)
    {
        this.Image = Image;

        this.ModelID = ModelID;

        this.Name = Name;

        this.CloseRate = CloseRate;

        this.FocusType = FocusType;

        this.Read = Read;

    }

    public String GetImage()
    {
        return this.Image;
    }

    public String GetName()
    {
        return this.Name;
    }

    public String GetModelID()
    {
        return this.ModelID;
    }

    public String GetCloseRate()
    {
        return this.CloseRate;
    }

    public String GetFocusType()
    {
        return this.FocusType;
    }

    public String GetRead()
    {
        return this.Read;
    }
}
