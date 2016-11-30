package com.example.yujhaochen.ims;

/**
 * Created by yujhaochen on 2016/10/21.
 */
public class Member_Item {

    String Title;

    String Name;

    String Image;

    String Phone;

    String WorkID;

    Boolean Section;

    public Member_Item(String Title,String Name,String Image,String Phone,String WorkID,Boolean Section)
    {
        this.Title = Title;

        this.Name = Name;

        this.Image = Image;

        this.Phone = Phone;

        this.Section = Section;

        this.WorkID = WorkID;
    }

    public String GetTitle()
    {
        return this.Title;
    }

    public String GetName()
    {
        return this.Name;
    }

    public String GetImage()
    {
        return this.Image;
    }

    public String GetPhone()
    {
        return this.Phone;
    }

    public String GetWorkID()
    {
        return this.WorkID;
    }

    public Boolean GetSection()
    {
        return this.Section;
    }
}
